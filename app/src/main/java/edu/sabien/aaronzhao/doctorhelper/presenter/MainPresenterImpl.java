package edu.sabien.aaronzhao.doctorhelper.presenter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;
import edu.sabien.aaronzhao.doctorhelper.GlassApp;
import edu.sabien.aaronzhao.doctorhelper.R;
import edu.sabien.aaronzhao.doctorhelper.components.NetworkComponent;
import edu.sabien.aaronzhao.doctorhelper.helper.Choreographer;
import edu.sabien.aaronzhao.doctorhelper.helper.Wifi;
import edu.sabien.aaronzhao.doctorhelper.helper.NetworkModule;
import edu.sabien.aaronzhao.doctorhelper.view.MainActivity;

/**
 * Created by AaronZhao on 2/17/16.
 */
public class MainPresenterImpl implements MainPresenter{

    private Thread connectionThread, periodCheckThread;
    private MainActivity mMainActivity;
    private static boolean flag = false;
    private static final String TAG = "MainPresenter";

    @BindView(R.id.wifi_icon) ImageView wifiIcon;
    @BindView(R.id.wifi_text) TextView wifiText;
    @BindDrawable(R.drawable.ic_wifi_tethering) Drawable wifiOn;
    @BindString(R.string.wifiSignalOn) String wifiOnString;
    @BindDrawable(R.drawable.ic_portable_wifi_off) Drawable wifiOff;
    @BindString(R.string.wifiSignalOff) String wifiOffString;
    @BindView(R.id.main_timestamp) TextView timestamp;

    @Inject Wifi wifi;
    @Inject @Named("unauthorized") Lazy<Choreographer> cghRunnableLazy;
    @Inject @Named("authorized") Lazy<Choreographer> authCghRunnableLazy;

    private Runnable periodRunnable = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();

            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    setDateTime(new Date());
                    if (!wifiCheck()) {
                        connectionThread = null;
                        cghRunnableLazy.get().onDisconnect();
                        mMainActivity.showMsgView(MainActivity.OFFLINE);
                    }
                    if (cghRunnableLazy.get().isAuthStatus() && !flag){
                        Message msg = Message.obtain();
                        msg.what = Choreographer.AUTH;
                        msg.obj = null;
                        MainActivity.getDeviceListHandler().sendMessage(msg);
                        connectionThread = new Thread(authCghRunnableLazy.get());
                        connectionThread.start();
                        flag = true;
                    }
                    handler.postDelayed(this, 1000);
                }
            };
            handler.post(runnable);

            Looper.loop();
        }
    };

    public MainPresenterImpl(MainActivity mainActivity){ //passing the mainview context to here
        this.mMainActivity = mainActivity;
        ButterKnife.bind(this, mMainActivity);
        mMainActivity.getNetworkComponent().inject(this);
        setRotate(wifiIcon);
    }

    @Override
    public void periodCheck(){
        periodCheckThread = new Thread(periodRunnable);
        periodCheckThread.start();
    }

    public void setRotate(View view){
        RotateAnimation ra = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(1000);
        ra.setRepeatCount(Animation.INFINITE); //keeps the loop running
        view.startAnimation(ra);
    }

    @Override
    public void onStart(){
        periodCheck();
        connectionThread = new Thread(cghRunnableLazy.get());
        connectionThread.start();
    }

    @Override
    public void onResume(){
    }

    @Override
    public void onPause(){
    }

    @Override
    public void onStop(){
        periodCheckThread = null;
        connectionThread = null;
    }

    @Override
    public void onDestroy(){

    }

    public boolean wifiCheck(){
        if(wifi.isWifiConnected()){
            setWifiIcon("ON");
            setWifiText("ON");
            if (wifi.isWifiAccessible()){ //WIFI is accessible
                return true;
            }else{
                wifi.popupAlert("wifi_inaccessible");
                return false;
            }
        }else{
            setWifiIcon("OFF");
            setWifiText("OFF");
            wifi.popupAlert("wifi_disconnected");
            return false;
        }
    }

    public void setWifiIcon(final String state){
            mMainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    wifiIcon.clearAnimation();
                    if (state.equals("ON")){
                        wifiIcon.setImageDrawable(wifiOn);
                    }
                    else if (state.equals("OFF"))
                        wifiIcon.setImageDrawable(wifiOff);
                }
            });
    }
    public void setWifiText(final String state){
        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (state.equals("ON")){
                    wifiText.setText(wifiOnString);
                }
                else if (state.equals("OFF"))
                    wifiText.setText(wifiOffString);
            }
        });
    }

    public void setDateTime(final Date dateTime){
        final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a E");
        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timestamp.setText(dateFormat.format(dateTime));
            }
        });
    }

}
