package honbab.voltage.com.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import honbab.voltage.com.tete.R;

public class AlertDialogUtil {

    public static void show(Context mContext, String message, String param) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setMessage(R.string.connect_network);
//        builder.setPositiveButton(R.string.yes,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent mStartActivity = new Intent(mContext, mContext2.getClass());
//                        int mPendingIntentId = 123456;
//                        PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//                        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                        System.exit(0);
//                    }
//                });
//        builder.show();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message);
        final EditText et = new EditText(mContext);
        if (param != null) {
            builder.setView(et);
        }
        builder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String value = et.getText().toString();
//                        Log.v(TAG, value);

                        dialog.dismiss();     //닫기
                    }
                });
        builder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                                holder.btn_check_feedee.setBackgroundResource(R.drawable.icon_check_n);
                    }
                });
        builder.show();
    }
}