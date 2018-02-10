package edu.sabien.aaronzhao.doctorhelper.presenter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.Lazy;
import edu.sabien.aaronzhao.doctorhelper.R;
import edu.sabien.aaronzhao.doctorhelper.components.DbComponent;
import edu.sabien.aaronzhao.doctorhelper.domain.dao.DaoModule;
import edu.sabien.aaronzhao.doctorhelper.helper.Choreographer;
import edu.sabien.aaronzhao.doctorhelper.helper.Wifi;
import edu.sabien.aaronzhao.doctorhelper.domain.model.Device;
import edu.sabien.aaronzhao.doctorhelper.domain.model.DeviceList;
import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageList;
import edu.sabien.aaronzhao.doctorhelper.domain.dao.OfflineDao;
import edu.sabien.aaronzhao.doctorhelper.domain.dao.OnlineDao;
import edu.sabien.aaronzhao.doctorhelper.view.MainActivity;
import edu.sabien.aaronzhao.doctorhelper.view.MsgActivity;

/**
 * Created by AaronZhao on 2/28/16.
 */
public class MsgPresenterImpl implements MsgPresenter {

    private MsgActivity mMsgActivity;
    private DbComponent dbComponent;
    private Thread periodCheckThread;
    private static volatile MessageList messageList = null;
    private Thread _onlineDao, _offlineDao, cghThread;
    private String mMsgType;
    private static final String TAG = "MainPresenter";
    private Unbinder unbinder;

    @BindView(R.id.timestamp) TextView timestamp;

    @Inject Lazy<Wifi> wifiLazy;
    @Inject @Named("authorized") Lazy<Choreographer> authCghLazy;
    //@Inject @Named("unauthorized") Lazy<Choreographer> cghLazy;
    @Inject Lazy<OnlineDao> onlineDaoLazy;
    @Inject Lazy<OfflineDao> offlineDaoLazy;

    public MsgPresenterImpl(MsgActivity msgActivity, String msgType){
        this.mMsgType = msgType;
        this.mMsgActivity = msgActivity;
        dbComponent = mMsgActivity.getNetworkComponent().plus(new DaoModule(msgActivity));
        dbComponent.inject(this);
        unbinder = ButterKnife.bind(this, mMsgActivity);
        messageList = new MessageList();
    }

    @Override
    public void periodCheck(){
        periodCheckThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        setDateTime(new Date());
                        if (!wifiCheck()) {
                            authCghLazy.get().onDisconnect();
                            mMsgActivity.showMsgView(MainActivity.OFFLINE);
                        }
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.post(runnable);

                Looper.loop();
            }
        });
        periodCheckThread.start();
    }

    public void setData(Serializable data, int metadata) {
        if (metadata == MsgActivity.DEVICE_LIST)
            authCghLazy.get().getRESTClient().setDeviceList((DeviceList) data);
        else if (metadata == MsgActivity.DEVICE)
            authCghLazy.get().getRESTClient().setDevice((Device) data);
    }

    @Override
    public void onStart(){
        if (this.mMsgType.equals("online"))
            periodCheck();
    }

    public void operateOffline(){
        _offlineDao = new Thread(offlineDaoLazy.get());
        _offlineDao.start();
        Thread offlineManager = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (messageList.size() != 0){
                        Message msg = Message.obtain();
                        msg.what = mMsgActivity.getMsgAdapter().ADD_MESSAGE;
                        msg.obj = messageList.get(0);
                        MsgActivity.getMsgHandler().sendMessage(msg);
                        messageList.remove(0);
                    }
                }
            }
        });
        offlineManager.start();
    }

    public void operateOnline(){
        cghThread = new Thread(authCghLazy.get());
        _onlineDao = new Thread(onlineDaoLazy.get());
        cghThread.start();
        _onlineDao.start();
    }

    @Override
    public void onPause(){
        if (this.mMsgType.equals("online")){
        }else if (this.mMsgType.equals("offline")){

        }
    }

    @Override
    public void onResume(){
    }

    @Override
    public void onStop(){
        if(this.mMsgType.equals("online")){
            periodCheckThread = null;
            cghThread = null;
            _onlineDao = null;
        }else if (this.mMsgType.equals("offline")){
            _offlineDao = null;
        }
    }

    @Override
    public void onDestroy(){

    }

    public Choreographer getCgh() {
        return authCghLazy.get();
    }

    public static MessageList getMessageList(){
        return messageList;
    }


    public boolean wifiCheck(){
        if(wifiLazy.get().isWifiConnected()){
            if (wifiLazy.get().isWifiAccessible()){ //WIFI is accessible
                return true;
            }else{
                wifiLazy.get().popupAlert("wifi_inaccessible");
                return false;
            }
        }else{
            wifiLazy.get().popupAlert("wifi_disconnected");
            return false;
        }
    }

    public void setDateTime(final Date dateTime){
        final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a E");
        mMsgActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timestamp.setText(dateFormat.format(dateTime));
            }
        });
    }

}
