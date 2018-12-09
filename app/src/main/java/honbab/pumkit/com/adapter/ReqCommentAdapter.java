package honbab.pumkit.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.pumkit.com.data.CommentData;
import honbab.pumkit.com.task.SendCommentTask;
import honbab.pumkit.com.tete.ProfileActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.widget.CircleTransform;
import okhttp3.OkHttpClient;

import static android.support.v7.widget.RecyclerView.Adapter;

public class ReqCommentAdapter extends Adapter<ReqCommentAdapter.ViewHolder> {

    Context mContext;
    OkHttpClient httpClient;
    public ArrayList<CommentData> listViewItemList = new ArrayList<>() ;

    String feed_id;
    int TYPE_FOOTER = 2;

    public ReqCommentAdapter() {

    }

    public ReqCommentAdapter(Context context, OkHttpClient httpClient, String feed_id, ArrayList<CommentData> commentsList) {
        this.mContext = context;
        this.httpClient = httpClient;
        this.feed_id = feed_id;
        this.listViewItemList = commentsList;
        this.TYPE_FOOTER = commentsList.size();
        Log.e("abc", "commentsList.size() = " + commentsList.size());
    }

//    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;

        if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_reqcomment, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_reqcomment, parent, false);
        }

        return new ViewHolder(view, viewType);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if (holder.holderId == TYPE_FOOTER) {
            //코멘트 푸터
            //코멘트 입력
            holder.btn_send_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String str_comment = holder.edit_comment.getText().toString();

                    if (!str_comment.trim().equals(""))
                    new SendCommentTask(mContext, httpClient, position).execute(feed_id, str_comment);
                }
            });
        } else {
            //코멘트 리스트
            final CommentData data = listViewItemList.get(position);

            Picasso.get().load(data.getImg_url())
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(holder.image_feedee);
            holder.image_feedee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("user_id", data.getUser_id());
                    mContext.startActivity(intent);
                }
            });
            holder.txt_feedeeName.setText(data.getUser_name());
            holder.txt_comment.setText(data.getComment());

            //코멘트 삭제 vvvvvvvv 나중에
//            holder.txt_comment.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    Log.e("abc", "코멘트 삭제");
//                    if (Statics.my_id.equals(data.getUser_id())) {
//                        new DelCommentTask(mContext, httpClient, position).execute(feed_id, data.getSid());
//                    }
//
//                    return false;
//                }
//            });
        }

    }

    //맨 마지막 일경우 푸터true를 반환한다
    private boolean isPositionFooter(int position) {
        return position == TYPE_FOOTER;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        } else {
            //position은 0번 부터 시작이므로 Header0번시작하고 1번 들어감
//            int type = listViewItemList.get(position - 1).getItemType();
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

        ImageView image_feedee;
        TextView txt_feedeeName, txt_comment;

        EditText edit_comment;
        TextView btn_send_comment;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType == TYPE_FOOTER) {
                holderId = TYPE_FOOTER;

                edit_comment = (EditText) itemView.findViewById(R.id.edit_comment);
                btn_send_comment = (TextView) itemView.findViewById(R.id.btn_send_comment);
            } else {
                holderId = 10000;

                image_feedee = itemView.findViewById(R.id.image_feedee);
                txt_feedeeName = itemView.findViewById(R.id.txt_feedeeName);
                txt_comment = itemView.findViewById(R.id.txt_comment);
            }

        }
    }
}