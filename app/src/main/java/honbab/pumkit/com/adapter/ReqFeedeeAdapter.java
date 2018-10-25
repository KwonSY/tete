package honbab.pumkit.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.pumkit.com.data.UserData;
import honbab.pumkit.com.task.AcceptReservTask;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.widget.CircleTransform;
import okhttp3.OkHttpClient;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.OnTouchListener;

public class ReqFeedeeAdapter extends Adapter<ReqFeedeeAdapter.ViewHolder> {

    Context mContext;
    OkHttpClient httpClient;
    public ArrayList<UserData> listViewItemList = new ArrayList<>() ;
//    private RecycledViewPool viewPool;

    String feed_id;

    public ReqFeedeeAdapter() {

    }

    public ReqFeedeeAdapter(Context context, OkHttpClient httpClient, String feed_id, ArrayList<UserData> usersItemList) {
        this.mContext = context;
        this.httpClient = httpClient;

        this.feed_id = feed_id;
        this.listViewItemList = usersItemList;
//        viewPool = new RecycledViewPool();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_reqfeedee, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final UserData data = listViewItemList.get(position);
        Log.e("abc", "data getImg_url = " + data.getImg_url());

        Picasso.get().load(data.getImg_url())
                .placeholder(R.drawable.icon_noprofile_circle)
                .error(R.drawable.icon_noprofile_circle)
                .transform(new CircleTransform())
                .into(holder.image_feedee);
        holder.txt_userName.setText(data.getUser_name());

        holder.btn_accept.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
//                    holder.btn_accept.setBackgroundColor(mContext.getResources(R.color.darkgrey));
//                    holder.btn_accept.setBackgroundColor(Color.GRAY);
                } else if (action == MotionEvent.ACTION_UP) {
//                    holder.btn_accept.setBackgroundColor(Color.WHITE);

                    new AcceptReservTask(mContext, httpClient, holder, data, feed_id, position)
                            .execute(feed_id, data.getUser_id());

                    holder.btn_accept.setText(R.string.acceptComplete);
                }

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_feedee;
        TextView txt_userName;
        Button btn_accept;

        public ViewHolder(View itemView) {
            super(itemView);
            image_feedee = itemView.findViewById(R.id.image_feedee);
            txt_userName = itemView.findViewById(R.id.txt_userName);
            btn_accept = itemView.findViewById(R.id.btn_accept);
        }
    }

//    public void addItem(String user_id, String user_name, String phone, String email, String gender, String img_url, String comment) {
//        FeedReqData item = new UserData(user_id, user_name, phone, email, gender, img_url, comment);
//
//        listViewItemList.add(item);
//    }

    public int findViewHolderforLayoutPosition(final ViewHolder holder, int position) {
//        final FeedReqData data = listViewItemList.get(position);
        int layoutPosition = holder.getLayoutPosition();
//        Log.e("abc", "layoutPosition = " + layoutPosition);
//        View view = mContext.

        return layoutPosition;
    }
}