package honbab.voltage.com.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import honbab.voltage.com.data.CityData;
import honbab.voltage.com.tete.R;

public class DialogTestFragment extends Fragment {

    public DialogTestFragment() {

    }

    public static DialogTestFragment newInstance(ArrayList<CityData> cityList, int position) {
        DialogTestFragment fragment = new DialogTestFragment();

        Bundle args = new Bundle();
        args.putSerializable("param1", cityList);
        args.putInt("param2", position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

////        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
//
//        Log.e("abc", "getArguments() = " + getArguments());
//
//        if (getArguments() != null) {
//            cityList = (ArrayList<CityData>) getArguments().getSerializable("param1");
//            Log.e("abc", "cityListcityList = " + cityList.size());
//            position = (Integer) getArguments().getInt("param2");
//            Log.e("abc", "cityListci position = " + position);
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_dialog_area, container, false);

        return inflater.inflate(R.layout.fragment_dialog_area, container, false);
    }

}