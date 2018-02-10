package edu.sabien.aaronzhao.doctorhelper.domain.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.util.Log;

import edu.sabien.aaronzhao.doctorhelper.domain.DbHelper;
import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageGram;
import edu.sabien.aaronzhao.doctorhelper.domain.model.DataContract;
import edu.sabien.aaronzhao.doctorhelper.presenter.MsgPresenterImpl;
import edu.sabien.aaronzhao.doctorhelper.view.MsgActivity;
import edu.sabien.aaronzhao.doctorhelper.view.MsgAdapter;

/**
 * Created by AaronZhao on 4/8/16.
 */
public class OnlineDao implements Runnable{  //online dao deals with data storage operation

    private DbHelper mDbHelper;
    private SQLiteDatabase mDb;
    private String TAG = "OnlineDao";

    public OnlineDao(DbHelper dbHelper) {
        mDbHelper = dbHelper;
    }

    @Override
    public void run(){
        mDb = mDbHelper.getWritableDatabase();
        try{
            while (true){
                consumeData();
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
            Log.e(TAG, "Interrupted!");
        }
    }

    public void consumeData() throws InterruptedException{
        synchronized (MsgPresenterImpl.getMessageList()){
            while (MsgPresenterImpl.getMessageList().size() == 0){
                MsgPresenterImpl.getMessageList().wait();
            }
            Log.d(TAG, "IN CONSUMER!");
            for (MessageGram temp : MsgPresenterImpl.getMessageList()){
                store(temp);
                Message msg = Message.obtain();
                msg.what = MsgAdapter.ADD_MESSAGE;
                msg.obj = temp;
                MsgActivity.getMsgHandler().sendMessage(msg);
                remove(temp);
            }
            Log.d(TAG, "CONSUME COMPLETE!");
            MsgPresenterImpl.getMessageList().notify();
        }
    }

    public void store(MessageGram temp){ //stores the data into the sqlite local
        ContentValues values = new ContentValues();
        values.put(DataContract.MessageEntry.COLUMN_NAME_DATA, temp.getData());
        values.put(DataContract.MessageEntry.COLUMN_NAME_MESSAGE_TYPE, temp.getMsgType());
        values.put(DataContract.MessageEntry.COLUMN_NAME_MESSAGE_UUID, temp.getMESSAGE_UUID());
        values.put(DataContract.MessageEntry.COLUMN_NAME_OPERATION_TYPE, temp.getOperationType());
        values.put(DataContract.MessageEntry.COLUMN_NAME_TIMESTAMP, temp.getTimestamp().toString());
        values.put(DataContract.MessageEntry.COLUMN_NAME_DEVICE_ID, temp.getDeviceid());
        mDb.insert(DataContract.MessageEntry.TABLE_NAME, null, values);
    }

    public void remove(MessageGram temp){
        MsgPresenterImpl.getMessageList().remove(temp);
    }

}
