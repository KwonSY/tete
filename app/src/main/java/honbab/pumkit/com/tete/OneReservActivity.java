package honbab.pumkit.com.tete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

import honbab.pumkit.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OneReservActivity extends AppCompatActivity {

    private OkHttpClient httpClient;

    String reservId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onereserv);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        Intent intent = getIntent();
        reservId = intent.getStringExtra("reservId");

    }

    @Override
    protected void onResume() {
        super.onResume();

        new OneRestTask().execute();
    }


    public class OneRestTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "one_reserv")
                    .add("reserv_id", "1")
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "원예약 : " + obj);

                    JSONObject obj2 = obj.getJSONObject("reserv");

                    String sid = obj2.getString("sid");

                    JSONObject user_obj = obj2.getJSONObject("user");
                    String user_id = user_obj.getString("sid");
                    String user_name = user_obj.getString("name");

//                    JSONArray hash_arr = obj2.getJSONArray("hash");
//                    for (int i=0; i<hash_arr.length(); i++) {
//                        JSONObject hash_obj = hash_arr.get(i)
//                        String hash_id = hash_obj.getString("sid");
//                        String hash_name = hash_obj.getString("name");
//                    }

                    JSONObject food_obj = obj2.getJSONObject("food");
                    String food_id = food_obj.getString("sid");
                    String food_name = food_obj.getString("name");

                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}