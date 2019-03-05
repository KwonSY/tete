package honbab.voltage.com.tete;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import honbab.voltage.com.task.JoinCheckTask;
import honbab.voltage.com.task.LoginByUidTask;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.OkHttpClient;

public class LoginActivity extends BaseActivity {
    private OkHttpClient httpClient;
    private SessionManager session;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    private static final int RC_SIGN_IN = 9001;

    private EditText edit_email, edit_password;
    private LoginButton btn_facebook_join;

    private String email, password;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        if (Statics.my_id == null || Statics.my_username == null || Statics.my_username.equals("null"))
            signOut();

        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);
        ImageView btn_show_password = (ImageView) findViewById(R.id.btn_show_password);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_facebook_join = (LoginButton) findViewById(R.id.btn_facebook_join);
        TextView btn_go_join = (TextView) findViewById(R.id.btn_go_join);

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
        btn_login.setOnClickListener(mOnClickListener);
        btn_facebook_join.setOnClickListener(mOnClickListener);
        btn_go_join.setOnClickListener(mOnClickListener);

        progressDialog = new ProgressDialog(this);

        TextView btn_go_findpsw;
        btn_go_findpsw = (TextView) findViewById(R.id.btn_go_findpsw);
        btn_go_findpsw.setOnClickListener(mOnClickListener);
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
                        progressDialog.setMessage("같이먹으러 가는 중...");
                        progressDialog.show();

//                        Encryption.setPassword(password);
//                        Encryption.encryption(password);
//                        password = Encryption.getPassword();
//                        new LoginTask(LoginActivity.this, httpClient).execute(email, password);
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            progressDialog.dismiss();

                                            FirebaseUser user = mAuth.getCurrentUser();
//                                            updateUI(user);
                                            new LoginByUidTask(LoginActivity.this).execute(user.getEmail(), user.getUid());

//                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                            startActivity(intent);
//                                            finish();
                                        } else {
                                            progressDialog.dismiss();

                                            Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

//                        // Choose authentication providers
//                        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                                new AuthUI.IdpConfig.EmailBuilder().build());
//
//                        // Create and launch sign-in intent
//                        startActivityForResult(
//                                AuthUI.getInstance()
//                                        .createSignInIntentBuilder()
//                                        .setAvailableProviders(providers)
//                                        .build(),
//                                RC_SIGN_IN);
                    }

                    break;
                case R.id.btn_go_join:
                    Intent intent2 = new Intent(LoginActivity.this, JoinActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);

                    break;
                case R.id.btn_facebook_join:
                    btn_facebook_join.setReadPermissions("email", "public_profile");
                    btn_facebook_join.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
//                            Log.d("abc", "facebook:onSuccess:" + loginResult.getAccessToken());
                            handleFacebookAccessToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            updateUI(null);
                        }

                        @Override
                        public void onError(FacebookException error) {
                            updateUI(null);
                        }
                    });

                    break;
                case R.id.btn_go_findpsw:
//                    FirebaseAuth auth = FirebaseAuth.getInstance();
//                    String emailAddress = "funsumer@gmail.com";
//                    edit_email.getText().toString().trim();

//                    AlertDialogUtil.show(LoginActivity.this, "사용하시는 이메일 계정으로 비밀번호 재설정 안내를 전송해드립니다.", emailAddress);

//                    mAuth.sendPasswordResetEmail(edit_email.getText().toString().trim())

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("비밀번호 재설정");
                    builder.setMessage("사용하시는 이메일 계정이 무엇인가요?");
                    final EditText et = new EditText(LoginActivity.this);
                    builder.setView(et);
                    builder.setPositiveButton("재설정 메일 보내기",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String emailAddress = et.getText().toString().trim();
//                        Log.v(TAG, value);

                                    mAuth.sendPasswordResetEmail(emailAddress)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "이메일을 확인해보세요.\n비밀번호 재설정을 하실 수 있습니다.", Toast.LENGTH_SHORT).show();

                                                        dialog.dismiss();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "가입하지 않은 이메일입니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
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
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign in succeeded
                updateUI(mAuth.getCurrentUser());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                new LoginByUidTask(LoginActivity.this).execute(user.getEmail(), user.getUid());
            } else {
                // Sign in failed
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            try {
                                JSONObject obj = new JoinCheckTask(LoginActivity.this).execute(user.getEmail()).get();

                                if (obj.getString("result").equals("0")) {
                                    Intent intent = new Intent(LoginActivity.this, JoinFacebookActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("my_uid", user.getUid());
                                    intent.putExtra("my_name", user.getDisplayName());
                                    intent.putExtra("my_email", user.getEmail());
                                    intent.putExtra("my_img", user.getPhotoUrl().toString());
                                    intent.putExtra("my_password", user.getUid());
                                    intent.putExtra("my_token", token);
                                    startActivity(intent);
                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                        } else {
//                            Log.w("abc", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
//            Toast.makeText(this, user.getDisplayName() + user.getUid(), Toast.LENGTH_SHORT).show();
        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.buttonFacebookLogin).setVisibility(View.VISIBLE);
//            findViewById(R.id.buttonFacebookSignout).setVisibility(View.GONE);
        }
    }

}
