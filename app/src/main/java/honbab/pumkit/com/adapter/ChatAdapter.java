package honbab.pumkit.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import honbab.pumkit.com.data.ChatData;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.Statics;
import honbab.pumkit.com.widget.CircleTransform;
import okhttp3.OkHttpClient;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    Context mContext;
    OkHttpClient httpClient;
    public ArrayList<ChatData> listViewItemList = new ArrayList<>();
    public ArrayList<ChatData> newList = new ArrayList<>();

    public ChatAdapter() {

    }

    public ChatAdapter(Context context, OkHttpClient httpClient, ArrayList<ChatData> listViewItemList) {
        this.mContext = context;
        this.httpClient = httpClient;
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
//        final ChatData data = newList.get(position);
        final ChatData data = listViewItemList.get(position);

        holder.bindToPost(data);
    }

    @Override
    public int getItemCount() {
//        Log.e("abc", "xxx newList.size() = " + newList.size());
//        return newList.size();
        return listViewItemList.size();
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
//            img_chatting = (ImageView) convertView.findViewById(R.id.img_chatting);
        }

        public void bindToPost(ChatData data) {
            txt_chatMessage_item.setVisibility(View.VISIBLE);
//        img_chatting.setVisibility(View.GONE);

            txt_chatMessage_item.setText(data.getText());
            txt_chatUserName_item.setText(data.getToUserName());

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

                Picasso.get().load(data.getPic1())
                        .placeholder(R.drawable.icon_noprofile_circle)
                        .error(R.drawable.icon_noprofile_circle)
                        .transform(new CircleTransform())
                        .into(img_user);
                Log.e("chat", "LEFT , " + data.getPic1());
            }
        }
    }

    public void addItem(String fromId, String toId, String ToUserName,
                        String message, Long milliSeconds, String imageUrl,
                        int imageWidth, int imageHeight, String toUserImg) {

        ChatData item = new ChatData();

        item.setFromId(fromId);
        item.setToId(toId);
        item.setToUserName(ToUserName);
        item.setText(message);
        item.setTimestamp(milliSeconds);
        item.setImageUrl(imageUrl);
        item.setImageWidth(imageWidth);
        item.setImageHeight(imageHeight);
        item.setPic1(toUserImg);

//        ChatData item = new ChatData(fromId, toId, message, milliSeconds);

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
}