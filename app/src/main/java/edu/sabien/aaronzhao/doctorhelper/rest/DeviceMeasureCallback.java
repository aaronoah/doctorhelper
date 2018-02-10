package edu.sabien.aaronzhao.doctorhelper.rest;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.StringTokenizer;

import edu.sabien.aaronzhao.doctorhelper.helper.Choreographer;
import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageGram;
import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageList;
import edu.sabien.aaronzhao.doctorhelper.presenter.MsgPresenterImpl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AaronZhao on 4/21/16.
 */
public class DeviceMeasureCallback implements Callback<String> {

    private static final String TAG = "DeviceMeasureCallback";
    private RESTClient restClient;
    private Choreographer cgh;
    private String[] measureArr; //storing measurement result, data field
    private String msgUUID;  //storing message UUID
    private String deviceId;  //storing device id

    public DeviceMeasureCallback(RESTClient restClient){
        this.restClient = restClient;
    }

    public void setCgh(Choreographer choreographer){
        this.cgh = choreographer;
    }


    @Override
    public void onResponse(Call<String> call, Response<String> response){
        if (response.isSuccessful()){
            Log.d(TAG, "Status code = " + response.code());
            String jsonString = response.body();
            Log.d(TAG, jsonString);
            try{
                JSONObject msgList = new JSONObject(jsonString);
                measureArr = msgList.getJSONObject("Contenido")
                        .getJSONObject("parametros").getJSONObject("getMeasureResult")
                        .get("value").toString().split("\\;");
                msgUUID = msgList.get("IdMensaje").toString();
                deviceId = msgList.get("sender").toString();
                produce(measureArr, msgUUID, deviceId);
            }catch (JSONException ex){
                ex.printStackTrace();
            }catch (InterruptedException ex){
                ex.printStackTrace();
                Log.e(TAG, "Interrupted!");
            }

        }else{
            // response received but request not successful (like 400,401,403 etc)
            //Handle errors
            int statusCode = response.code();
            // handle request errors yourself
            ResponseBody errorBody = response.errorBody();
            Log.e(TAG, statusCode+ errorBody.toString());
        }
    }

    @Override
    public void onFailure(Call<String> call, Throwable t){
        Log.e(TAG, "GET measure failed!");
        Log.e(TAG, t.toString());
        cgh.notifyMsgRefresh(Choreographer.CGHLOST);
    }

    public void produce(String[] measureArr, String msgUUID, String deviceId) throws InterruptedException{
        synchronized (MsgPresenterImpl.getMessageList()){
            while (MsgPresenterImpl.getMessageList().size() == MessageList.QUEUE_LENGTH_LONG){
                MsgPresenterImpl.getMessageList().wait();
            }
            Log.d(TAG, "IN PRODUCER!");
            for (int i=0;i<measureArr.length;i++){
                MessageGram msg = new MessageGram();
                msg.setData(measureArr[i]);
                msg.setTimestamp(new Date());
                msg.setOperationType(MessageGram.OPERATION_TYPE.PULL.toString());
                msg.setDeviceid(deviceId);
                msg.setMESSAGE_UUID(msgUUID);
                msg.setMsgType(MessageGram.MESSAGE_TYPE.Temperature.toString());
                addMsgToQueue(msg);
            }
            Log.d(TAG, "PRODUCE COMPLETE!");
            MsgPresenterImpl.getMessageList().notify();
        }
    }

    public void addMsgToQueue(MessageGram messageGram){
        MsgPresenterImpl.getMessageList().add(messageGram);
    }
}
