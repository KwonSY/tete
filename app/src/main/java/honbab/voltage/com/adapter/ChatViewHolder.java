package honbab.voltage.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import honbab.voltage.com.data.ChatData;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout layout_chatBox;
    public ImageView img_user;
    public TextView txt_chatUserName_item;
    public TextView txt_chatMessage_item;
    public TextView chatTime_right;
    public TextView chatTime_left;

    public ChatViewHolder(View itemView) {
        super(itemView);

        layout_chatBox = itemView.findViewById(R.id.layout_chatBox);
        img_user = itemView.findViewById(R.id.img_user);
        txt_chatUserName_item = itemView.findViewById(R.id.txt_chatUserName_item);
        txt_chatMessage_item = itemView.findViewById(R.id.txt_chatMessage_item);
        chatTime_right = itemView.findViewById(R.id.chatTime_right);
        chatTime_left = itemView.findViewById(R.id.chatTime_left);
    }

    public void bindToPost(ChatData data) {
        txt_chatMessage_item.setVisibility(View.VISIBLE);
//        img_chatting.setVisibility(View.GONE);

        txt_chatMessage_item.setText(data.getText());

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd hh:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(data.getTimestampLong());
        chatTime_right.setText(formatter.format(calendar.getTime()));
        chatTime_left.setText(formatter.format(calendar.getTime()));

        if (data.getFromId().equals(Statics.my_id)) {
            layout_chatBox.setGravity(Gravity.RIGHT);
            img_user.setVisibility(View.GONE);
            txt_chatUserName_item.setVisibility(View.GONE);

            chatTime_right.setVisibility(View.GONE);
            chatTime_left.setVisibility(View.VISIBLE);

            Log.e("chat", "RIGHT , ");
        } else {
            layout_chatBox.setGravity(Gravity.LEFT);
            img_user.setVisibility(View.VISIBLE);
            txt_chatUserName_item.setVisibility(View.VISIBLE);

            chatTime_right.setVisibility(View.VISIBLE);
            chatTime_left.setVisibility(View.GONE);
            Log.e("chat", "LEFT , ");
        }
    }
}