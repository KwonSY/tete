package honbab.voltage.com.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import honbab.voltage.com.adapter.DialogDateListAdapter;
import honbab.voltage.com.data.SelectDateData;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.task.SelectFeedListTask;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;

public class PickDateDialog {
    private Context mContext;
    private Fragment fragment;

    RecyclerView recyclerView;
    DialogDateListAdapter mAdapter;

    public PickDateDialog(Context mContext) {
        this.mContext = mContext;
        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(ArrayList<SelectDateData> dateAllList) {
        final Dialog dlg = new Dialog(mContext);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_pickdate);
        dlg.show();

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) dlg.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DialogDateListAdapter(mContext, dateAllList);
        recyclerView.setAdapter(mAdapter);
        while (recyclerView.getItemDecorationCount() > 0) {
            recyclerView.removeItemDecorationAt(0);
        }
        recyclerView.addItemDecoration(new SpacesItemDecoration(18));

        ImageView btn_cancle = (ImageView) dlg.findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
//                        ((SelectFeedFragment) fragment).area_cd,
                        ((SelectFeedFragment) fragment).feed_rest_id,
                        "");

                dlg.dismiss();
            }
        });

        Button btn_go_return = (Button) dlg.findViewById(R.id.btn_go_return);
        btn_go_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
//                        ((SelectFeedFragment) fragment).area_cd,
                        ((SelectFeedFragment) fragment).feed_rest_id,
                        "");

                dlg.dismiss();
            }
        });
    }
}