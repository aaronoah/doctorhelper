package edu.sabien.aaronzhao.doctorhelper.view;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.GestureDetector;

import javax.inject.Inject;
import javax.inject.Named;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.Lazy;
import edu.sabien.aaronzhao.doctorhelper.GlassApp;
import edu.sabien.aaronzhao.doctorhelper.R;
import edu.sabien.aaronzhao.doctorhelper.components.MsgActivityComponent;
import edu.sabien.aaronzhao.doctorhelper.presenter.MsgViewModule;
import edu.sabien.aaronzhao.doctorhelper.helper.NetworkModule;
import edu.sabien.aaronzhao.doctorhelper.utils.GestureModule;
import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageGram;
import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageList;
import edu.sabien.aaronzhao.doctorhelper.presenter.MsgPresenter;

public class MsgActivity extends BaseActivity implements MsgView{

    public static final int DEVICE_LIST = 1;
    public static final int DEVICE = 0;
    public static final int REFRESH = -5;
    public static final int CGHLOST = -2;
    public static final int ONLINE = 1;
    public static final int OFFLINE = 0;
    private Unbinder unbinder;
    private AlertDialog dialog;

    @Inject MsgAdapter msgAdapter;
    @Inject @Named("online") Lazy<MsgPresenter> onlineMsgPresenter;
    @Inject @Named("offline") Lazy<MsgPresenter> offlineMsgPresenter;
    @Inject @Named("Msg") GestureDetector gestureDetector;

    @BindView(R.id.message_list) ListView msgList;
    @BindView(R.id.msg_progressbar_loading) ProgressBar progressBar;

    private MsgActivityComponent msgActivityComponent;
    private static final String TAG = "MsgActivity";
    private boolean flag = false;
    private int messageType;
    private static MsgHandler msgHandler;

