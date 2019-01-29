package honbab.voltage.com.tete;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import honbab.voltage.com.task.ChangePswTask;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.Encryption;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class ChangePswActivity extends AppCompatActivity {
    private OkHttpClient httpClient;

    EditText edit_previous_psw, edit_new_psw, edit_new_psw_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psw);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText(R.string.change_password);

        edit_previous_psw = (EditText) findViewById(R.id.edit_previous_psw);
        edit_new_psw = (EditText) findViewById(R.id.edit_new_psw);
        edit_new_psw_confirm = (EditText) findViewById(R.id.edit_new_psw_confirm);

        Button btn_change_password = (Button) findViewById(R.id.btn_change_password);
        btn_change_password.setOnClickListener(mOnClickListener);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_change_password:
                    String str_previous_psw = edit_previous_psw.getText().toString().trim();
                    String str_new_psw = edit_new_psw.getText().toString().trim();
                    String str_new_psw_confirm = edit_new_psw_confirm.getText().toString().trim();

                    if (str_new_psw.equals(str_new_psw_confirm)) {
                        if (str_new_psw.length() < 8 || str_new_psw.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*")) {
                            Toast.makeText(ChangePswActivity.this, R.string.enter_at_least_8, Toast.LENGTH_SHORT).show();
                        } else {
                            //vvvvvvvvvvvvv
                            Encryption.setPassword(str_previous_psw);
                            Encryption.encryption(str_previous_psw);
                            str_previous_psw = Encryption.getPassword();

                            Encryption.setPassword(str_new_psw);
                            Encryption.encryption(str_new_psw);
                            str_new_psw = Encryption.getPassword();

                            new ChangePswTask(ChangePswActivity.this, httpClient,
                                    str_previous_psw, str_new_psw).execute();
                        }

                    } else {
                        Toast.makeText(ChangePswActivity.this, R.string.password_confirm_not_correct, Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };
}