package edu.sabien.aaronzhao.doctorhelper.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.glass.media.Sounds;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Lazy;
import edu.sabien.aaronzhao.doctorhelper.GlassApp;
import edu.sabien.aaronzhao.doctorhelper.R;
import edu.sabien.aaronzhao.doctorhelper.components.AuthComponent;
import edu.sabien.aaronzhao.doctorhelper.components.RESTComponent;
import edu.sabien.aaronzhao.doctorhelper.domain.model.DataModule;
import edu.sabien.aaronzhao.doctorhelper.rest.RESTClient;
import edu.sabien.aaronzhao.doctorhelper.rest.RESTModule;
import edu.sabien.aaronzhao.doctorhelper.rest.ToStringConverterFactory;
import edu.sabien.aaronzhao.doctorhelper.tcp.TCPClient;
import edu.sabien.aaronzhao.doctorhelper.utils.Utils;
import edu.sabien.aaronzhao.doctorhelper.view.AlertDialog;
import edu.sabien.aaronzhao.doctorhelper.view.BaseActivity;
import edu.sabien.aaronzhao.doctorhelper.view.MainActivity;
import retrofit2.Call;

/**
 * Created by AaronZhao on 2/28/16.
 */
public class Choreographer implements Runnable{

    private static final String TAG = "Choreographer";
    private static boolean authInit = false;
    private boolean chStatus = false; //default not connected
    private boolean authStatus;  //auth status for two different types of cgh, cgh of two types be allowed for different things
    private static String authSession = null;
    private AuthComponent mAuthComponent = null;
    private RESTComponent mRESTComponent = null;
    private AlertDialog chDialog;
    private static UUID uuid;
    private static String readyString = null;    //transmitting auth string

    private static Handler cghHandler;
    private static Handler restHandler;
    private BaseActivity mActivity;

    public static final int AUTH = 5;
    public static final int REFRESH = 0;
    public static final int CGHLOST = 1;
    public static final int CH_ON = 1;
    public static final int CH_OFF = 0;

    @Inject @Named("non_cached") Lazy<AuthModule.AuthInterface> authInterfaceLazy;
    @Inject Lazy<RESTClient> restClientLazy;
    @Inject Lazy<TCPClient> tcpClientLazy;

    public RESTClient getRESTClient(){
        return this.restClientLazy.get();
    }
    public RESTComponent getRESTComponent() {
        return mRESTComponent;
    }

    public boolean getChStatus(){ //return the current Choreographer connectivity status
        return this.chStatus;
    }
    public void setChStatus(boolean status){
        this.chStatus = status;
    }
    public boolean isAuthStatus() {
        return authStatus;
    }
    public void setAuthStatus(boolean status){
        this.authStatus = status;
    }
    public void setAuthSession(String authSession) {
        this.authSession = authSession;
    }

    public static Handler getRestHandler(){
        return restHandler;
    }

    public Choreographer(BaseActivity activity, boolean authStatus){
        mActivity = activity;
        this.authStatus = authStatus;
        mAuthComponent = activity.getNetworkComponent().plus(
                new AuthModule("http://158.42.166.132:8000/", new ToStringConverterFactory()),
                new ClientModule(mActivity, this));
        mAuthComponent.inject(this);
        if (this.authStatus){  //authorized
            mRESTComponent = mAuthComponent.plus(new RESTModule((GlassApp) mActivity.getApplication()), new DataModule(mActivity));
        }
    }

    public void onDestroy(){
        mAuthComponent = null;
        mRESTComponent = null;
    }

    @Override
    public void run(){
        Looper.prepare();
        if (isAuthStatus()){  //authorized
            authorizedRun();
        }else{
            onAuth();  //to be authorized
        }
        Looper.loop();
    }

    public void authorizedRun(){  //invokes when auth passes
        restHandler = new Handler();
        restClientLazy.get().setSessionToken(authSession);
        restClientLazy.get().run();
        if (mActivity.getLocalClassName().equals("view.MsgActivity"))
            tcpClientLazy.get().run();
    }

    /*
    * proactive method which could be invoked outside of this class*/
    public void onAuth(){ //does work when auth begins, auth components will be available
        cghHandler = new Handler();
        String jsonString = Utils.readFromJsonFileToString("auth", mActivity);
        UUID temp = UUID.randomUUID();
        while (temp == uuid){
            temp = UUID.randomUUID();
        }
        uuid = temp;
        try{
            readyString = jsonString.replace("UUIDToBeReplaced",uuid.toString()).replace("IPToBeReplaced", Utils.getCurrentIP());
        }catch (NullPointerException ex){
            Log.e(TAG, "Glass IP address not obtained!");
            ex.printStackTrace();
        }
        Runnable authRunnable = new Runnable() {
            @Override
            public void run() {   //only invokes when auth component is injected
                try{
                    //Log.d(TAG, readyString);
                    Call<String> authSession = authInterfaceLazy.get().auth(readyString);
                    authSession.enqueue(new AuthCallback(Choreographer.this));
                }catch (NullPointerException ex){
                    ex.printStackTrace();
                }
            }
        };
        cghHandler.post(authRunnable);
    }

    public void onDisconnect(){  //does work when wifi's disconnected
        String activityType = mActivity.getLocalClassName();
        if (activityType.equals("view.MainActivity")){
            //cghHandler.removeCallbacks(mainRunnable);
        }else if (activityType.equals("view.MsgActivity")){
            //cghHandler.removeCallbacks(msgRunnable);
            tcpClientLazy.get().onDisconnect();
        }
    }


    public void notifyMsgRefresh(int status){
        if (status == CGHLOST)
            setChStatus(false);
    }

    public void notifyCghRefresh(int status){
        if (status == Choreographer.CGHLOST){
            ((MainActivity)mActivity).setChText(Choreographer.CH_OFF);
            if (getChDialog() == null)
                popupCghAlert();
        }else{
            Message msg = Message.obtain();
            msg.what = status;
            msg.obj = null;
            MainActivity.getDeviceListHandler().sendMessage(msg);
        }
    }

    public void popupCghAlert(){  //pop up alert when wifi's connected but ch is not, and when ch connection is lost during transmission
        if (chDialog == null){
            chDialog = new AlertDialog(mActivity, R.drawable.ic_phonelink_off, R.string.chNotConnected,
                    R.string.chNotConnectedFootnote,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            dialog.cancel();
                        }
                    }, new DialogInterface.OnDismissListener(){
                @Override
                public void onDismiss(DialogInterface dialogInterface){
                    dialogInterface.dismiss();
                }
            }
            );
            chDialog.show();
            AudioManager audioManager = (AudioManager)mActivity.getSystemService(Context.AUDIO_SERVICE);
            audioManager.playSoundEffect(Sounds.ERROR);
        }
    }

    public AlertDialog getChDialog() {
        return chDialog;
    }

}
