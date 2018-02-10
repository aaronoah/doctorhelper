package edu.sabien.aaronzhao.doctorhelper.domain;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

import java.util.Properties;

import edu.sabien.aaronzhao.doctorhelper.domain.model.DataContract;
import edu.sabien.aaronzhao.doctorhelper.utils.Utils;

/**
 * Created by AaronZhao on 2/16/16.
 */
public class DbHelper extends SQLiteOpenHelper{

    private static Context mContext;

    public DbHelper(Context context){
        this(Utils.readProps("db/db_config", context), context);
    }

    private DbHelper(Properties props, Context context) {  //default constructor for every instance
        super(context, props.getProperty("DATABASE_NAME"), null, Integer.parseInt(props.getProperty("DATABASE_VERSION")));
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_DEVICE =
                "CREATE TABLE " + DataContract.DeviceEntry.TABLE_NAME + " (" +
                        DataContract.DeviceEntry._ID + " INTEGER PRIMARY KEY," +
                        DataContract.DeviceEntry.COLUMN_NAME_NAME + " VARCHAR(10)," +
                        DataContract.DeviceEntry.COLUMN_NAME_UUID + " CHAR(16)"  +
                " )";
        String SQL_CREATE_MESSAGE =
                "CREATE TABLE " + DataContract.MessageEntry.TABLE_NAME + " (" +
                        DataContract.MessageEntry._ID + " INTEGER PRIMARY KEY," +
                        DataContract.MessageEntry.COLUMN_NAME_DATA + " VARCHAR(100)," +
                        DataContract.MessageEntry.COLUMN_NAME_MESSAGE_TYPE + " VARCHAR(10)," +
                        DataContract.MessageEntry.COLUMN_NAME_MESSAGE_UUID + " CHAR(16)," +
                        DataContract.MessageEntry.COLUMN_NAME_OPERATION_TYPE + " CHAR(4)," +
                        DataContract.MessageEntry.COLUMN_NAME_TIMESTAMP + " CHAR(50)," +
                        DataContract.MessageEntry.COLUMN_NAME_DEVICE_ID + " VARCHAR(25)," +
                        "FOREIGN KEY (deviceid) REFERENCES Message(_id)" +
                        " )";
        db.execSQL(SQL_CREATE_DEVICE);
        db.execSQL(SQL_CREATE_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        String SQL_DELETE_TABLES = Utils.readFromTxtfile("db/drop_tbs", mContext);
        db.execSQL(SQL_DELETE_TABLES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
