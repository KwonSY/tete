package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.task.SelectFeedListTask;
import honbab.voltage.com.tete.LoginActivity;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.OneRestaurantActivity;
import honbab.voltage.com.tete.PickRestLikeActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class SelectRestListAdapter extends RecyclerView.Adapter<SelectRestListAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

    int TYPE_REST = 0;
    int TYPE_END = 1;
    int i_touch = 0;

    private Fragment fragment;
    private ArrayList<RestData> listViewItemList = new ArrayList<>();
    private int mSelectedItem = -1;
    private AdapterView.OnItemClickListener onItemClickListener;

    public SelectRestListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public SelectRestListAdapter(Context mContext, ArrayList<RestData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.listViewItemList = listViewItemList;

        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_select_rest, parent, false);

        return new ViewHolder(view, viewType, this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_REST) {
            final RestData data = listViewItemList.get(position);
            data.setPosition(position);

            holder.bindToPost(data, getItemViewType(position));
        } else {
            // TYPE_END
            holder.bindToPost(null, getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return listViewItemList.size() == position ? TYPE_END : TYPE_REST;
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SelectRestListAdapter mAdapter;

        public RelativeLayout layout_card;

        public ImageView img_rest;
        public TextView txt_restName, txt_cnt;
        public RelativeLayout layout_check;
        public CheckBox checkBox;

        public ViewHolder(View itemView, int viewType, final SelectRestListAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;

            layout_card = itemView.findViewById(R.id.layout_card);
            img_rest = itemView.findViewById(R.id.img_rest);
            txt_restName = itemView.findViewById(R.id.txt_restName);
            txt_cnt = itemView.findViewById(R.id.txt_cnt);

            layout_check = itemView.findViewById(R.id.layout_check);
            checkBox = itemView.findViewById(R.id.checkBox);
            layout_card.setOnClickListener(this);
            checkBox.setOnClickListener(this);
        }

        public void bindToPost(final RestData data, int viewType) {
            if (viewType == TYPE_REST) {
                Picasso.get().load(data.getRest_img())
                        .placeholder(R.drawable.icon_no_image)
                        .error(R.drawable.icon_no_image)
                        .into(img_rest);

//                checkBox.setChecked(data.isChecked());
                checkBox.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(mContext, OneRestaurantActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("feed_id", data.getFeed_id());
                        intent.putExtra("rest_name", data.getRest_name());
                        intent.putExtra("compound_code", data.getCompound_code());
                        intent.putExtra("rest_phone", data.getRest_phone());
//                    intent.putExtra("feed_time", data.getFeed_time());
                        intent.putExtra("place_id", data.getPlace_id());
                        intent.putExtra("vicinity", data.getVicinity());
                        intent.putExtra("latLng", data.getLatLng());

//                    intent.putExtra("feeder_id", data.getUser_id());
//                    intent.putExtra("feeder_name", data.getUser_name());
//                    intent.putExtra("feeder_img", Statics.main_url + data.getUser_img());
//                    intent.putExtra("status", data.getStatus());
                        mContext.startActivity(intent);

                        return false;
                    }
                });
                txt_restName.setText(data.getRest_name());
                if (data.getCnt() > 0)
                    txt_cnt.setText(data.getCnt() + "명");

                try {
                    setDateToView(data, data.getPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                // TYPE_END
//                layout_card.setBackgroundColor(Color.parseColor("#efefef"));
                img_rest.setVisibility(View.GONE);
                txt_restName.setVisibility(View.GONE);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Integer.parseInt(Statics.my_id) < 1) {
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, PickRestLikeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("timelike_id", ((SelectFeedFragment) fragment).timelike_id);
                            ((MainActivity) mContext).overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
                            mContext.startActivity(intent);



//                            PickRestDialog dialog = new PickRestDialog(mContext);
//                            dialog.callFunction();

//                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//                            lp.copyFrom(getWindow().getAttributes());
//                            lp.copyFrom(((Activity) mContext).getWindow().getAttributes());
//                            lp.copyFrom(((PickRestDialog) mContext).getWindow().getAttributes());
//                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;


//                            mNamingDialog = new NamingDialog(MainActivity.this);
//                            mNamingDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//                            mNamingDialog.show();
//                            mNamingDialog.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.getWindow().getDecorView().getSystemUiVisibility());
//                            mNamingDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


                        }
                    }
                });
            }
        }

        public void setDateToView(RestData data, int position) throws Exception {
            Log.e("abc", "Rest mSelectedItem = " + mSelectedItem);
//            Log.e("abc", position + " , SELECTREST data.isChecked() = " + data.isChecked() + ", mSelectedItem = " + mSelectedItem);

//            checkBox.setChecked(position == mSelectedItem);

            if (mSelectedItem >= 0) {//선택이 되었으면
                if (position == mSelectedItem) {
                    ((SelectFeedFragment) fragment).feed_rest_id = data.getRest_id();
                    ((SelectFeedFragment) fragment).txt_explain_rest.setText(String.format(mContext.getResources().getString(R.string.explain_choose_rest), data.getRest_name()));

                    if (((SelectFeedFragment) fragment).feed_time.equals("")) {
                        ((SelectFeedFragment) fragment).txt_explain_pick.setText(R.string.select_feed_time);
                    } else if (((SelectFeedFragment) fragment).feed_rest_id.equals("")) {
                        ((SelectFeedFragment) fragment).txt_explain_pick.setText(R.string.select_feed_rest);
                    } else if (((SelectFeedFragment) fragment).to_id.equals("")) {
                        ((SelectFeedFragment) fragment).txt_explain_pick.setText("");
                    } else {
                        ((SelectFeedFragment) fragment).txt_explain_pick.setText("");
                    }

                    data.setChecked(true);
//                checkBox.setChecked(data.isChecked());

                    new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
//                            ((SelectFeedFragment) fragment).area_cd,
                            ((SelectFeedFragment) fragment).feed_rest_id,
                            "readOnlyUser");
                } else {
                    data.setChecked(false);
//                checkBox.setChecked(data.isChecked());
                }

            } else {//선택이 없으나, 사전에 체크 표기
//                Log.e("abc", "feed_time = " + ((SelectFeedFragment) fragment).feed_time + ", feed_rest_id = "+((SelectFeedFragment) fragment).feed_rest_id);
                if (data.isChecked()) {
//                    ((SelectFeedFragment) fragment).feed_rest_id = data.getRest_id();

                    new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
//                            ((SelectFeedFragment) fragment).area_cd,
                            ((SelectFeedFragment) fragment).feed_rest_id,
                            "readOnlyUser");
                }
            }

            checkBox.setChecked(data.isChecked());
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checkBox:
                    mSelectedItem = getAdapterPosition();
                    notifyItemRangeChanged(0, listViewItemList.size());
                    mAdapter.onItemHolderClick(ViewHolder.this);

                    if (i_touch == 0) {
                        Toast.makeText(mContext, "길게 누르면,\n음식점 상세를 볼 수 있습니다.", Toast.LENGTH_SHORT).show();
                        i_touch++;
                    }

                    break;
            }
        }
    }

    public void clearItemList() {
        listViewItemList.clear();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void onItemHolderClick(ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}