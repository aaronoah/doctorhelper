package edu.sabien.aaronzhao.doctorhelper.domain.model;

import android.content.Context;

import java.io.Serializable;
import java.util.UUID;

import edu.sabien.aaronzhao.doctorhelper.utils.Utils;

/**
 * Created by AaronZhao on 2/29/16.
 */
public class Device implements Serializable{

    private static final long serialVersionUID = -3032400668288516411L; //THIS CLASS HOLDS THE LOCAL DEVICE INSTANCE

    public static String readyString; //readystring denotes which one is ready to send to cgh
    private static String tempString;
    private Context mContext;
    private String name = "N/A";
    private UUID uuid = UUID.randomUUID();  //universally unique identifier
    private static int id = 0;   //labeling the numbers of the same type device, initialize to 1
    private static String recv = "N/A";  //recv denotes the receiver field of json file get_device

    public Device(){
        id++;
    }

    public Device(Context context){
        mContext = context;
        id++;
    }

    public void readFile(){
        tempString = Utils.readFromJsonFileToString("get_device_measure", mContext);
    }

    public void addSessionToken(String sessionToken){
        readyString = tempString.replace("SessionToBeReplaced", sessionToken);
        tempString = readyString;
    }

    public Device replaceUUID(){
        UUID temp = UUID.randomUUID();
        while (temp.compareTo(uuid) == 0){
            temp = UUID.randomUUID();
        }
        uuid = temp;
        readyString = tempString.replace("UUIDToBeReplaced",uuid.toString());
        tempString = readyString;
        return this;
    }

    public Device replaceRecv(String recv){
        readyString = tempString.replace("RecvToBeReplaced", recv);
        tempString = readyString;
        return this;
    }

    public String getName(){
        return this.name;
    }

    public Device setName(String name) {
        this.name = name;
        return this;
    }

    public void setContext(Context context){
        this.mContext = context;
    }
}
