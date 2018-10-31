package honbab.pumkit.com.tete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import honbab.pumkit.com.adapter.MyFeedListAdapter;
import honbab.pumkit.com.widget.CircleTransform;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ProfileActivity extends AppCompatActivity {

    private OkHttpClient httpClient;

//    public String my_id = Statics.my_id;

    ImageView img_user;
    TextView txt_my_name, txt_comment;
    ListView listView_myFeed;
    MyFeedListAdapter myFeedListAdapter;

    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        if (user_id==null) {
            user_id = Statics.my_id;
        }
        Log.e("abc", "user_id = " + user_id);

        img_user = (ImageView) findViewById(R.id.img_user);
        txt_my_name = (TextView) findViewById(R.id.txt_my_name);

        txt_comment = (TextView) findViewById(R.id.txt_comment);

//        listView_myFeed = (ListView) findViewById(R.id.listView_myFeed);
//        myFeedListAdapter = new MyFeedListAdapter();
//        listView_myFeed.setAdapter(myFeedListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new MyAccountTask().execute();
    }

    public class MyAccountTask extends AsyncTask<Void, Void, Void> {
        String user_name, user_img, comment;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "account")
                    .add("user_id", user_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "어카운트 : " + obj);

                    JSONObject user_obj = obj.getJSONObject("user");
//                    user_id = user_obj.getString("sid");
                    user_name = user_obj.getString("name");
                    String email = user_obj.getString("email");
                    String gender = user_obj.getString("gender");
                    String age = user_obj.getString("age");
                    user_img = Statics.main_url + user_obj.getString("img_url");
                    comment = user_obj.getString("comment");
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
            txt_my_name.setText(user_name);
            Picasso.get().load(user_img)
                    .resize(200,200)
                    .centerCrop()
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(img_user);
            txt_comment.setText(comment);
        }
    }
}