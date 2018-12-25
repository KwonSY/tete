package honbab.voltage.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.voltage.com.tete.OneImageActivity;
import honbab.voltage.com.tete.R;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {

    private ArrayList<String> listViewitemList = new ArrayList<>();
    private Context mContext;

    public GridViewAdapter(Context context, ArrayList<String> listViewitemList) {
        this.mContext = context;
        this.listViewitemList = listViewitemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_grid, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String rest_img_url = listViewitemList.get(position);
        Log.e("abc", "선택되었음");

        if (rest_img_url.isEmpty()) {
            holder.image.setImageResource(R.drawable.icon_no_image);
        } else {
            Picasso.get().load(listViewitemList.get(position))
                    .resize(100, 100)
                    .centerCrop()
                    .placeholder(R.drawable.icon_no_image)
                    .error(R.drawable.icon_no_image)
                    .into(holder.image);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OneImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("img_url", listViewitemList.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listViewitemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_grid);
        }
    }
}