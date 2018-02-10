package edu.sabien.aaronzhao.doctorhelper.domain.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import edu.sabien.aaronzhao.doctorhelper.domain.model.DataContract;
import edu.sabien.aaronzhao.doctorhelper.domain.DbHelper;
import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageGram;
import edu.sabien.aaronzhao.doctorhelper.presenter.MsgPresenterImpl;

/**
 * Created by AaronZhao on 4/8/16.
 */
public class OfflineDao implements Runnable{

    private DbHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String TAG = "OfflineDao";

    public OfflineDao(DbHelper mDbHelper) {
        this.mDbHelper = mDbHelper;
    }

    @Override
    public void run(){
        fetch();
    }

    public void fetch(){
        mDb = mDbHelper.getReadableDatabase();
        Cursor res =  mDb.rawQuery( "select * from message", null );
        if (!res.moveToFirst()){
            Log.e(TAG, "empty result sets!");
            return;
        }
        while (res.moveToNext()){
            MessageGram temp = new MessageGram();
            temp.setData(res.getString(
                    res.getColumnIndexOrThrow(DataContract.MessageEntry.COLUMN_NAME_DATA)
            ));
            temp.setMsgType(res.getString(
                    res.getColumnIndexOrThrow(DataContract.MessageEntry.COLUMN_NAME_MESSAGE_TYPE)
            ));
            temp.setOperationType(res.getString(
                    res.getColumnIndexOrThrow(DataContract.MessageEntry.COLUMN_NAME_OPERATION_TYPE)
            ));
            temp.setMESSAGE_UUID(res.getString(
                    res.getColumnIndexOrThrow(DataContract.MessageEntry.COLUMN_NAME_MESSAGE_UUID)
            ));
            temp.setDeviceid(
                    res.getString(res.getColumnIndexOrThrow(DataContract.MessageEntry.COLUMN_NAME_DEVICE_ID))
            );
            try{
                temp.setTimestamp(new SimpleDateFormat("E MMM d k:m:s Z y", Locale.ENGLISH).parse(
                        res.getString(
                                res.getColumnIndexOrThrow(DataContract.MessageEntry.COLUMN_NAME_TIMESTAMP)
                        )
                ));
            }catch (ParseException ex){
                Log.e(TAG, "Date formate parsing error!");
                ex.printStackTrace();
            }
            MsgPresenterImpl.getMessageList().add(temp);
        }
    }
}
