package edu.sabien.aaronzhao.doctorhelper.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import dagger.Lazy;
import edu.sabien.aaronzhao.doctorhelper.GlassApp;
import edu.sabien.aaronzhao.doctorhelper.components.MainActivityComponent;
import edu.sabien.aaronzhao.doctorhelper.presenter.MainViewModule;
import edu.sabien.aaronzhao.doctorhelper.helper.Choreographer;
import edu.sabien.aaronzhao.doctorhelper.helper.NetworkModule;
import edu.sabien.aaronzhao.doctorhelper.utils.GestureModule;
import edu.sabien.aaronzhao.doctorhelper.domain.model.Device;
import edu.sabien.aaronzhao.doctorhelper.domain.model.DeviceList;
import edu.sabien.aaronzhao.doctorhelper.presenter.MainPresenter;
import edu.sabien.aaronzhao.doctorhelper.R;

/**
 * @author AaronZhao
 */
public class MainActivity extends BaseActivity implements MainView{  //MainView: consists of PaneView and MsgView

    private MainActivityComponent mainActivityComponent;

    private String TAG = "MainActivity";
    private boolean flag = false;
    private static int msgType;
    private AlertDialog dialog;
    private Unbinder unbinder;

    private static final int LAUNCH_MSG_VIEW = Menu.FIRST;
    private static final int CHECK_WIFI = Menu.FIRST + 1;
    private static final int LAUNCH_MSG_DEVICE = 0;
    private static final int LAUNCH_MSG_DEVICES = 1;
    public static final int ONLINE = 1;
    public static final int OFFLINE = 0;

    @BindView(R.id.main_list) ListView listView;
    @BindView(R.id.progressbar_loading) ProgressBar progressBar;

    @BindString(R.string.chOn) String chOnString;
    @BindString(R.string.chOff) String chOffString;

    @BindString(R.string.open_online_msg_view) String open_online_msg_view;
    @BindString(R.string.open_offline_msg_view) String open_offline_msg_view;

    @Inject MainListAdapter mainListAdapter;
    @Inject Lazy<MainPresenter> mainPresenterLazy;
    @Inject @Named("Main") GestureDetector gestureDetector;
    @Inject AudioManager audioManager;

    private static DeviceListHandler deviceListHandler;
    public static Handler getDeviceListHandler() {
        return deviceListHandler;
    }

