package edu.sabien.aaronzhao.doctorhelper.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.sabien.aaronzhao.doctorhelper.R;
import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageGram;
import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageList;

/**
 * Created by AaronZhao on 28/04/16.
 */
public class MsgAdapter extends BaseAdapter{
    public static final int NO_MESSAGE = -1;
    public static final int ADD_MESSAGE_LIST = 1;
    public static final int ADD_MESSAGE = 0;
    public static final int DELETE_MESSAGE = -1;
    public static final int REMOVE_MESSAGE_LIST = -2;

    private Context mContext;
    private LayoutInflater mInflater;
    private MessageList mData;
    private int selectPosition;

    public MsgAdapter(Context context, MessageList data){
        this.mInflater = LayoutInflater.from(context);
        mData = data;
    }
    public void refresh(MessageGram msg, int type){
        if (type == ADD_MESSAGE){
            mData.add(msg);
        }else if (type == DELETE_MESSAGE){
            mData.remove(msg);
        }
        notifyDataSetChanged();
    }
    public void refreshAll(MessageList messageList, int type){
        if (type == ADD_MESSAGE_LIST){
            mData.addAll(messageList);
        }else if (type == REMOVE_MESSAGE_LIST){
            mData.removeAll(messageList);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }
    @Override
    public MessageGram getItem(int arg0) {
        return mData.get(arg0);
    }
    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        MessageGram message = mData.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.row_list_msg, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        if (message.getOperationType().equals("PULL"))
            holder.msgContent.setText(message.toString());
        else if (message.getOperationType().equals("PUSH")){
            holder.msgContent.setTextColor(Color.YELLOW);
            holder.msgContent.setText(message.toString());
        }
        if (position == selectPosition){
            convertView.setBackgroundColor(Color.GRAY);
        }else {
            convertView.setBackgroundColor(Color.BLACK);
        }
        return convertView;
    }
    static class ViewHolder {
        @BindView(R.id.msg_content)
        TextView msgContent;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void setSelectItem(int position){
        selectPosition = position;
    }
}
