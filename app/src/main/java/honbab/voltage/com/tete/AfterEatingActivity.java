package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import honbab.voltage.com.task.EvaluateFeedTask;
import honbab.voltage.com.widget.CircleTransform;

public class AfterEatingActivity extends AppCompatActivity {
    private RatingBar ratingBar;

    private String feed_id, user_id, user_name, user_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aftereating);

        Intent intent = getIntent();
        feed_id = intent.getStringExtra("after_feed_id");
        user_id = intent.getStringExtra("toId");
        user_name = intent.getStringExtra("after_user_name");
        user_img = intent.getStringExtra("after_user_img");

        ImageView btn_close = (ImageView) findViewById(R.id.btn_close);
        ImageView img_user = (ImageView) findViewById(R.id.img_user);
        TextView txt_userName = (TextView) findViewById(R.id.txt_userName);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        Button btn_go_main = (Button) findViewById(R.id.btn_go_main);

        btn_close.setOnClickListener(mOnClickListener);
        ratingBar.setOnClickListener(mOnClickListener);
        btn_go_main.setOnClickListener(mOnClickListener);

        Picasso.get().load(user_img)
                .placeholder(R.drawable.icon_noprofile_circle)
                .error(R.drawable.icon_noprofile_circle)
                .transform(new CircleTransform())
                .into(img_user);
        txt_userName.setText(user_name + "님과의 식사 괜찮으셨나요?");
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Float f_rating = ratingBar.getRating();
            int i_rating = Math.round(f_rating);
            String s_rating = String.valueOf(i_rating);

            switch (view.getId()) {
                case R.id.btn_close:
//                    Intent intent = new Intent(AfterEatingActivity.this, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    finish();

                    // status == skip
                    new EvaluateFeedTask(AfterEatingActivity.this).execute(user_id, feed_id, s_rating, "s");

                    break;
                case R.id.btn_go_main:
                    // status == finish
                    new EvaluateFeedTask(AfterEatingActivity.this).execute(user_id, feed_id, s_rating, "f");

                    break;
            }
        }
    };
}