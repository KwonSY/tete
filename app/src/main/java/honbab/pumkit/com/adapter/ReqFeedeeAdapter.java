package honbab.pumkit.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import honbab.pumkit.com.task.AcceptFeedTask;
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

    int TYPE_FOOTER = 2;

    String feed_id;

    public ReqFeedeeAdapter() {

    }

    public ReqFeedeeAdapter(Context context, OkHttpClient httpClient, String feed_id, ArrayList<UserData> usersItemList) {
        this.mContext = context;
        this.httpClient = httpClient;
        this.feed_id = feed_id;
        this.listViewItemList = usersItemList;
        this.TYPE_FOOTER = listViewItemList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_stub, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_reqfeedee, parent, false);
        }

        return new ViewHolder(view, viewType);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if (holder.holderId != TYPE_FOOTER) {
            Log.e("abc", "holder.holderId = " + holder.holderId);
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
                    //vvvvvvvvvvvvvvv
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("user_id", data.getUser_id());
                    mContext.startActivity(intent);
                }
            });
            holder.btn_check_feedee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = String.format(mContext.getResources().getString(R.string.ask_godmuk_with), data.getUser_name());

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(message);
                    builder.setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new AcceptFeedTask(mContext, httpClient, holder, data, feed_id, position)
                                            .execute(feed_id, data.getUser_id());
                                }
                            });
                    builder.setNegativeButton(R.string.no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

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

//                    holder.btn_accept.setText(R.string.acceptComplete);
                    }

                    return false;
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
        }
    }

    private boolean isPositionFooter(int position) {
        return position == TYPE_FOOTER;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        } else {
            int type = position;
            return type;
        }
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        int holderId;

        ImageView img_feedee;
        public ImageView btn_check_feedee;
        TextView txt_feedee_name;
        Button btn_accept;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType == TYPE_FOOTER) {
                holderId = TYPE_FOOTER;
            } else {
                holderId = 10000;

                img_feedee = itemView.findViewById(R.id.img_feedee);
                btn_check_feedee = itemView.findViewById(R.id.btn_check_feedee);
                txt_feedee_name = itemView.findViewById(R.id.txt_feedee_name);
                btn_accept = itemView.findViewById(R.id.btn_accept);
            }
        }
    }

    public int findViewHolderforLayoutPosition(final ViewHolder holder, int position) {
        int layoutPosition = holder.getLayoutPosition();

        return layoutPosition;
    }

    public void showBtnCheckFeedee(@NonNull ViewHolder holder) {
        //vvvvvvvvvvvvvvv
//        mAdapter.
//        ViewHolder holder = get
        holder.btn_check_feedee.setVisibility(View.VISIBLE);
//        holder.btn_check_feedee.setVisibility(View.VISIBLE);
    }
}