package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import honbab.voltage.com.data.SelectDateData;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.task.CanclePickDateTask;
import honbab.voltage.com.task.PickDateTask;
import honbab.voltage.com.task.SelectFeedListTask;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class SelectDateListAdapter extends RecyclerView.Adapter<SelectDateListAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

    int TYPE_DATE = 0;
    int TYPE_TIME = 1;

    public ArrayList<SelectDateData> listViewItemList = new ArrayList<>();
    private int split = 2;
    public boolean activate = false;

    public SelectDateListAdapter() {

    }

    public SelectDateListAdapter(Context mContext, ArrayList<SelectDateData> listViewItemList, int split) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.listViewItemList = listViewItemList;
        this.split = split;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_select_date, parent, false);
        Log.e("abc", "onCreateViewHolder (viewType) = " + viewType);
        return new ViewHolder(view, viewType);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final SelectDateData data = listViewItemList.get(position);

//        Log.e("abc", "getItemViewType(position) = " + getItemViewType(position));
        holder.bindToPost(data, getItemViewType(position));
    }

    @Override
    public int getItemViewType(int position) {

        if (position % split == 0) {
            return TYPE_DATE;
        } else {
            return TYPE_TIME;
        }

//        return position % 2 == 0 ? VIEW_TYPE_TEXT : VIEW_TYPE_IMAGE;
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //        int holdId = 0;
        int cnt_touch = 0;

        public RelativeLayout layout_time;
        public ImageView btn_check;
        public TextView txt_date, txt_time, cnt_users;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            layout_time = itemView.findViewById(R.id.layout_time);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_time = itemView.findViewById(R.id.txt_time);
            cnt_users = itemView.findViewById(R.id.cnt_users);
            btn_check = itemView.findViewById(R.id.btn_check);
        }

        public void bindToPost(final SelectDateData data, int viewType) {
            if (viewType == TYPE_DATE) {
                txt_date.setVisibility(View.VISIBLE);

                txt_date.setText(data.getTime().substring(5, 10).replace("-", "/"));
            } else if (viewType == TYPE_TIME) {
                txt_date.setVisibility(View.INVISIBLE);

                if (data.getTime().contains("12:00:00"))
                    txt_time.setText("점심");

                if (data.getTime().contains("19:00:00"))
                    txt_time.setText("저녁");

                cnt_users.setText(String.valueOf(data.getCnt()) + "명");
            } else {
            }

            if (data.getFeed_yn().equals("y")) {
                layout_time.setBackgroundResource(R.drawable.border_round_drgr1);
            } else {
                layout_time.setBackgroundResource(R.drawable.border_round_gr1);
            }

            layout_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("abc", "버튼을 확인 중 cnt_touch = " + cnt_touch);
                    Fragment fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");

                    String restLikeIds = "";
                    String feed_time = data.getTime();

                    for (int i = 0; i < ((SelectFeedFragment) fragment).restLikeList.size(); i++) {
                        if (i > 0)
                            restLikeIds += ",";

                        restLikeIds += ((SelectFeedFragment) fragment).restLikeList.get(i);
                    }

                    if (data.getFeed_yn().equals("y")) {
                        if (cnt_touch == 0) {
                            activateButtons(false);
                            btn_check.setSelected(true);
                            ((SelectFeedFragment) fragment).feed_time = data.getTime();
                        } else if (cnt_touch >= 1) {
                            layout_time.setBackgroundResource(R.drawable.border_round_gr1);
                            btn_check.setSelected(false);

                            new CanclePickDateTask(mContext).execute(restLikeIds, feed_time);
                            data.setFeed_yn("n");

                            cnt_touch = 0;
                        }
                        cnt_touch++;
                    } else {
                        layout_time.setBackgroundResource(R.drawable.border_round_drgr1);
                        btn_check.setSelected(false);

                        new PickDateTask(mContext).execute(restLikeIds, feed_time);
                        data.setFeed_yn("y");

                        cnt_touch = 0;
                    }

                    new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
                            ((SelectFeedFragment) fragment).area_cd,
                            ((SelectFeedFragment) fragment).feed_rest_id,
                            "readOnlyUser");
                }
            });

        }
    }

//    public void removeAt(int position) {
//        listViewItemList.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, listViewItemList.size());
//    }

    public void clearItemList() {
        listViewItemList.clear();
        notifyDataSetChanged();
    }

    public void activateButtons(boolean activate) {
        this.activate = activate;

        notifyDataSetChanged();
    }
}