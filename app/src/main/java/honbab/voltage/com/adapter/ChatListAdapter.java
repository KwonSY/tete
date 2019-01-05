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

import java.util.ArrayList;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.tete.ChatActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.CircleTransform;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private Context mContext;
    public ArrayList<UserData> listViewItemList = new ArrayList<>();
    public ArrayList<UserData> newList = new ArrayList<>();

    public ChatListAdapter(Context context) {
        this.mContext = context;
    }

    public ChatListAdapter(Context context, ArrayList<UserData> listViewItemList) {
        this.mContext = context;
        this.listViewItemList = listViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_feedee, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//        final UserData data = newList.get(position);
        final UserData data = listViewItemList.get(position);

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
        public ImageView img_user, img_new_chat;
        public TextView txt_userName;

        public ViewHolder(View itemView) {
            super(itemView);

            img_user = itemView.findViewById(R.id.img_user);
            img_new_chat = itemView.findViewById(R.id.img_new_chat);
            txt_userName = itemView.findViewById(R.id.txt_userName);
        }

        public void bindToPost(final UserData data) {
            int new_chat = Integer.parseInt(data.getStatus());
            Log.e("abc", "chatlist newchat " + data.getUser_name() + new_chat);
            if (new_chat > 0)
                img_new_chat.setVisibility(View.VISIBLE);
            else
                img_new_chat.setVisibility(View.GONE);

            txt_userName.setText(data.getUser_name());
//            txt_lastMessage.setText(data.getToUserName());
//            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd hh:mm");
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(data.getTimestampLong());
//            txt_time.setText(formatter.format(calendar.getTime()));

            Picasso.get().load(data.getImg_url())
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(img_user);
            img_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("fromId", Statics.my_id);
                    intent.putExtra("toId", data.getUser_id());
                    intent.putExtra("toUserName", data.getUser_name());
                    intent.putExtra("toUserImg", data.getUser_name());
                    intent.putExtra("toToken", data.getToken());
                    intent.putExtra("restData", new RestData());
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

    public void addItem(UserData data) {
        UserData item = data;
//        item.setTimestamp(milliSeconds);
//        Log.e("abc", "왜 안도냐 1= " + data.getToUserName());
//        Log.e("abc", "왜 안도냐 1= " + data.getToUserImg());
//        Log.e("abc", "왜 안도냐 3= " + data.getTimestampLong());

        listViewItemList.add(item);

//        RemoveDuplicate();
    }

//    public void RemoveDuplicate() {
//        Set set = new TreeSet(new Comparator<ChatData>() {
//            @Override
//            public int compare(ChatData obj1, ChatData obj2) {
//                // DESC 내림차순
////                return (obj1.timestamp > obj2.timestamp) ? -1: (obj1.timestamp > obj2.timestamp) ? 1:0 ;
//                // ASC 오름차순
//                return (obj1.timestamp < obj2.timestamp) ? -1 : (obj1.timestamp > obj2.timestamp) ? 1 : 0;
//            }
//
//        });
//        set.addAll(listViewItemList);
//
//        newList = new ArrayList<ChatData>(set);
//    }

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