package honbab.voltage.com.tete;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
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
import honbab.voltage.com.task.LoginTask;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.OkHttpClient;

public class LoginActivity extends BaseActivity {

    private OkHttpClient httpClient;
    private SessionManager session;

    FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    private EditText edit_email, edit_password;
    private LoginButton btn_facebook_join;

    String email, password;

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
        btn_facebook_join = (LoginButton) findViewById(R.id.btn_facebook_join);
        btn_login.setOnClickListener(mOnClickListener);
        btn_facebook_join.setOnClickListener(mOnClickListener);

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
                case R.id.btn_facebook_join:
                    btn_facebook_join.setReadPermissions("email", "public_profile");
                    btn_facebook_join.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.d("abc", "facebook:onSuccess:" + loginResult.getAccessToken());
                            handleFacebookAccessToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            Log.d("abc", "facebook:onCancel");
                            updateUI(null);
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.d("abc", "facebook:onError", error);
                            updateUI(null);
                        }
                    });

                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("abc", "facebook onStart:" + currentUser);
        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("abc", "handleFacebookAccessToken:" + token);


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("abc", "signInWithCredential:success");
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
                            // If sign in fails, display a message to the user.
                            Log.w("abc", "signInWithCredential:failure", task.getException());
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
            Log.e("abc", "getDisplayName = " + user.getDisplayName());
            Log.e("abc", "user.getUid() = " + user.getUid());
            Log.e("abc", "user.getEmail() = " + user.getEmail());
            Log.e("abc", "user.getPhotoUrl() = " + user.getPhotoUrl());
            Log.e("abc", "user.getProviderId() = " + user.getProviderId());
            Log.e("abc", "user.getIdToken() = " + user.getIdToken(true));
            Log.e("abc", "user.getMetadata() = " + user.getMetadata().toString());
            Log.e("abc", "user.getProviders() = " + user.getProviders());
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
