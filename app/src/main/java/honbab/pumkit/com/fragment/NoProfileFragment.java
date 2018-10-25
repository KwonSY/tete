package honbab.pumkit.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import honbab.pumkit.com.task.LoginTask;
import honbab.pumkit.com.tete.LoginActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class NoProfileFragment extends Fragment {

    private OkHttpClient httpClient;

    EditText edit_email, edit_password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_noprofile, container, false);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

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
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
                case R.id.btn_go_login:
//                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
                    String email = edit_email.getText().toString();
                    String password = edit_password.getText().toString();

                    new LoginTask(getActivity(), httpClient).execute(email, password);

                    break;
            }
        }
    };
}