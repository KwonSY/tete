package honbab.voltage.com.task;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONObject;

import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class VersionTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

    String version, status, text;
    int vCd;
    String appVersion;
    int appVCd = 0;

    public VersionTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Void... objects) {
        FormBody body = new FormBody.Builder()
                .add("opt", "version")
                .add("device", "android")
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                JSONObject obj = new JSONObject(bodyStr);

                vCd = obj.getInt("vCd");
                version = obj.getString("version");
                status = obj.getString("status");
                text = obj.getString("text");
            } else {
                    Log.d("abc", "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error Version : " + e.getMessage());
            e.printStackTrace();
        }

        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            appVersion = pInfo.versionName;
            appVCd = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
//        super.onPostExecute(result);

            //vCd 배포3, appVCd 2현재
            if (vCd > appVCd) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(text);
                builder.setPositiveButton(R.string.update,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        });
                builder.show();
            } else {

            }
    }

}