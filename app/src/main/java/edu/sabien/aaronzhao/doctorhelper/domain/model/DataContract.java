package edu.sabien.aaronzhao.doctorhelper.domain.model;

import android.provider.BaseColumns;

/**
 * Created by AaronZhao on 2/16/16.
 */
public final class DataContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DataContract() {}

    public static abstract class MessageEntry implements BaseColumns{
        public static final String TABLE_NAME = "Message";
        public static final String COLUMN_NAME_MESSAGE_UUID = "messageuuid";
        public static final String COLUMN_NAME_DATA = "data";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_MESSAGE_TYPE = "messagetype";
        public static final String COLUMN_NAME_OPERATION_TYPE = "operationtype";
        public static final String COLUMN_NAME_DEVICE_ID = "deviceid";
    }

    public static abstract class DeviceEntry implements BaseColumns{
        public static final String TABLE_NAME = "Device";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_NAME = "name";
    }
}
