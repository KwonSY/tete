package honbab.pumkit.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import honbab.pumkit.com.widget.CircleTransform;

public class DelayHandlerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delay_handler);

        final Intent intent = getIntent();
        String user_name = intent.getStringExtra("user_name");
        String user_img = intent.getStringExtra("user_img");

        ImageView img_user = (ImageView) findViewById(R.id.img_user);
        Picasso.get().load(user_img)
                .transform(new CircleTransform())
                .placeholder(R.drawable.icon_noprofile_circle)
                .error(R.drawable.icon_noprofile_circle)
                .into(img_user);

        TextView txt_userName = (TextView) findViewById(R.id.txt_userName);
        txt_userName.setText(user_name);

        TextView txt_text= (TextView) findViewById(R.id.txt_text);
//        Resources res = mContext.getResources();
        String text = String.format(getResources().getString(R.string.complete_reserv_with), user_name);
        txt_text.setText(text);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent intent2 = new Intent(DelayHandlerActivity.this, MainActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent2.putExtra("position", 1);
                startActivity(intent2);
            }
        }, 3500);

        Button btn_go_comment = (Button) findViewById(R.id.btn_go_comment);
        btn_go_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent2 = new Intent(DelayHandlerActivity.this, MainActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent2.putExtra("position", 1);
                startActivity(intent2);
            }
        });
    }

}