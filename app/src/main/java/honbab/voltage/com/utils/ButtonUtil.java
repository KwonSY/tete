package honbab.voltage.com.utils;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;

public class ButtonUtil extends Activity {

//    static Activity mActivity;
//    static Context mContext;
//
//    public ButtonUtil(Activity mActivity, Context mContext) {
//        this.mActivity = mActivity;
//        this.mContext = mContext;
//    }

    public static void setBackButtonClickListener(final Activity mActivity) {
        ImageView imageView = (ImageView) mActivity.findViewById(R.id.btn_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
                Statics.to_id = null;
            }
        });
    }

}