    private static class MsgHandler extends Handler{
        private final MsgActivity msgActivity;
        public MsgHandler(MsgActivity msgActivity){
            this.msgActivity = msgActivity;
        }
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if (msg.what == REFRESH){
                msgActivity.updateListUI("message_list_wait");
            }else if(msg.what == CGHLOST){
                if (msgActivity.messageType == OFFLINE)
                    msgActivity.onlineMsgPresenter.get().getCgh().popupCghAlert();
            }else if (msg.what == MsgAdapter.NO_MESSAGE){
                msgActivity.updateListUI("no_message_list");
                msgActivity.msgAdapter.refresh((MessageGram)msg.obj, msg.what);
                //msgActivity.showMsgView(OFFLINE);
            }else if (msg.what == MsgAdapter.ADD_MESSAGE){
                Log.d(TAG, "IN ADD MESSAGE!");
                msgActivity.updateListUI("get_message");
                msgActivity.msgAdapter.refresh((MessageGram)msg.obj, msg.what);
            }
        }
    }

    public static Handler getMsgHandler() {
        return msgHandler;
    }

    public MsgAdapter getMsgAdapter() {
        return msgAdapter;
    }

    public void updateListUI(String which){
        progressBar.setIndeterminate(true);
        if (which == "message_list_wait"){
            msgList.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else if (which == "get_message_list" || which == "no_message_list" || which == "get_message"){
            progressBar.setVisibility(View.INVISIBLE);
            msgList.setVisibility(View.VISIBLE);
        }
    }

    /*
         * Send generic motion events to the gesture detector
         */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (gestureDetector != null) {
            return gestureDetector.onMotionEvent(event);
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_view);
        unbinder = ButterKnife.bind(this);

        progressBar.setVisibility(View.INVISIBLE);

        msgActivityComponent = ((GlassApp) getApplication()).getAppComponent().plus(
                new MsgViewModule(this),
                new GestureModule(this));
        msgActivityComponent.inject(this);
        networkComponent = msgActivityComponent.plus(new NetworkModule(this));

        Log.d(TAG, "MsgActivity created!");

        if (msgList != null) {
            this.msgAdapter = new MsgAdapter(this, new MessageList());
            msgList.setAdapter(msgAdapter);
            msgList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
        this.msgHandler = new MsgHandler(this);

    }

    public MsgActivityComponent getMsgActivityComponent() {
        return msgActivityComponent;
    }

    @Override
    public void openOptionsMenu(){
        super.openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_msg, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        return true;
    }

    @Override
    public void onStart(){
        Bundle extras = getIntent().getExtras();
        if(extras.getInt("MESSAGE_TYPE") == ONLINE){   //ONLINE means cgh is authorized
            this.messageType = ONLINE;
            if (getIntent().getSerializableExtra("DEVICE") != null){
                onlineMsgPresenter.get().setData(getIntent().getSerializableExtra("DEVICE"), DEVICE);
            }else if (getIntent().getSerializableExtra("DEVICE_LIST") != null){
                onlineMsgPresenter.get().setData(getIntent().getSerializableExtra("DEVICE_LIST"), DEVICE_LIST);
            }
            onlineMsgPresenter.get().operateOnline();
        }else if (extras.getInt("MESSAGE_TYPE") == OFFLINE) {
            this.messageType = OFFLINE;
            offlineMsgPresenter.get().operateOffline();
        }
        super.onStart();
    }

    @Override
    public void onPause(){
        if(onlineMsgPresenter != null)
            onlineMsgPresenter.get().onPause();
        else if (offlineMsgPresenter != null)
            offlineMsgPresenter.get().onPause();
        Log.d(TAG, "MsgActivity pause!");
        super.onPause();
    }

    @Override
    public void onResume(){
        if (!flag) {//called upon without oncreate() beforehand
            if(onlineMsgPresenter != null)
                onlineMsgPresenter.get().onResume();
            else if (offlineMsgPresenter != null)
                offlineMsgPresenter.get().onResume();
        }
        flag = false;
        Log.d(TAG, "MsgActivity resumed!");
        super.onResume();
    }

    @Override
    public void onStop(){
        if(onlineMsgPresenter != null)
            onlineMsgPresenter.get().onStop();
        else if (offlineMsgPresenter != null)
            offlineMsgPresenter.get().onStop();
        Log.d(TAG, "MsgActivity stop!");
        super.onStop();;
    }

    @Override
    public void onDestroy(){
        if(onlineMsgPresenter != null)
            onlineMsgPresenter.get().onDestroy();
        else if (offlineMsgPresenter != null)
            offlineMsgPresenter.get().onDestroy();
        msgActivityComponent = null;
        unbinder.unbind();
        Log.d(TAG, "MsgActivity destroy!");
        super.onDestroy();
    }


    @Override
    public void showMsgView(int msgType){ //user decides whether to show the view or not, intuitively false and true
        if (dialog == null){
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            final DialogInterface.OnClickListener mOnClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            MsgActivity.this.finish();
                        }
                    };
            final  DialogInterface.OnDismissListener onDismissListener =
                    new DialogInterface.OnDismissListener(){
                        @Override
                        public void onDismiss(DialogInterface dialogInterface){
                            dialogInterface.dismiss();
                        }
                    };
            dialog = new AlertDialog(this, R.drawable.ic_portable_wifi_off, R.string.connectionLost,
                    R.string.connectionLostFootnote, mOnClickListener, onDismissListener);
            dialog.show();
            audioManager.playSoundEffect(Sounds.ERROR);
        }
    }

    public void scrollUpList(){  //one item per swipe, (backward swipe)
        //Log.d(TAG, "scroll up!");
        msgList.clearFocus();
        msgList.post(new Runnable() {
            @Override
            public void run() {
                int pos = msgList.getSelectedItemPosition()-1;
                msgList.setSelection(pos);
                msgAdapter.setSelectItem(pos);
                msgAdapter.notifyDataSetChanged();
            }
        });
    }

    public void scrollDownList(){   //one item per swipe, (forward swipe)
        //Log.d(TAG, "scroll down");
        msgList.clearFocus();
        msgList.post(new Runnable() {
            @Override
            public void run() {
                int pos = msgList.getSelectedItemPosition()+1;
                msgList.setSelection(pos);
                msgAdapter.setSelectItem(pos);
                msgAdapter.notifyDataSetChanged();
            }
        });
    }

}
