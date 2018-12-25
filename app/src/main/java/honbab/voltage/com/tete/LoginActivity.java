package honbab.voltage.com.tete;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import honbab.voltage.com.task.LoginTask;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {

    private OkHttpClient httpClient;
    private SessionManager session;

    EditText edit_email, edit_password;

    String email, password;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(getApplicationContext());


        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);

        ImageView btn_show_password = (ImageView) findViewById(R.id.btn_show_password);
        btn_show_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        edit_password.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case MotionEvent.ACTION_UP:
                        edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });

        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(mOnClickListener);

        TextView btn_go_join = (TextView) findViewById(R.id.btn_go_join);
        btn_go_join.setOnClickListener(mOnClickListener);

        progressDialog = new ProgressDialog(this);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_login:
                    String email = edit_email.getText().toString().trim();
                    String password = edit_password.getText().toString().trim();

                    if (email.length() == 0) {
                        Toast.makeText(LoginActivity.this, R.string.enter_email, Toast.LENGTH_SHORT).show();
                    } else if (password.length() == 0) {
                        Toast.makeText(LoginActivity.this, R.string.enter_password, Toast.LENGTH_SHORT).show();
                    } else {
                        new LoginTask(LoginActivity.this, httpClient).execute(email, password);
                    }

                    break;
                case R.id.btn_go_join:
                    Intent intent2 = new Intent(LoginActivity.this, JoinActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);

                    break;
            }
        }
    };
}
