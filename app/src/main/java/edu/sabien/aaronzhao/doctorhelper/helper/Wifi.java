package edu.sabien.aaronzhao.doctorhelper.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import com.google.android.glass.media.Sounds;

import java.io.IOException;

import edu.sabien.aaronzhao.doctorhelper.R;
import edu.sabien.aaronzhao.doctorhelper.view.AlertDialog;

/**
 * Created by AaronZhao on 2/28/16.
 */
public class Wifi implements OnWifiConnectListener {

    private Context mContext;
    private WifiManager wifiManager;
    private AlertDialog wifiDialog = null;

    public Wifi(Context context){
        mContext = context;
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public boolean isWifiConnected(){  //in this stage, only preliminary connectivity can be verified, actual availability needs ping tool
        if (mContext != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
            return (mWiFiNetworkInfo != null && mWiFiNetworkInfo.isConnected());
        }
        return false;
    }

    public boolean isWifiAccessible() { //using ping system networking tool to verify the actual availability of transmission
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 1 www.google.com");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void popupAlert(String alertType){  //pop up alert when open app and check with no wifi, and when connection lost during transmission
        if (wifiDialog == null){
            final DialogInterface.OnClickListener mOnClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            // Open WiFi Settings
                            mContext.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    };
            final DialogInterface.OnDismissListener onDismissListener =
                    new DialogInterface.OnDismissListener(){
                        @Override
                        public void onDismiss(DialogInterface dialogInterface){
                            dialogInterface.dismiss();
                        }
                    };
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if (alertType == "wifi_disconnected"){
                wifiDialog = new AlertDialog(mContext, R.drawable.ic_portable_wifi_off, R.string.wifiNotConnected,
                        R.string.wifiNotConnectedFootnote, mOnClickListener, onDismissListener);
                wifiDialog.show();
                audioManager.playSoundEffect(Sounds.ERROR);
            }
            else if (alertType == "wifi_inaccessible"){
                wifiDialog = new AlertDialog(mContext, R.drawable.ic_portable_wifi_off, R.string.wifiInaccessible,
                        R.string.wifiInaccessibleFootnote, mOnClickListener, onDismissListener);
                wifiDialog.show();
                audioManager.playSoundEffect(Sounds.ERROR);
            }
        }
    }
}
