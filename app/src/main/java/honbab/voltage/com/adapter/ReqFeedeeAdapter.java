package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.voltage.com.data.UserData;
import honbab.voltage.com.task.AcceptFeedTask;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.CircleTransform;
import okhttp3.OkHttpClient;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.OnTouchListener;

public class ReqFeedeeAdapter extends Adapter<ReqFeedeeAdapter.ViewHolder> {

    Context mContext;
    OkHttpClient httpClient;
    public ArrayList<UserData> listViewItemList = new ArrayList<>();

    public boolean activate;
    String feed_id, rest_id, place_id, rest_phone;

    public ReqFeedeeAdapter() {

    }

    public ReqFeedeeAdapter(Context context, OkHttpClient httpClient,
                            String feed_id, String rest_id, String place_id, String rest_phone,
                            ArrayList<UserData> usersItemList) {
        this.mContext = context;
        this.httpClient = httpClient;

        this.feed_id = feed_id;
        this.rest_id = rest_id;
        this.place_id = place_id;
        this.rest_phone = rest_phone;

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

            if (activate) {
                holder.btn_check_feedee.setVisibility(View.VISIBLE);
            } else {
                holder.btn_check_feedee.setVisibility(View.GONE);
            }
            holder.btn_check_feedee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = String.format(mContext.getResources().getString(R.string.ask_godmuk_with), data.getUser_name());

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(message);
                    builder.setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new AcceptFeedTask(mContext, httpClient, holder, data, feed_id, rest_id, place_id, position)
                                            .execute(feed_id, data.getUser_id());
                                }
                            });
                    builder.setNegativeButton(R.string.no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.btn_check_feedee.setBackgroundResource(R.drawable.icon_check_n);
                                }
                            });
                    builder.show();
                }
            });
            holder.btn_check_feedee.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();

                    if (action == MotionEvent.ACTION_DOWN) {
                        holder.btn_check_feedee.setBackgroundResource(R.drawable.icon_check_y);
                    } else if (action == MotionEvent.ACTION_UP) {
                        holder.btn_check_feedee.setBackgroundResource(R.drawable.icon_check_y);
                    }

                    return false;
                }
            });
//        }
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_feedee;
        public ImageView btn_check_feedee;
        TextView txt_feedee_name;

        public ViewHolder(View itemView) {
            super(itemView);

                img_feedee = itemView.findViewById(R.id.img_feedee);
                btn_check_feedee = itemView.findViewById(R.id.btn_check_feedee);
                txt_feedee_name = itemView.findViewById(R.id.txt_feedee_name);
        }
    }

    public int findViewHolderforLayoutPosition(final ViewHolder holder, int position) {
        int layoutPosition = holder.getLayoutPosition();

        return layoutPosition;
    }

    public void activateButtons(boolean activate) {
        this.activate = activate;
        notifyDataSetChanged();
    }
}