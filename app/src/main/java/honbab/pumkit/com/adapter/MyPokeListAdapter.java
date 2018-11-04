package honbab.pumkit.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.tete.R;
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

        Log.e("abc", "status = " + data.getUsersList().get(0).getStatus());
        //d - 지웠다. //n - 수락전
        if (data.getUsersList().get(0).getStatus().equals("n")) {
            holder.btn_cancle_poke.setText(R.string.cancle);
        } else {
            holder.btn_cancle_poke.setText(R.string.poke_reserve);
        }
        holder.btn_cancle_poke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.getUsersList().get(0).getStatus().equals("n")) {
                    // n -> d  지우기
                    holder.btn_cancle_poke.setText("다시 " + R.string.poke_reserve);
                    data.getUsersList().get(0).setStatus("d");

                    //vvvvvvvvvvvvvv new PokeTask(),execute();
                } else {
                    // d -> n 예약하기
                    holder.btn_cancle_poke.setText(R.string.cancle);
                    data.getUsersList().get(0).setStatus("n");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_no_req;
        TextView txt_restName;
        Button btn_cancle_poke;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_no_req = itemView.findViewById(R.id.txt_no_req);
            txt_no_req.setVisibility(View.VISIBLE);
            txt_restName = itemView.findViewById(R.id.txt_restName);
            btn_cancle_poke = itemView.findViewById(R.id.btn_cancle_poke);

            if (listViewItemList.size() > 0)
                txt_no_req.setVisibility(View.GONE);
        }
    }
}