    private static class DeviceListHandler extends Handler{
        private final MainActivity mMainActivity;
        public DeviceListHandler(MainActivity mainActivity){
            mMainActivity = mainActivity;
        }
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if (msg.what == Choreographer.REFRESH){
                mMainActivity.setChText(Choreographer.CH_ON);
                mMainActivity.updateListUI("device_list_wait");
            }else if (msg.what == Choreographer.AUTH){
                mMainActivity.setChAuth();
            }else if (msg.what == MainListAdapter.ADD_DEVICE){
                mMainActivity.updateListUI("get_device_list");
                mMainActivity.mainListAdapter.refresh((Device)msg.obj, msg.what);
                mMainActivity.listView.post(new Runnable() {
                    @Override
                    public void run() {
                        mMainActivity.listView.setSelection(0);
                    }
                });
                if (mMainActivity.dialog == null)
                    mMainActivity.showMsgView(ONLINE);
            }else if (msg.what == MainListAdapter.NO_UPDATE){
                mMainActivity.updateListUI("get_device_list");
                mMainActivity.mainListAdapter.refresh((Device)msg.obj, msg.what);
            }
            else if (msg.what == MainListAdapter.NO_DEVICE){
                mMainActivity.updateListUI("no_device_list");
                mMainActivity.mainListAdapter.refresh((Device)msg.obj, msg.what);
                if (mMainActivity.dialog == null)
                    mMainActivity.showMsgView(OFFLINE);
            }
        }
    }

    public void updateListUI(String which){
        progressBar.setIndeterminate(true);
        if (which.equals("device_list_wait")){
            listView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else if (which.equals("get_device_list") || which.equals("no_device_list")){
            progressBar.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
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
        flag = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        unbinder = ButterKnife.bind(this); //only after binding views can refs work, don't move this line!

        mainActivityComponent =
                ((GlassApp)getApplication()).getAppComponent().plus(
                        new MainViewModule(this),
                        new GestureModule(this));
        mainActivityComponent.inject(this);
        networkComponent = mainActivityComponent.plus(new NetworkModule(this));

        Log.d(TAG, "MainActivity launches!");

        if(listView != null) {
            View chHeaderView = getLayoutInflater().inflate(R.layout.choreographer_header, listView, false);
            listView.addHeaderView(chHeaderView);
            this.mainListAdapter = new MainListAdapter(this, new DeviceList());
            listView.setAdapter(mainListAdapter);
            listView.setClickable(true);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
        Log.d(TAG, "Device listview ready!");

        this.deviceListHandler = new DeviceListHandler(this);
        mainPresenterLazy.get();
    }


    @Override
    public void onStart(){
        mainPresenterLazy.get().onStart();
        super.onStart();
    }

    public void onGestureTwoTap(){
        listView.performItemClick(
                listView.getAdapter().getView(listView.getSelectedItemPosition(), null, null),
                listView.getSelectedItemPosition(),
                listView.getAdapter().getItemId(listView.getCheckedItemPosition()));
    }

    @OnItemClick(R.id.main_list) void onItemClick(int position){
        launchMessageView(msgType, LAUNCH_MSG_DEVICE, position);
    }

    @Override
    public void openOptionsMenu(){
        super.openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem menuItem = menu.findItem(R.id.open_msg_view);
        if (msgType == ONLINE){
            menuItem.setTitle(open_online_msg_view);
        }else if (msgType == OFFLINE){
            menuItem.setTitle(open_offline_msg_view);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case LAUNCH_MSG_VIEW:
                Log.d(TAG, "local!");
                launchMessageView(msgType, LAUNCH_MSG_DEVICES);
                break;
            case CHECK_WIFI:
                checkWifi();
                break;
        }
        return true;
    }

    private void checkWifi(){
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onResume(){
        if (!flag) {//called upon without oncreate() beforehand
            mainPresenterLazy.get().onResume();
        }
        flag = false;
        Log.d(TAG, "MainActivity resumed!");
        super.onResume();
    }

    @Override
    protected void onPause(){
        mainPresenterLazy.get().onPause();
        Log.d(TAG, "MainActivity pause!");
        super.onPause();
    }

    @Override
    public void onStop(){
        mainPresenterLazy.get().onStop();
        Log.d(TAG, "onStop called!");
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        mainPresenterLazy.get().onDestroy();
        mainPresenterLazy = null;  //destroying the object graphs and then the component
        gestureDetector = null;
        mainActivityComponent = null;
        unbinder.unbind();
        Log.d(TAG, "MainActivity destroy!");
        super.onDestroy();
    }

    public void setChText(int state){
        if (state == Choreographer.CH_ON)
            ((TextView)findViewById(R.id.choreographer)).setText(chOnString);
        else if (state == Choreographer.CH_OFF)
            ((TextView)findViewById(R.id.choreographer)).setText(chOffString);
    }

    public void setChAuth(){
        ((TextView)findViewById(R.id.choreographer)).setTextColor(Color.GREEN);
    }

    private void launchMessageView(int msgType, int... args){  //Note: never calls the method with more than 2 args for optional arguments
        Intent intent = new Intent(MainActivity.this, MsgActivity.class);
        intent.putExtra("MESSAGE_TYPE", msgType); //the MsgView will wait for ch connection online
        if (msgType == ONLINE) {
            for (int i=0;i<2;i++){
                if (args[i] == LAUNCH_MSG_DEVICES) {
                    String listSerializedToJson = new Gson().toJson(mainListAdapter.getDeviceList());
                    intent.putExtra("DEVICE_LIST", listSerializedToJson);
                } else if (args[i] == LAUNCH_MSG_DEVICE) {
                    intent.putExtra("DEVICE", mainListAdapter.getItem(args[++i]));
                }
            }
        }
        startActivity(intent);
    }

    @Override
    public void showMsgView(final int msgType){ //user decides whether to show the view or not, intuitively false and true
        MainActivity.this.msgType = msgType;
        final DialogInterface.OnClickListener mOnClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        launchMessageView(msgType, LAUNCH_MSG_DEVICES);
                    }
                };
        final  DialogInterface.OnDismissListener onDismissListener =
                new DialogInterface.OnDismissListener(){
                    @Override
                    public void onDismiss(DialogInterface dialogInterface){
                        dialogInterface.dismiss();
                    }
                };
        if (msgType == ONLINE) {
            if (dialog == null){
                dialog = new AlertDialog(this, R.drawable.ic_check_circle, R.string.connectionSet,
                        R.string.connectionSetFootnote, mOnClickListener, onDismissListener);
                dialog.show();
                audioManager.playSoundEffect(Sounds.SUCCESS);
            }
        }else if (msgType == OFFLINE){
            if (dialog == null){
                dialog = new AlertDialog(this, R.drawable.ic_portable_wifi_off, R.string.connectionLost,
                        R.string.connectionLostFootnote, mOnClickListener, onDismissListener);
                dialog.show();
                audioManager.playSoundEffect(Sounds.ERROR);
            }
        }
    }

    public void scrollUpList(){  //one item per swipe, (backward swipe)
        //Log.d(TAG, "scroll up!");
        listView.clearFocus();
        listView.post(new Runnable() {
            @Override
            public void run() {
                int pos = listView.getSelectedItemPosition()-1;
                listView.requestFocusFromTouch();
                listView.setSelection(pos);
                listView.requestFocus();
                mainListAdapter.setSelectItem(pos);
                mainListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void scrollDownList(){   //one item per swipe, (forward swipe)
        //Log.d(TAG, "scroll down");
        listView.clearFocus();
        listView.post(new Runnable() {
            @Override
            public void run() {
                int pos = listView.getSelectedItemPosition()+1;
                listView.requestFocusFromTouch();
                listView.setSelection(pos);
                listView.requestFocus();
                mainListAdapter.setSelectItem(pos);
                mainListAdapter.notifyDataSetChanged();
            }
        });
    }

}
