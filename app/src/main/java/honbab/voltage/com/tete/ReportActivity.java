package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import honbab.voltage.com.task.ReportTask;

public class ReportActivity extends AppCompatActivity {
    private EditText edit_report;

    private String feed_id, user_id, user_name, user_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Intent intent = getIntent();
        feed_id = intent.getStringExtra("after_feed_id");
        user_id = intent.getStringExtra("toId");

        ImageView btn_close = (ImageView) findViewById(R.id.btn_close);
//        ImageView img_user = (ImageView) findViewById(R.id.img_user);
//        TextView txt_userName = (TextView) findViewById(R.id.txt_userName);
//        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
//        Button btn_go_main = (Button) findViewById(R.id.btn_go_main);

        btn_close.setOnClickListener(mOnClickListener);
//        ratingBar.setOnClickListener(mOnClickListener);
//        btn_go_main.setOnClickListener(mOnClickListener);

//        Picasso.get().load(user_img)
//                .placeholder(R.drawable.icon_noprofile_circle)
//                .error(R.drawable.icon_noprofile_circle)
//                .transform(new CircleTransform())
//                .into(img_user);
//        txt_userName.setText(user_name + "님과의 식사 괜찮으셨나요?");

        edit_report = (EditText) findViewById(R.id.edit_report);


        Button btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            Float f_rating = ratingBar.getRating();
//            int i_rating = Math.round(f_rating);
//            String s_rating = String.valueOf(i_rating);

            switch (view.getId()) {
                case R.id.btn_close:
                    // status == skip
//                    new EvaluateFeedTask(ReportActivity.this).execute(user_id, feed_id, s_rating, "s");
                    Intent intent = new Intent(ReportActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                    break;
//                case R.id.btn_go_main:
//                    // status == finish
//                    new EvaluateFeedTask(ReportActivity.this).execute(user_id, feed_id, s_rating, "f");
//
//                    break;
                case R.id.btn_submit:
                    String message = edit_report.getText().toString().trim();
                    new ReportTask(ReportActivity.this).execute(user_id, feed_id, message);

                    break;
            }
        }
    };
}