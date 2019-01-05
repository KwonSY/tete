package honbab.voltage.com.tete;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import honbab.voltage.com.network.OnTaskCompleted;
import honbab.voltage.com.task.GetPhotoTask;
import honbab.voltage.com.utils.GoogleMapUtil;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class DelayBefroePickRestActivity extends AppCompatActivity {
    private OkHttpClient httpClient;

    public TextView btn_call_rest;

    private String rest_id, place_id;
    public String formatted_phone_number = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delay_handler);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        final Intent intent = getIntent();
        String user_name = intent.getStringExtra("user_name");
        String user_img = intent.getStringExtra("user_img");
        rest_id = intent.getStringExtra("rest_id");
        place_id = intent.getStringExtra("place_id");

        btn_call_rest = (TextView) findViewById(R.id.btn_call_rest);
        btn_call_rest.setOnClickListener(mOnClickListener);

        ImageView img_user = (ImageView) findViewById(R.id.img_user);
        Picasso.get().load(user_img)
                .transform(new CircleTransform())
                .placeholder(R.drawable.icon_noprofile_circle)
                .error(R.drawable.icon_noprofile_circle)
                .into(img_user);

        TextView txt_userName = (TextView) findViewById(R.id.txt_userName);
        txt_userName.setText(user_name);

        TextView txt_text = (TextView) findViewById(R.id.txt_text);
//        Resources res = mContext.getResources();
        String text = String.format(getResources().getString(R.string.complete_reserv_with), user_name);
        txt_text.setText(text);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent intent2 = new Intent(DelayBefroePickRestActivity.this, MainActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent2.putExtra("position", 1);
                startActivity(intent2);
            }
        }, 7500);

        Button btn_go_comment = (Button) findViewById(R.id.btn_go_comment);
        btn_go_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent2 = new Intent(DelayBefroePickRestActivity.this, MainActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent2.putExtra("position", 1);
                startActivity(intent2);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        String url = "";
//        RestClient client = new RestClient(url);
//        client.addBasicAuthentication(user_name, password);
//        try {
//            client.execute(RequestMethod.GET);
//            if (client.getResponseCode() != 200) {
//                //return server error
//                return client.getErrorMessage();
//            }
//            //return valid data
//            JSONObject jObj = new JSONObject(client.getResponse());
//            return jObj.toString();
//        } catch (Exception e) {
//            return e.toString();
//        }

        String url = GoogleMapUtil.getDetailUrl(getApplicationContext(), place_id);
        OnTaskCompleted onTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String value) {

            }
        };
        new GetPhotoTask(DelayBefroePickRestActivity.this, httpClient, onTaskCompleted, rest_id).execute(url);


//        try {
//
////            String formatted_phone_number = new GetPhotoTask(DelayHandlerActivity.this, null, null).execute(url).get();
////            Log.e("abc", "캬겟써봣다잉 formatted_phone_number = " + formatted_phone_number);
////            btn_call_rest.setText(formatted_phone_number);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//            formatted_phone_number = GetPhotoTask.onTaskCompleted();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_call_rest:
                    String uri = "tel:" + formatted_phone_number;

                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse(uri));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);

                    break;
            }
        }
    };


}