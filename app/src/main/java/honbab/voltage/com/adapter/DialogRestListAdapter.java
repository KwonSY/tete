package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.task.PickRestTask;
import honbab.voltage.com.tete.PickRestLikeActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class DialogRestListAdapter extends RecyclerView.Adapter<DialogRestListAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

//    private Fragment fragment;

    public ArrayList<RestData> listViewItemList = new ArrayList<>();

    public DialogRestListAdapter() {

    }

    public DialogRestListAdapter(Context mContext, ArrayList<RestData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.listViewItemList = listViewItemList;

//        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_pick_restlike, parent, false);

        return new ViewHolder(view, viewType);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final RestData data = listViewItemList.get(position);
        data.setPosition(position);

        holder.bindToPost(data, getItemViewType(position));
    }

//    @Override
//    public int getItemViewType(int position) {
////        Log.e("abc", position + ", getItemViewType listViewItemList = " + listViewItemList.size());
////        Log.e("abc", position + " == " + listViewItemList.size());
////        if (listViewItemList.size() == position)
////            return TYPE_END;
////        else
////            return TYPE_TIME;
//
////        return position % 2 == 0 ? VIEW_TYPE_TEXT : VIEW_TYPE_IMAGE;
//    }

    @Override
    public int getItemCount() {

        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView img;
        public TextView txt_restName;
        public CheckBox checkBox;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            img = itemView.findViewById(R.id.img);
            txt_restName = itemView.findViewById(R.id.txt_restName);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void bindToPost(final RestData data, int viewType) {
            Picasso.get().load(data.getRest_img())
                    .placeholder(R.drawable.icon_no_image)
                    .error(R.drawable.icon_no_image)
                    .into(img);

            txt_restName.setText(data.getRest_name());

            Log.e("abc", "첫로딩 checkBox = " +data.getLike_yn());
            if (data.getLike_yn() == null || data.getLike_yn().equals("n"))
                checkBox.setChecked(false);
            else
                checkBox.setChecked(true);

//            cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("abc", "checkBox = " +data.isChecked());
                    if (data.getLike_yn() == null || data.getLike_yn().equals("n")) {
                        // n -> y
                        data.setLike_yn("y");
                        checkBox.setChecked(true);
                        data.setChecked(true);

                    } else if (data.getLike_yn().equals("y")) {
                        // y -> n
                        data.setLike_yn("n");
                        checkBox.setChecked(false);
                        data.setChecked(false);
                    }

//                    new RestLikeTask(mContext).execute(data.getRest_id(), data.getLike_yn());
                    new PickRestTask(mContext).execute(((PickRestLikeActivity) mContext).timelike_id, data.getRest_id());
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
}