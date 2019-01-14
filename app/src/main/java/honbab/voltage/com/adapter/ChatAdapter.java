package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import honbab.voltage.com.data.ChatData;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.CircleTransform;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    Context mContext;
    public ArrayList<ChatData> listViewItemList = new ArrayList<>();
    public ArrayList<ChatData> newList = new ArrayList<>();

    public ChatAdapter(Context context) {
        this.mContext = context;
    }

    public ChatAdapter(Context context, ArrayList<ChatData> listViewItemList) {
        this.mContext = context;
        this.listViewItemList = listViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_chat, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ChatData data = newList.get(position);
//        final ChatData data = listViewItemList.get(position);

        holder.bindToPost(data);
    }

    @Override
    public int getItemCount() {
//        Log.e("abc", "chat listViewItemList.size() = " + listViewItemList.size());
//        Log.e("abc", "chat newList.size() = " + newList.size());
        return newList.size();
//        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout layout_chatBox;
        public ImageView img_user;
        public TextView txt_chatUserName_item;
        public TextView txt_chatMessage_item;
        public TextView chatTime_right;
        public TextView chatTime_left;

        public ViewHolder(View itemView) {
            super(itemView);

            layout_chatBox = itemView.findViewById(R.id.layout_chatBox);
            img_user = itemView.findViewById(R.id.img_user);
            txt_chatUserName_item = itemView.findViewById(R.id.txt_chatUserName_item);
            txt_chatMessage_item = itemView.findViewById(R.id.txt_chatMessage_item);
            chatTime_right = itemView.findViewById(R.id.chatTime_right);
            chatTime_left = itemView.findViewById(R.id.chatTime_left);
        }

        public void bindToPost(final ChatData data) {
            txt_chatMessage_item.setVisibility(View.VISIBLE);
//        img_chatting.setVisibility(View.GONE);

            txt_chatMessage_item.setText(data.getText());
            txt_chatUserName_item.setText(data.getToUserName());

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd hh:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(data.getTimestampLong());
            chatTime_right.setText(formatter.format(calendar.getTime()));
            chatTime_left.setText(formatter.format(calendar.getTime()));

            if (data.getType().equals("t")) {
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

                    Picasso.get().load(data.getToUserImg())
                            .placeholder(R.drawable.icon_noprofile_circle)
                            .error(R.drawable.icon_noprofile_circle)
                            .transform(new CircleTransform())
                            .into(img_user);
                    Log.e("chat", "LEFT , " + data.getToId());

                    img_user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("user_id", data.getFromId());
                            mContext.startActivity(intent);
                        }
                    });
                }
            } else if (data.getType().equals("a")) {
                layout_chatBox.setGravity(Gravity.CENTER);
                img_user.setVisibility(View.GONE);
                txt_chatUserName_item.setVisibility(View.GONE);

                chatTime_right.setVisibility(View.GONE);
                chatTime_left.setVisibility(View.GONE);

                txt_chatMessage_item.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                txt_chatMessage_item.setBackgroundResource(R.color.brightgrey);
                txt_chatMessage_item.setGravity(Gravity.CENTER);
                txt_chatMessage_item.setTextColor(Color.parseColor("#ffffff"));
                Log.e("chat", "CENTER");
            }
        }
    }

    public void addItem(String type, String fromId, String toId, String toUserName, String message,
                        Long milliSeconds,
                        String imageUrl, int imageWidth, int imageHeight, String toUserImg) {
//        ChatData item = new ChatData();
        ChatData item = new ChatData(type, fromId, toId, toUserName, message, imageUrl, imageWidth, imageHeight, toUserImg);
//        item.setFromId(fromId);
//        item.setToId(toId);
//        item.setToUserName(ToUserName);
//        item.setText(message);
        item.setTimestamp(milliSeconds);
//        item.setImageUrl(imageUrl);
//        item.setImageWidth(imageWidth);
//        item.setImageHeight(imageHeight);
//        item.setPic1(toUserImg);

        listViewItemList.add(item);

        RemoveDuplicate();
    }

    public void RemoveDuplicate() {
        Set set = new TreeSet(new Comparator<ChatData>() {
            @Override
            public int compare(ChatData obj1, ChatData obj2) {
                // DESC 내림차순
//                return (obj1.timestamp > obj2.timestamp) ? -1: (obj1.timestamp > obj2.timestamp) ? 1:0 ;
                // ASC 오름차순
                return (obj1.timestamp < obj2.timestamp) ? -1 : (obj1.timestamp > obj2.timestamp) ? 1 : 0;
            }

        });
        set.addAll(listViewItemList);

        newList = new ArrayList<ChatData>(set);
    }

    public String getLastType() {
        return listViewItemList.get(listViewItemList.size() - 1).getType();
    }

    public void clearItemList() {
        listViewItemList.clear();
        notifyDataSetChanged();
    }
}