package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import honbab.voltage.com.widget.CircleTransform;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    Context mContext;
    public ArrayList<ChatData> listViewItemList = new ArrayList<>();
    public ArrayList<ChatData> newList = new ArrayList<>();

    public ChatListAdapter(Context context) {
        this.mContext = context;
    }

    public ChatListAdapter(Context context, ArrayList<ChatData> listViewItemList) {
        this.mContext = context;
        this.listViewItemList = listViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_chatlist, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//        final ChatData data = newList.get(position);
        final ChatData data = listViewItemList.get(position);

        holder.bindToPost(data);
    }

    @Override
    public int getItemCount() {
//        Log.e("abc", "chat listViewItemList.size() = " + listViewItemList.size());
//        Log.e("abc", "chat newList.size() = " + newList.size());
//        return newList.size();
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img_user;
        public TextView txt_userName, txt_lastMessage, txt_time;

        public ViewHolder(View itemView) {
            super(itemView);

            img_user = itemView.findViewById(R.id.img_user);
            txt_userName = itemView.findViewById(R.id.txt_userName);
            txt_lastMessage = itemView.findViewById(R.id.txt_lastMessage);
            txt_time = itemView.findViewById(R.id.txt_time);
        }

        public void bindToPost(final ChatData data) {
            Log.e("abc", "bindToPost getToUserName1= " + data.getToUserName());
            Log.e("abc", "bindToPost getImageUrl2= " + data.getImageUrl());
            Log.e("abc", "bindToPost getTimestampLong3= " + data.getTimestampLong());

            txt_userName.setText(data.getText());
            txt_lastMessage.setText(data.getToUserName());

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd hh:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(data.getTimestampLong());
            txt_time.setText(formatter.format(calendar.getTime()));

            Picasso.get().load(data.getToUserImg())
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(img_user);
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
    }

//    public void addItem(String fromId, String toId, String toUserName, String message,
//                        Long milliSeconds,
//                        String imageUrl, int imageWidth, int imageHeight, String toUserImg) {
//
//        ChatData item = new ChatData(fromId, toId, toUserName, message, imageUrl, imageWidth, imageHeight, toUserImg);
//        item.setTimestamp(milliSeconds);
//
//        listViewItemList.add(item);
//
////        RemoveDuplicate();
//    }

    public void addItem(ChatData data) {
        ChatData item = data;
//        item.setTimestamp(milliSeconds);
        Log.e("abc", "왜 안도냐 1= " + data.getToUserName());
        Log.e("abc", "왜 안도냐 1= " + data.getToUserImg());
        Log.e("abc", "왜 안도냐 3= " + data.getTimestampLong());

        listViewItemList.add(item);

//        RemoveDuplicate();
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

//    public void changeUserName(String partnerId, String user_name, String pic1) {
//        for (int i=0; i < newList.size(); i++) {
//            if (newList.get(i).getYourId().contains(partnerId)) {
////                listViewItemList.get(i).getYourName().replace(yourName, user_name);
//                newList.get(i).setYourName(user_name);
//                newList.get(i).setPic1(Statics.main_url + pic1);
//            }
//        }
//    }
}