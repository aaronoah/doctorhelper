package edu.sabien.aaronzhao.doctorhelper.tcp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageGram;
import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageList;
import edu.sabien.aaronzhao.doctorhelper.presenter.MsgPresenterImpl;
import edu.sabien.aaronzhao.doctorhelper.utils.Utils;

/**
 * Created by AaronZhao on 09/05/16.
 */
public class TCPClient {

    private static final String TAG = "TCPClient";
    private int port = 8081;
    private int backlog = 2;
    private Thread tcpThread;
    private String measure; //storing measurement result, data field
    private String msgUUID;  //storing message UUID
    private String deviceId;  //storing device id

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d(TAG, "IN TCP!");
                ServerSocket serverSocket = new ServerSocket(port, backlog);
                Socket clientSock = serverSocket.accept();
                clientSock.setKeepAlive(true);
                Log.d(TAG, "revcive from " + clientSock.getPort());
                InputStream in = clientSock.getInputStream();
                String jsonString = Utils.convertStreamToString(in);
                Log.d(TAG, jsonString);
                try{
                    JSONObject msgList = new JSONObject(jsonString);
                    measure = msgList.getJSONObject("Contenido")
                            .getJSONObject("parametros").getJSONObject("HeartRate")
                            .get("value").toString();
                    msgUUID = msgList.get("IdMensaje").toString();
                    deviceId = msgList.get("sender").toString();
                    produce(measure, msgUUID, deviceId);
                }catch (JSONException ex){
                    ex.printStackTrace();
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                    Log.e(TAG, "Interrupted!");
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    };

    public TCPClient(){

    }

    public void run(){
        tcpThread = new Thread(runnable);
        tcpThread.start();
    }

    public void onDisconnect(){
        tcpThread = null;
    }

    public void produce(String measure, String msgUUID, String deviceId) throws InterruptedException{
        synchronized (MsgPresenterImpl.getMessageList()){
            while (MsgPresenterImpl.getMessageList().size() == MessageList.QUEUE_LENGTH_LONG){
                MsgPresenterImpl.getMessageList().wait();
            }
            Log.d(TAG, "IN PRODUCER!");
                MessageGram msg = new MessageGram();
                msg.setData(measure);
                msg.setTimestamp(new Date());
                msg.setOperationType(MessageGram.OPERATION_TYPE.PUSH.toString());
                msg.setDeviceid(deviceId);
                msg.setMESSAGE_UUID(msgUUID);
                msg.setMsgType(MessageGram.MESSAGE_TYPE.HEART_RATE.toString());
                addMsgToQueue(msg);
            Log.d(TAG, "PRODUCE COMPLETE!");
            MsgPresenterImpl.getMessageList().notify();
        }
    }

    public void addMsgToQueue(MessageGram messageGram){
        MsgPresenterImpl.getMessageList().add(messageGram);
    }
}
