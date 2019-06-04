package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

import honbab.voltage.com.data.SelectDateData;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class SelectPublicAreaAdapter extends RecyclerView.Adapter<SelectPublicAreaAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

    int TYPE_TIME = 1;
//    int TYPE_END = 2;

//    private Fragment fragment;

    public ArrayList<SelectDateData> listViewItemList = new ArrayList<>();
    private int mSelectedItem = -1;
    private AdapterView.OnItemClickListener onItemClickListener;

    public SelectPublicAreaAdapter() {

    }

    public SelectPublicAreaAdapter(Context mContext, ArrayList<SelectDateData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.listViewItemList = listViewItemList;

//        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_select_public_area, parent, false);

        return new ViewHolder(view, viewType, this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_TIME) {

            final SelectDateData data = listViewItemList.get(position);
            data.setPosition(position);

            holder.bindToPost(data, getItemViewType(position));
        } else {
            //TYPE_END
//            holder.bindToPost(null, getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
//        return listViewItemList.size() == position ? TYPE_END : TYPE_TIME;
        return TYPE_TIME;
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SelectPublicAreaAdapter mAdapter;

        public RelativeLayout layout_card;
        public CheckBox checkBox;
        public ImageView img_area;
        public TextView txt_area;

        public ViewHolder(View itemView, int viewType, final SelectPublicAreaAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;

            layout_card = itemView.findViewById(R.id.layout_card);
            img_area = itemView.findViewById(R.id.img_area);
            txt_area = itemView.findViewById(R.id.txt_area);
            checkBox = itemView.findViewById(R.id.checkBox);
//            layout_card.setOnClickListener(this);
//            checkBox.setOnClickListener(this);
            if (viewType == TYPE_TIME) {

            }
        }

        public void bindToPost(final SelectDateData data, int viewType) {
            if (viewType == TYPE_TIME) {
                txt_area.setText(data.getTimeName());

                try {
                    setDateToView(data, data.getPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mSelectedItem = getAdapterPosition();
                        notifyItemRangeChanged(0, listViewItemList.size());
                        mAdapter.onItemHolderClick(ViewHolder.this);
                    }
                });
            } else {
                //TYPE_END
//                layout_card.setBackgroundColor(Color.parseColor("#efefef"));
//                txt_time.setText("시간선택");
//
//                checkBox.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (Integer.parseInt(Statics.my_id) < 1) {
//                            Intent intent = new Intent(mContext, LoginActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            mContext.startActivity(intent);
//                        } else {
//                            PickDateDialog pickDateDialog = new PickDateDialog(mContext);
//                            pickDateDialog.callFunction(((SelectFeedFragment) fragment).dateAllList);
//                        }
//                    }
//                });
            }
        }

        public void setDateToView(SelectDateData data, int position) throws Exception {
//            checkBox.setChecked(position == mSelectedItem);
            Log.e("abc", position + ", mSelectedItem = " + mSelectedItem);

            if (mSelectedItem >= 0) {//선택이 되었으면
                if (position == mSelectedItem) {
                    data.setChecked(true);
                } else {
                    data.setChecked(false);
                }
            } else {//선택이 없으나, 사전에 체크 표기

                if (listViewItemList.size() == 1) {

                    data.setChecked(true);

                } else {
//                    if (listViewItemList.size() == position + 1) {
                    if (position ==  0) {


                        data.setChecked(true);


                    }
                }



            }

            checkBox.setChecked(data.isChecked());
        }

        public void showFancyShowCaseView() {
//            new FancyShowCaseView.Builder(((MainActivity) mContext))
//                    .title("\n\n음식점을 선택해보세요.")
//                    .focusOn(((SelectFeedFragment) fragment).recyclerView_rest.getChildAt(0))
//                    .build()
//                    .show();
        }

//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
////                case R.id.layout_card:
////                    Log.e("abc","layout_card");
////
////
////                    break;
////                case R.id.checkBox:
////                    Log.e("abc","checkBox feed_time = " + ((SelectFeedFragment) fragment).feed_time);
////
////                    try {
////                        Calendar calendar = Calendar.getInstance();
////                        Date date_setting_time = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss").parse(listViewItemList.get(getAdapterPosition()).getTime());
////                        calendar.setTime(date_setting_time);
////
////                        Calendar curCal = Calendar.getInstance();
////                        long time_setting = calendar.getTimeInMillis();
////                        long time_current = curCal.getTimeInMillis();
////
////                        if (time_setting > time_current) {
////                            mSelectedItem = getAdapterPosition();
////                            notifyItemRangeChanged(0, listViewItemList.size());
////                            mAdapter.onItemHolderClick(ViewHolder.this);
////
////                            new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
////                                    ((SelectFeedFragment) fragment).area_cd,
////                                    ((SelectFeedFragment) fragment).feed_rest_id,
////                                    "readOnlyUser");
////                        } else {
////                            checkBox.setVisibility(View.GONE);
////                            Toast.makeText(mContext, R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
////                        }
////                    } catch (ParseException e) {
////                        e.printStackTrace();
////                    }
////
////                    break;
//            }
//        }
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

//    public void activateButtons(boolean activate) {
//        this.activate = activate;
//
//        notifyDataSetChanged();
//    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void onItemHolderClick(SelectPublicAreaAdapter.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}