package honbab.voltage.com.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class NoMyFeedFragment extends Fragment {

    private OkHttpClient httpClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nomyfeed, container, false);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        initControls();
    }

    private void initControls() {
        Button btn_go_my_rest_like = (Button) getActivity().findViewById(R.id.btn_go_my_rest_like);
        btn_go_my_rest_like.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_my_rest_like:
//                    getActivity().viewPager
                    ((MainActivity) getActivity()).viewPager.setCurrentItem(1);

                    break;
            }
        }
    };
}