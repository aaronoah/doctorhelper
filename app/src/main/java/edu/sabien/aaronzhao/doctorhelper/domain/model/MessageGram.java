package edu.sabien.aaronzhao.doctorhelper.domain.model;

import java.io.Serializable;
import java.util.Date;
/**
 * Created by AaronZhao on 3/7/16.
 */
public class MessageGram implements Serializable{

    private static final long serialVersionUID = 6699452812205487213L;

    private String data;
    private String MESSAGE_UUID;
    private Date timestamp;
    private String msgType;
    private String operationType;
    private String deviceid;

    public static enum MESSAGE_TYPE{ //defining the types of messages being received
        HEART_RATE{
            @Override
            public String toString(){
                return "HEART_RATE";
            }
        }, ACTIVITY{
            @Override
            public String toString(){
                return "ACTIVITY";
            }
        }, SPO2{
            @Override
            public String toString(){
                return "SPO2";
            }
        }, Temperature{
            @Override
            public String toString(){
                return "Temperature";
            }
        }
    }

    public static enum OPERATION_TYPE{
        PULL{
            @Override
            public String toString(){
                return "PULL";
            }
        }, PUSH{
            @Override
            public String toString(){
                return "PUSH";
            }
        }
    }

    public String getMESSAGE_UUID() {
        return MESSAGE_UUID;
    }

    public void setMESSAGE_UUID(String MESSAGE_UUID) {
        this.MESSAGE_UUID = MESSAGE_UUID;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString(){   //is used to display message content on the Msg Panel, do not modify
        return getMsgType() + " | " + getData() + " | " + getTimestamp();
    }
}
