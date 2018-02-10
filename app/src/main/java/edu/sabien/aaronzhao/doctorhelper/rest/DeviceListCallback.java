package edu.sabien.aaronzhao.doctorhelper.rest;

import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import edu.sabien.aaronzhao.doctorhelper.domain.model.DeviceList;
import edu.sabien.aaronzhao.doctorhelper.helper.Choreographer;
import edu.sabien.aaronzhao.doctorhelper.domain.model.Device;
import edu.sabien.aaronzhao.doctorhelper.view.MainActivity;
import edu.sabien.aaronzhao.doctorhelper.view.MainListAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AaronZhao on 4/19/16.
 */
public class DeviceListCallback implements Callback<String> {

    private static final String TAG = "DeviceListCallback";
    private Choreographer cgh;
    private RESTClient restClient;
    //private Device device;

    public DeviceListCallback(RESTClient restClient){
        this.restClient = restClient;
    }

    public void setCgh(Choreographer choreographer){
        this.cgh = choreographer;
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        if (response.isSuccessful()) {
            // request successful (status code 200, 201)
            //Choreographer.this.devices = response.body();
            cgh.setChStatus(true);
            Log.d(TAG, "Status Code = " + response.code());
            String sensorList = response.body();
            Log.d(TAG, sensorList);
            try{
                JSONObject sensorLs = new JSONObject(sensorList);
                String[] deviceArr = sensorLs.getJSONObject("Contenido")
                        .getJSONObject("parametros").getJSONObject("getSensorListResult")
                        .get("value").toString().split("\\;");
                if (deviceArr != null){
                    if (deviceArr.length == 0)
                        deviceUpdate(MainListAdapter.NO_DEVICE, null);
                    else {
                        boolean flag = false;
                        for (int i=0; i<deviceArr.length;i++){
                            if (restClient.deviceList.size() == 0) {
                                Device device = new Device().setName(deviceArr[i]);
                                restClient.deviceList.add(device);
                                deviceUpdate(MainListAdapter.ADD_DEVICE, device);
                            }else{
                                for (int j=0;j<restClient.deviceList.size();j++){
                                    if (!restClient.deviceList.get(j).getName().equals(deviceArr[i])){
                                        Device device = new Device().setName(deviceArr[i]);
                                        restClient.deviceList.add(device);
                                        deviceUpdate(MainListAdapter.ADD_DEVICE, device);
                                        flag = true;
                                    }
                                }
                            }
                        }
                        if (!flag){
                            deviceUpdate(MainListAdapter.NO_UPDATE, null);
                        }
                    }
                }else
                    deviceUpdate(MainListAdapter.NO_DEVICE, null);
            }catch (JSONException ex){
                Log.e(TAG, "Parsing sensor list failed!");
                ex.printStackTrace();
            }
        } else {
            // response received but request not successful (like 400,401,403 etc)
            //Handle errors
            int statusCode = response.code();
            // handle request errors yourself
            ResponseBody errorBody = response.errorBody();
            Log.e(TAG, statusCode+ errorBody.toString());
            cgh.setChStatus(false);
            cgh.notifyCghRefresh(Choreographer.CGHLOST);
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        Log.e(TAG, "REST request failed!");
        Log.e(TAG, t.toString());
        cgh.setChStatus(false);
        cgh.notifyCghRefresh(Choreographer.CGHLOST);
    }

    private void deviceUpdate(int type, Device device){
        Message msg = Message.obtain();
        msg.what = type;
        if (type == MainListAdapter.ADD_DEVICE)
            msg.obj = device;
        else if (type == MainListAdapter.NO_DEVICE)
            msg.obj = new Device().setName("NO DEVICES");
        else if (type == MainListAdapter.NO_UPDATE){
            msg.obj = device;
        }

        MainActivity.getDeviceListHandler().sendMessage(msg);
    }
}
