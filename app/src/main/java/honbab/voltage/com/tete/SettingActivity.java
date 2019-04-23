package honbab.voltage.com.tete;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import honbab.voltage.com.task.LeaveTask;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SettingActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private SessionManager session;

    RadioButton radio_y, radio_n;

    private String alarm_yn = "y";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText("세팅");

        radio_y = (RadioButton) findViewById(R.id.radio_y);
        radio_n = (RadioButton) findViewById(R.id.radio_n);
        radio_y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm_yn = "y";
                new UpdateAlarmYnTask().execute(alarm_yn);
            }
        });
        radio_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm_yn = "n";
                new UpdateAlarmYnTask().execute(alarm_yn);
            }
        });

//        Button btn_kakao_invite = (Button) findViewById(R.id.btn_kakao_invite);
//        btn_kakao_invite.setOnClickListener(mOnClickListener);
        Button btn_change_psw = (Button) findViewById(R.id.btn_change_psw);
        Button btn_go_babfrs = (Button) findViewById(R.id.btn_go_babfrs);
        Button btn_ask = (Button) findViewById(R.id.btn_ask);
        Button btn_leave = (Button) findViewById(R.id.btn_leave);
        Button btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_change_psw.setOnClickListener(mOnClickListener);
        btn_go_babfrs.setOnClickListener(mOnClickListener);
        btn_ask.setOnClickListener(mOnClickListener);
        btn_leave.setOnClickListener(mOnClickListener);
        btn_logout.setOnClickListener(mOnClickListener);

//        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
//        btn_back.setOnClickListener(mOnClickListener);
//        btn_back.setOnTouchListener(mOnTouchListener);

        TextView link_privacy = (TextView) findViewById(R.id.link_privacy);
        TextView link_personal = (TextView) findViewById(R.id.link_personal);
        Linkify.TransformFilter mTransform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return "";
            }
        };
        Pattern pattern = Pattern.compile("");
        Linkify.addLinks(link_privacy, pattern, Statics.main_url + "law/privacy/", null, mTransform);
        Linkify.addLinks(link_personal, pattern, Statics.main_url + "law/personal/", null, mTransform);

        TextView txt_app_version = (TextView) findViewById(R.id.txt_app_version);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String appVersion = pInfo.versionName;
            txt_app_version.setText("v" + appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new MySettingTask().execute();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_change_psw:
                    Intent intent = new Intent(SettingActivity.this, ChangePswActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
                case R.id.btn_go_babfrs:
                    Intent intent3 = new Intent(SettingActivity.this, BabFriendsActivity.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent3);

                    break;
                case R.id.btn_ask:
                    Intent intent4 = new Intent(SettingActivity.this, ReportActivity.class);
                    intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent4.putExtra("title", "문의하기");
                    intent4.putExtra("feed_id", "-11");
                    intent4.putExtra("to_id", "ask_for_app");
                    startActivity(intent4);

                    break;
                case R.id.btn_leave:
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setTitle("탈퇴하기");
                    builder.setMessage("'같이먹어요'를 탈퇴하시겠습니까?");
                    builder.setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new LeaveTask(SettingActivity.this).execute();

                                    Toast.makeText(getApplicationContext(), "'같이 먹어요'를 이용해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    builder.setNegativeButton(R.string.no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
//                                holder.btn_check_feedee.setBackgroundResource(R.drawable.icon_check_n);
                                }
                            });
                    builder.show();

                    break;
                case R.id.btn_logout:
                    session.logoutUser();
                    FirebaseAuth.getInstance().signOut();

                    Intent intent2 = new Intent(SettingActivity.this, MainActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);

                    break;
                case R.id.btn_kakao_invite:
//                    try {
//                        final KakaoLink kakaoLink = KakaoLink.getKakaoLink(SettingActivity.this);
//                        final KakaoTalkLinkMessageBuilder kakaoBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
//
//                        String kakao_message = "세영이가 열심히 만들었습니다.\n이 어플을 통해서 혼밥탈출 우리 모두 이겨냅시다.";
//                        kakaoBuilder.addText(kakao_message);
//
//                        String url = Statics.main_url + "full-ci.png";
//                        kakaoBuilder.addImage(url, 1080, 1920);
//
//                        kakaoBuilder.addAppButton("앱 실행");
//
//                        kakaoLink.sendMessage(kakaoBuilder, SettingActivity.this);
//                    } catch (KakaoParameterException e) {
//                        e.printStackTrace();
//                    }


                    //3
//                    try {
//                        KakaoLink link=KakaoLink.getKakaoLink(SettingActivity.this);
//                        KakaoTalkLinkMessageBuilder builder=link.createKakaoTalkLinkMessageBuilder();
//
//                        builder.addText("하하하");
//                        builder.addAppButton("앱 실행하기");
//                        link.sendMessage(builder, getApplicationContext());
//
//                    } catch (KakaoParameterException e) {
//                        e.printStackTrace();
//                    }

//                    try {
//                        KakaoLink link=KakaoLink.getKakaoLink(mContext);
//                        KakaoTalkLinkMessageBuilder builder=link.createKakaoTalkLinkMessageBuilder();
//
//                        builder.addText("ggg");
//                        builder.addAppButton("앱 실행하기");
//                        link.sendMessage(builder,mContext);
//
//                    } catch (KakaoParameterException e) {
//                        e.printStackTrace();
//                    }

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