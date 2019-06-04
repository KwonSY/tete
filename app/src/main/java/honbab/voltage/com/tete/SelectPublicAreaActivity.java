package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import org.json.JSONObject;

import honbab.voltage.com.adapter.SelectPublicAreaAdapter;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SelectPublicAreaActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private SessionManager session;

    RadioButton radio_y, radio_n;

    private String alarm_yn = "y";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_public_area);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());

        RecyclerView recyclerView_area;
        SelectPublicAreaAdapter mAdapter;

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText("지역선택");

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView_area = (RecyclerView) findViewById(R.id.recyclerView_area);
        recyclerView_area.setLayoutManager(layoutManager);
        mAdapter = new SelectPublicAreaAdapter();
        recyclerView_area.setAdapter(mAdapter);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        new MySettingTask().execute();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_change_psw:
                    Intent intent = new Intent(SelectPublicAreaActivity.this, ChangePswActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
            }
        }
    };

    public class MySettingTask extends AsyncTask<Void, Void, Void> {
        String alarm;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "my_setting")
                    .add("my_id", Statics.my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    Log.e("abc", "예약 : " + bodyStr);

                    JSONObject obj = new JSONObject(bodyStr);

                    alarm_yn = obj.getString("alarm");
                } else {
                    Log.d("abc", "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (alarm_yn.equals("y")) {
                radio_y.setChecked(true);
            } else if (alarm_yn.equals("n")) {
                radio_n.setChecked(true);
            }
        }
    }

    public class UpdateAlarmYnTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {
            Log.e("abc", "yn = " + params[0]);
            FormBody body = new FormBody.Builder()
                    .add("opt", "alarm_on_off")
                    .add("my_id", Statics.my_id)
                    .add("yn", params[0])
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    Log.e("abc", "예약 : " + bodyStr);

                    JSONObject obj = new JSONObject(bodyStr);

                    alarm_yn = obj.getString("alarm");
                } else {
                    Log.d("abc", "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            if (alarm_yn.equals("y")) {
//                radio_y.setChecked(true);
//            } else if (alarm_yn.equals("n")) {
//                radio_n.setChecked(true);
//            }
        }
    }

    public void shareKakao(View v) {
//        try {
//            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
//            final KakaoTalkLinkMessageBuilder kakaoBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
//
//            String kakao_message = "여러분의 친구 세영이가 잠도 안 자고 만들었습니다.\n아직 많이 부족하지만, 어플을 통해서 혼밥탈출하시고, 우리 모두 같이먹어요.";
//            kakaoBuilder.addText(kakao_message);
//
////            String url = Statics.main_url + "full-ci.png";
////            kakaoBuilder.addImage(url, 1080, 1920);
//
//            kakaoBuilder.addAppButton("같이먹어요 시작");
//
//            kakaoLink.sendMessage(kakaoBuilder, this);
//        } catch (KakaoParameterException e) {
//            e.printStackTrace();
//        }

//        FeedTemplate
//        TextTemplate params = TextTemplate.newBuilder("Text", LinkObject.newBuilder().setWebUrl("https://developers.kakao.com").setMobileWebUrl("https://developers.kakao.com").build()).setButtonTitle("This is button").build();
//
//        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
//        serverCallbackArgs.put("user_id", "${current_user_id}");
//        serverCallbackArgs.put("product_id", "${shared_product_id}");
//
//        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
//            @Override
//            public void onFailure(ErrorResult errorResult) {
//                Logger.e(errorResult.toString());
//            }
//
//            @Override
//            public void onSuccess(KakaoLinkResponse result) {
//                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
//            }
//        });

        try {
            KakaoLink kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
            KakaoTalkLinkMessageBuilder messageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
            messageBuilder.addText("카카오톡으로 공유해요.");
            kakaoLink.sendMessage(messageBuilder, this);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
    }

//    public static String getKeyHash(final Context context) {
//        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
//        if (packageInfo == null)
//            return null;
//
//        for (Signature signature : packageInfo.signatures) {
//            try {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
//            } catch (NoSuchAlgorithmException e) {
//                Log.w("abc", "Unable to get MessageDigest. signature=" + signature, e);
//            }
//        }
//        return null;
//    }
}