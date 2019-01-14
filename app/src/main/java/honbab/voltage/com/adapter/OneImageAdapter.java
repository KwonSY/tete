package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.tete.R;

public class OneImageAdapter extends RecyclerView.Adapter<OneImageAdapter.ViewHolder> {
    private Context mContext;
    public ArrayList<RestData> listViewItemList = new ArrayList<>();

    public OneImageAdapter() {

    }

    public OneImageAdapter(Context mContext, ArrayList<RestData> listViewItemList) {
        this.mContext = mContext;
        this.listViewItemList = listViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_image, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Picasso.get().load(listViewItemList.get(position).getRest_img())
                .placeholder(R.drawable.icon_no_image)
                .error(R.drawable.icon_no_image)
                .into(holder.img);
        holder.txt_restName.setText(listViewItemList.get(position).getRest_name());
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView img;
        public TextView txt_restName;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            img = (ImageView) itemView.findViewById(R.id.img);
            txt_restName = (TextView) itemView.findViewById(R.id.txt_restName);
        }
    }
}