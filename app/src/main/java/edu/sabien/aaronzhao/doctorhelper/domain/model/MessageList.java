package edu.sabien.aaronzhao.doctorhelper.domain.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

import edu.sabien.aaronzhao.doctorhelper.utils.Utils;

/**
 * Created by AaronZhao on 4/8/16.
 */
public class MessageList extends ArrayList<MessageGram> {

    public static final int QUEUE_LENGTH_SHORT = 5;
    public static final int QUEUE_LENGTH_MEDIUM = 10;
    public static final int QUEUE_LENGTH_LONG = 15;

    private UUID uuid = UUID.randomUUID();
    private static Context mContext;
    public static String readyString = null; //readystring denots which one is ready to send to cgh
    private String tempString;

    public MessageList(){}

    public MessageList(Context context){
        mContext = context;
    }

    public MessageList replaceUUID(){
        String jsonString = Utils.readFromJsonFileToString("get_device_measure", mContext);
        this.tempString = jsonString.replace("UUIDToBeReplaced",uuid.toString());
        return this;
    }

    public MessageList replaceRecv(String recv){
        readyString = tempString.replace("RecvToBeReplaced", recv);
        return this;
    }

    @Override
    public synchronized boolean add(MessageGram message){
        return super.add(message);
    }

    @Override
    public synchronized MessageGram remove(int index){
        return super.remove(index);
    }

}
