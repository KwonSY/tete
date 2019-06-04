package honbab.voltage.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.SelectPublicTimeActivity;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class PublicEatFragment extends Fragment {

    private OkHttpClient httpClient;


    public PublicEatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_public_eat, container, false);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        initControls();
    }

    private void initControls() {

//        ImageView img_my_pic = (ImageView) getActivity().findViewById(R.id.img_my_pic);
        Button btn_go_select_public = (Button) getActivity().findViewById(R.id.btn_go_select_public);
        btn_go_select_public.setOnClickListener(mOnClickListener);


    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_go_select_public:
                    Intent intent = new Intent(getActivity(), SelectPublicTimeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
            }
        }
    };
}