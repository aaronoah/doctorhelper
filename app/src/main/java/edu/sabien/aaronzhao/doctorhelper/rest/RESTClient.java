package edu.sabien.aaronzhao.doctorhelper.rest;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Lazy;
import edu.sabien.aaronzhao.doctorhelper.components.RESTComponent;
import edu.sabien.aaronzhao.doctorhelper.domain.model.Device;
import edu.sabien.aaronzhao.doctorhelper.domain.model.DeviceList;
import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageGram;
import edu.sabien.aaronzhao.doctorhelper.helper.Choreographer;
import edu.sabien.aaronzhao.doctorhelper.view.BaseActivity;
import retrofit2.Call;

/**
 * Created by AaronZhao on 30/04/16.
 */
public class RESTClient {

    private static final String TAG = "RESTClient";
    private BaseActivity mActivity;
    private Choreographer cgh;
    private String sessionToken;

    @Inject @Named("cached") Lazy<RESTModule.RESTInterface> restInterfaceLazy;
    @Inject Lazy<DeviceListCallback> deviceListCallbackLazy;
    @Inject Lazy<DeviceMeasureCallback> deviceMeasureCallbackLazy;
    @Inject Device device;
    @Inject DeviceList deviceList;
    @Inject Lazy<MessageGram> messageGramLazy;

    private Runnable mainRunnable = new Runnable(){
        @Override
        public void run(){
            try {
                cgh.notifyCghRefresh(Choreographer.REFRESH);
                deviceList.readFile();
                deviceList.addSessionToken(sessionToken);
                Call<String> restDeviceList = restInterfaceLazy.get().getDevices(
                        deviceList
                                .replaceUUID()
                                .readyString);
                restDeviceList.enqueue(deviceListCallbackLazy.get());
            } catch (NullPointerException ex) {
                Log.e(TAG, "Cgh connect failed!");
                cgh.setChStatus(false);
                cgh.notifyCghRefresh(Choreographer.CGHLOST);
                ex.printStackTrace();
            }
            Choreographer.getRestHandler().postDelayed(this, 5000);
        }
    };

    private Runnable msgRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                device.readFile();
                device.addSessionToken(sessionToken);
                Call<String> restDeviceMeasure = restInterfaceLazy.get().getDeviceMeasure(
                        device.replaceUUID()
                                .replaceRecv(device.getName())
                                .readyString);
                restDeviceMeasure.enqueue(deviceMeasureCallbackLazy.get());
            } catch (NullPointerException ex) {
                Log.e(TAG, "Cgh connect failed!");
                cgh.setChStatus(false);
                ex.printStackTrace();
            }
            Choreographer.getRestHandler().postDelayed(this, 2000);
        }
    };

    public RESTClient(BaseActivity activity, Choreographer choreographer){
        this.mActivity = activity;
        this.cgh = choreographer;
        try{
            RESTComponent restComponent = choreographer.getRESTComponent();
            restComponent.inject(this);
        }catch (NullPointerException ex){
            Log.e(TAG, "RESTComponet got failed!");
            ex.printStackTrace();
        }
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void run(){
        String activityType = mActivity.getLocalClassName();
        if (activityType.equals("view.MainActivity")){
            deviceListCallbackLazy.get().setCgh(this.cgh);
            //deviceListCallbackLazy.get().setDeviceList(deviceList);
            Choreographer.getRestHandler().post(mainRunnable);
        }else if (activityType.equals("view.MsgActivity")){
            deviceMeasureCallbackLazy.get().setCgh(this.cgh);
            //deviceMeasureCallbackLazy.get().setMessageGram(messageGramLazy.get());
            device.setContext(mActivity);
            Choreographer.getRestHandler().post(msgRunnable);
        }
    }

    public void setDevice(Device device){
        this.device = device;
    }

    public void setDeviceList(DeviceList deviceList){
        this.deviceList = deviceList;
    }
}
