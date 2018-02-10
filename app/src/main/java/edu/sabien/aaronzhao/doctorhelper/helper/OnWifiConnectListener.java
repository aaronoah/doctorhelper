package edu.sabien.aaronzhao.doctorhelper.helper;

/**
 * Created by AaronZhao on 2/16/16.
 */
public interface OnWifiConnectListener { //TODO: wifi connects, clear the screen, disconnect, can show history
    boolean isWifiConnected(); //returns the status of wifi
    void popupAlert(String alertType); //popup menu redirecting for network settings activity
}
