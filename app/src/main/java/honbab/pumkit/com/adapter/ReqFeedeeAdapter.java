package honbab.pumkit.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import honbab.pumkit.com.tete.ProfileActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.widget.CircleTransform;
import okhttp3.OkHttpClient;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.OnTouchListener;

public class ReqFeedeeAdapter extends Adapter<ReqFeedeeAdapter.ViewHolder> {

    Context mContext;
    OkHttpClient httpClient;
    public ArrayList<UserData> listViewItemList = new ArrayList<>();

    String feed_id;

    public ReqFeedeeAdapter() {

    }

    public ReqFeedeeAdapter(Context context, OkHttpClient httpClient, String feed_id, ArrayList<UserData> usersItemList) {
        this.mContext = context;
        this.httpClient = httpClient;

        this.feed_id = feed_id;
        this.listViewItemList = usersItemList;
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
                .into(holder.img_feedee);
        holder.txt_feedee_name.setText(data.getUser_name());

        holder.img_feedee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("user_id", data.getUser_id());
                mContext.startActivity(intent);
            }
        });

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

        //vvvvvvvvvv accept 버튼 바꿔야 함
//        //d - 지웠다. //n - 수락전
//        if (data.getUsersList().get(0).getStatus().equals("n")) {
//            holder.btn_cancle_poke.setText(R.string.cancle);
//        } else {
//            holder.btn_cancle_poke.setText(R.string.poke_reserve);
//        }
//        holder.btn_cancle_poke.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (data.getUsersList().get(0).getStatus().equals("n")) {
//                    // n -> d  지우기
//                    holder.btn_cancle_poke.setText(R.string.repoke);
//                    data.getUsersList().get(0).setStatus("d");
//                } else {
//                    // d -> n 예약하기
//                    holder.btn_cancle_poke.setText(R.string.cancle);
//                    data.getUsersList().get(0).setStatus("n");
//                }
//
//                new PokeFeedTask(mContext, httpClient).execute(data.getFeed_id());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_feedee;
        TextView txt_feedee_name;
        Button btn_accept;

        public ViewHolder(View itemView) {
            super(itemView);

            img_feedee = itemView.findViewById(R.id.img_feedee);
            txt_feedee_name = itemView.findViewById(R.id.txt_feedee_name);
            btn_accept = itemView.findViewById(R.id.btn_accept);
        }
    }

    public int findViewHolderforLayoutPosition(final ViewHolder holder, int position) {
        int layoutPosition = holder.getLayoutPosition();

        return layoutPosition;
    }
}