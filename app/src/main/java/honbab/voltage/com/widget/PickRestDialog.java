package honbab.voltage.com.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.adapter.DialogRestListAdapter;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.task.SelectFeedListTask;
import honbab.voltage.com.task.UserRestLikeListTask;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;

public class PickRestDialog {

    private Context mContext;
    private Fragment fragment;

    private RecyclerView recyclerView;
    private DialogRestListAdapter mAdapter;

    public PickRestDialog(Context mContext) {
        this.mContext = mContext;
        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    public void callFunction(ArrayList<RestData> restList) {
        final Dialog dlg = new Dialog(mContext);

//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dlg.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //xxxxx
//        mNamingDialog = new NamingDialog(MainActivity.this);
//        mNamingDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//        mNamingDialog.show();
//        mNamingDialog.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.getWindow().getDecorView().getSystemUiVisibility());
//        mNamingDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_pickrest);
        WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.getWindow().setAttributes(params);
//        dlg.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//        dlg.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
//        dlg.getWindow().getAttributes().height = WindowManager.LayoutParams.MATCH_PARENT;

        dlg.show();
        //
//        dlg.getWindow().getDecorView().setSystemUiVisibility(dlg.getWindow().getDecorView().getSystemUiVisibility());
//        dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);


        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) dlg.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        try {
            restList = new UserRestLikeListTask(mContext).execute(((SelectFeedFragment) fragment).area_cd).get();

            mAdapter = new DialogRestListAdapter(mContext, restList);
            recyclerView.setAdapter(mAdapter);
            while (recyclerView.getItemDecorationCount() > 0) {
                recyclerView.removeItemDecorationAt(0);
            }
            recyclerView.measure(WindowManager.LayoutParams.MATCH_PARENT, 350);
            recyclerView.addItemDecoration(new SpacesItemDecoration(1));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        ImageView btn_cancle = (ImageView) dlg.findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
                        ((SelectFeedFragment) fragment).area_cd,
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
                        ((SelectFeedFragment) fragment).area_cd,
                        ((SelectFeedFragment) fragment).feed_rest_id,
                        "");

                dlg.dismiss();
            }
        });
    }
}