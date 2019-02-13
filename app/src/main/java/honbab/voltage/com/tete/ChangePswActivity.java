package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class ChangePswActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private FirebaseUser user;

    EditText edit_previous_psw, edit_new_psw, edit_new_psw_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psw);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        user = FirebaseAuth.getInstance().getCurrentUser();

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
//                            //vvvvvvvvvvvvv
//                            Encryption.setPassword(str_previous_psw);
//                            Encryption.encryption(str_previous_psw);
//                            str_previous_psw = Encryption.getPassword();
//
//                            Encryption.setPassword(str_new_psw);
//                            Encryption.encryption(str_new_psw);
//                            str_new_psw = Encryption.getPassword();
//
//                            new ChangePswTask(ChangePswActivity.this, httpClient,
//                                    str_previous_psw, str_new_psw).execute();
                            Log.e("abc", "Password user.getEmail() = " + user.getEmail());
                            Log.e("abc", "Password str_previous_psw = " + str_previous_psw);
                            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), str_previous_psw);

//                            user.reauthenticate(credential).addOnCompleteListener()
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
//                                                user.updatePassword(str_new_psw).t;
                                                Log.e("abc", "Password str_new_psw = " + str_new_psw);

                                                user.updatePassword(str_new_psw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ChangePswActivity.this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();

                                                            Log.e("abc", "Password updated" + str_new_psw);
                                                            Intent intent = new Intent(ChangePswActivity.this, SettingActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Log.e("abc", "Error password not updated");
                                                            Toast.makeText(ChangePswActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.e("abc", "Error auth failed");
                                                Toast.makeText(ChangePswActivity.this, "다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }

                    } else {
                        Toast.makeText(ChangePswActivity.this, R.string.password_confirm_not_correct, Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };
}