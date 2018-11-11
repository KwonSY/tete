package honbab.pumkit.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.widget.CircleTransform;
import okhttp3.OkHttpClient;

public class MyPokeListAdapter extends RecyclerView.Adapter<MyPokeListAdapter.ViewHolder> {
    Context mContext;
    OkHttpClient httpClient;
    public ArrayList<FeedReqData> listViewItemList = new ArrayList<>();

    public MyPokeListAdapter() {

    }

    public MyPokeListAdapter(Context mContext, OkHttpClient httpClient, ArrayList<FeedReqData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.listViewItemList = listViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_my_poke, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FeedReqData data = listViewItemList.get(position);

        holder.txt_restName.setText(data.getRest_name());
        holder.txt_hostName.setText(data.getHost_name());
        Picasso.get().load(data.getHost_img())
                .placeholder(R.drawable.icon_noprofile_circle)
                .error(R.drawable.icon_noprofile_circle)
                .transform(new CircleTransform())
                .into(holder.img_host);

        if (data.getUsersList().size() > 0) {
            ReqFeedeeAdapter mAdapter = new ReqFeedeeAdapter(mContext, httpClient, data.getFeed_id(), data.getUsersList());
            holder.recyclerView.setAdapter(mAdapter);
        } else {

        }

//        //vvvvvvvvvvvvvv MyPokeListAdapter -> ReqFeedeeAdapter 로 이동해야함
//        Log.e("abc", "status = " + data.getUsersList().get(0).getStatus());
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
        TextView txt_no_req;
        TextView txt_restName;

        ImageView img_host;
        TextView txt_hostName;

        Button btn_cancle_poke;
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_no_req = itemView.findViewById(R.id.txt_no_req);
            txt_no_req.setVisibility(View.VISIBLE);
            txt_restName = itemView.findViewById(R.id.txt_restName);

            img_host = itemView.findViewById(R.id.img_host);
            txt_hostName = itemView.findViewById(R.id.txt_hostName);

            recyclerView = itemView.findViewById(R.id.recyclerView_req_feedee);
            btn_cancle_poke = itemView.findViewById(R.id.btn_cancle_poke);

            if (listViewItemList.size() > 0)
                txt_no_req.setVisibility(View.GONE);
        }
    }
}