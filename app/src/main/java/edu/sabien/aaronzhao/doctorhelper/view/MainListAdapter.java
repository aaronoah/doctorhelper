package edu.sabien.aaronzhao.doctorhelper.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.sabien.aaronzhao.doctorhelper.R;
import edu.sabien.aaronzhao.doctorhelper.domain.model.Device;
import edu.sabien.aaronzhao.doctorhelper.domain.model.DeviceList;

/**
 * Created by AaronZhao on 4/21/16.
 */
public class MainListAdapter extends BaseAdapter {

    public static final int NO_UPDATE = 4;
    public static final int ADD_DEVICE = 1;
    public static final int ADD_DEVICE_LIST = 2;
    public static final int REMOVE_DEVICE_LSIT = 3;
    public static final int NO_DEVICE = -1;
    public static final int DELETE_DEVICE = -2;

    private LayoutInflater mInflater;
    private DeviceList deviceList;
    private int selectPosition;
    public DeviceList getDeviceList() {
        return deviceList;
    }

    public MainListAdapter(Context context, DeviceList devices) {
        this.mInflater = LayoutInflater.from(context);
        this.deviceList = devices;
    }
    @Override
    public int getCount() {
        return deviceList.size();
    }
    @Override
    public Device getItem(int arg0) {
        return deviceList.get(arg0);
    }
    @Override
    public long getItemId(int arg0) {
        return arg0;
    }


    public void refresh(Device device, int type){
        if (type == ADD_DEVICE){
            deviceList.add(device);
        }else if (type == DELETE_DEVICE){
            deviceList.remove(device);
        }else if (type == NO_UPDATE){
            return;
        }
        notifyDataSetChanged();
    }

    public void refreshAll(DeviceList deviceLs, int type){
        if (type == ADD_DEVICE_LIST){
            deviceList.addAll(deviceLs);
        }else if (type == REMOVE_DEVICE_LSIT){
            deviceList.removeAll(deviceLs);
        }
        notifyDataSetChanged();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Device device = deviceList.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.row_list_device, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (device.getName() != "NO DEVICES") {
            holder.deviceName.setText(device.getName());
            holder.deviceIcon.setImageResource(R.drawable.ic_fiber_smart_record);
        }else if (device.getName() == "NO DEVICES"){
            holder.deviceName.setText("NO DEVICES");
            holder.deviceIcon.setImageResource(R.drawable.ic_highlight_off);
        }

        if (position == selectPosition){
            convertView.setBackgroundColor(Color.GRAY);
        }else {
            convertView.setBackgroundColor(Color.BLACK);
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.main_device_label) TextView deviceName;
        @BindView(R.id.main_device_icon) ImageView deviceIcon;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void setSelectItem(int position){
        selectPosition = position;
    }
}

