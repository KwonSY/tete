package honbab.voltage.com.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.util.ArrayList;

import honbab.voltage.com.adapter.DialogDateListAdapter;
import honbab.voltage.com.data.SelectDateData;
import honbab.voltage.com.task.SelectFeedListTask;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.utils.SpacesItemDecoration;

public class PickDateDialog {

    private Context mContext;
    RecyclerView recyclerView;
    DialogDateListAdapter mAdapter;

    public PickDateDialog(Context mContext) {
        this.mContext = mContext;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(ArrayList<SelectDateData> dateLikeList) {
        final Dialog dlg = new Dialog(mContext);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_pickdate);
        dlg.show();

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) dlg.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DialogDateListAdapter(mContext, dateLikeList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(18));

        final ImageView btn_cancle = (ImageView) dlg.findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SelectFeedListTask(mContext).execute("", "", "", "");

                dlg.dismiss();
            }
        });
    }
}