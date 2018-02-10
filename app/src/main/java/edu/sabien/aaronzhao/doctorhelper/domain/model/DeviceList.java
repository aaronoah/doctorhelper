package edu.sabien.aaronzhao.doctorhelper.domain.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import edu.sabien.aaronzhao.doctorhelper.utils.Utils;

/**
 * Created by AaronZhao on 4/8/16.
 */
public class DeviceList extends ArrayList<Device> {

    private static UUID uuid = UUID.randomUUID();
    private static Context mContext;
    public static String readyString; //readystring denots which one is ready to send to cgh
    private static String tempString;

    public DeviceList(){}

    public DeviceList(Context context){
        mContext = context;
    }

    public void readFile(){
        tempString = Utils.readFromJsonFileToString("get_device_list", mContext);
    }

    public DeviceList replaceUUID(){
        UUID temp = UUID.randomUUID();
        while (temp.compareTo(uuid) == 0){
            temp = UUID.randomUUID();
        }
        //Log.d("temp", temp.toString());
        uuid = temp;
        //Log.d("uuid", uuid.toString());
        readyString = tempString.replace("UUIDToBeReplaced",uuid.toString());
        tempString = readyString;
        //Log.d("DeviceList", readyString);
        return this;
    }

    public void addSessionToken(String sessionToken){
        readyString = tempString.replace("SessionToBeReplaced", sessionToken);
        tempString = readyString;
    }

    @Override
    public synchronized boolean add(Device device){
        return super.add(device);
    }

    @Override
    public synchronized Device remove(int index){
        return super.remove(index);
    }

}
