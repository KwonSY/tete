package honbab.voltage.com.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.task.PickDateTask;
import honbab.voltage.com.task.SelectFeedListTask;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;

public class PickTimeDialog {
    private Context mContext;
    private Fragment fragment;

    public PickTimeDialog(Context mContext) {
        this.mContext = mContext;
        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String data) {
        final Dialog dlg = new Dialog(mContext);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_picktime);
        dlg.show();

        TextView btn_lunch = (TextView) dlg.findViewById(R.id.btn_lunch);
        TextView btn_dinner = (TextView) dlg.findViewById(R.id.btn_dinner);
        btn_lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String datetime = data  + " " + "15:00:00";
                new PickDateTask(mContext).execute(datetime);
                new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
//                        ((SelectFeedFragment) fragment).area_cd,
                        ((SelectFeedFragment) fragment).feed_rest_id,
                        "");

                dlg.dismiss();
            }
        });
        btn_dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String datetime = data  + " " + "21:00:00";
                new PickDateTask(mContext).execute(datetime);
                new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
//                        ((SelectFeedFragment) fragment).area_cd,
                        ((SelectFeedFragment) fragment).feed_rest_id,
                        "");

                dlg.dismiss();
            }
        });

//        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
//        recyclerView = (RecyclerView) dlg.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(layoutManager);
//        mAdapter = new DialogDateListAdapter(mContext, dateLikeList);
//        recyclerView.setAdapter(mAdapter);
//        recyclerView.addItemDecoration(new SpacesItemDecoration(18));
//
//        final ImageView btn_cancle = (ImageView) dlg.findViewById(R.id.btn_cancle);
//        btn_cancle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
//                        ((SelectFeedFragment) fragment).area_cd,
//                        ((SelectFeedFragment) fragment).feed_rest_id,
//                        "");
//
//                dlg.dismiss();
//            }
//        });
    }
}