package honbab.voltage.com.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import honbab.voltage.com.task.LoginByUidTask;
import honbab.voltage.com.tete.JoinActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class NoProfileFragment extends Fragment {
    private OkHttpClient httpClient;
    private FirebaseAuth mAuth;

    private EditText edit_email, edit_password;
    private ProgressDialog progressDialog;

    public static NoProfileFragment newInstance(int val) {
        NoProfileFragment fragment = new NoProfileFragment();

        Bundle args = new Bundle();
        args.putInt("val2", val);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_noprofile, container, false);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        mAuth = FirebaseAuth.getInstance();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        initControls();
    }

    private void initControls() {
        edit_email = (EditText) getActivity().findViewById(R.id.edit_email);
        edit_password = (EditText) getActivity().findViewById(R.id.edit_password);

        TextView btn_go_join = (TextView) getActivity().findViewById(R.id.btn_go_join);
        btn_go_join.setOnClickListener(mOnClickListener);

        Button btn_go_login = (Button) getActivity().findViewById(R.id.btn_go_login);
        btn_go_login.setOnClickListener(mOnClickListener);

        progressDialog = new ProgressDialog(getActivity());

        TextView btn_go_findpsw;
        btn_go_findpsw = (TextView) getActivity().findViewById(R.id.btn_go_findpsw);
        btn_go_findpsw.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_join:
                    Intent intent = new Intent(getActivity(), JoinActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
                case R.id.btn_go_login:
                    String email = edit_email.getText().toString().trim();
                    String password = edit_password.getText().toString().trim();

                    if (email.length() == 0) {
                        Toast.makeText(getActivity(), R.string.enter_email, Toast.LENGTH_SHORT).show();
                    } else if (password.length() == 0) {
                        Toast.makeText(getActivity(), R.string.enter_password, Toast.LENGTH_SHORT).show();
                    } else {
//                        new LoginTask(getActivity(), httpClient).execute(email, password);
                        //vvvvvvvvvvvvvvvvvv
//                        new LoginByUidTask(getActivity()).execute(email, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        progressDialog.setMessage("같이먹으러 가는 중...");
                        progressDialog.show();

                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            progressDialog.dismiss();

                                            FirebaseUser user = mAuth.getCurrentUser();
//                                            updateUI(user);
                                            new LoginByUidTask(getActivity()).execute(user.getEmail(), user.getUid());
                                        } else {
                                            progressDialog.dismiss();

                                            Toast.makeText(getActivity(), "이메일과 비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                    }

                    break;
                case R.id.btn_go_findpsw:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("비밀번호 재설정");
                    builder.setMessage("사용하시는 이메일 계정이 무엇인가요?");
                    final EditText et = new EditText(getActivity());
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
                                                        Toast.makeText(getActivity().getApplicationContext(), "이메일을 확인해보세요.\n비밀번호 재설정을 하실 수 있습니다.", Toast.LENGTH_SHORT).show();

                                                        dialog.dismiss();
                                                    } else {
                                                        Toast.makeText(getActivity().getApplicationContext(), "가입하지 않은 이메일입니다.", Toast.LENGTH_SHORT).show();
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
}