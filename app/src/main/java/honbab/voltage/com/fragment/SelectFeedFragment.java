package honbab.voltage.com.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import honbab.voltage.com.adapter.SelectDateListAdapter;
import honbab.voltage.com.adapter.SelectRestListAdapter;
import honbab.voltage.com.adapter.SelectUserListAdapter;
import honbab.voltage.com.data.AreaData;
import honbab.voltage.com.task.ReservFeedTask;
import honbab.voltage.com.task.SelectFeedListTask;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.ReservActivity;
import honbab.voltage.com.utils.SpacesItemDecoration;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class SelectFeedFragment extends Fragment {
    private OkHttpClient httpClient;

    public TextView txt_date;
    public RecyclerView recyclerView_date, recyclerView_rest, recyclerView_user;
    public SelectDateListAdapter mAdapter_date;
    public SelectRestListAdapter mAdapter_rest;
    public SelectUserListAdapter mAdapter_user;
    public SwipeRefreshLayout swipeContainer;
    public Spinner spinner;
    public SpinnerAdapter spinnerAdapter;
    public TextView txt_explain_reserv;
    public SlidingUpPanelLayout layout_slidingPanel;

    //    private Calendar calendar;
//    int year, month, day;
//    public String feed_date = "";
    public int split = 2;
    public String area_cd = "GNS1";
    public String feed_time = "";
    public String feed_rest_id = "";
    public String to_id = "", to_name = "";
    public ArrayList<AreaData> areaList;
    public ArrayList<String> areaNameList;

    public ArrayList<String> restLikeList = new ArrayList<>();

    public static SelectFeedFragment newInstance(int val) {
        SelectFeedFragment fragment = new SelectFeedFragment();

        Bundle args = new Bundle();
        args.putInt("val", val);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_selectfeed, container, false);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        areaList = new ArrayList<>();
        areaNameList = new ArrayList<>();
        AreaData areaData = new AreaData("GNS1", "강남역");
        areaList.add(areaData);
        areaNameList.add("강남역");

        initControls();
    }

    @Override
    public void onResume() {
        super.onResume();

        new SelectFeedListTask(getActivity()).execute(feed_time, area_cd, feed_rest_id, "");
    }

    private void initControls() {
//        calendar = Calendar.getInstance();
//        year = calendar.get(Calendar.YEAR);
//        month = calendar.get(Calendar.MONTH) + 1;
//        day = calendar.get(Calendar.DAY_OF_MONTH);
//        String str_date = String.valueOf(month) + "/" + String.valueOf(day);
//
//        TextView txt_date = (TextView) getActivity().findViewById(R.id.txt_date);
//        txt_date.setText(str_date);
//        txt_date.setOnClickListener(mOnClickListener);

        spinner = (Spinner) getActivity().findViewById(R.id.spinner_location);
//        spinnerAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, areaNameList);
        spinnerAdapter = new ArrayAdapter(getActivity(), R.layout.item_row_spinner, areaNameList);
//        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
//        spinner.getBackground().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int r = 0;

                for (int i = 0; i < areaList.size(); i++) {
                    if (areaList.get(i).getArea_name().equals(spinner.getSelectedItem().toString()))
                        r = i;
                }

                area_cd = areaList.get(r).getArea_cd();
                Log.e("abc", "onItemSelected area_cd = " + area_cd);

                new SelectFeedListTask(getActivity()).execute(feed_time, area_cd, feed_rest_id, "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                area_cd = areaList.get(0).getArea_cd();
                Log.e("abc", "onNothingSelected area_cd = " + area_cd);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);

        txt_date = (TextView) getActivity().findViewById(R.id.txt_date);

        recyclerView_date = (RecyclerView) getActivity().findViewById(R.id.recyclerView_date);
        recyclerView_date.setLayoutManager(layoutManager);
        mAdapter_date = new SelectDateListAdapter();
        recyclerView_date.setAdapter(mAdapter_date);
        recyclerView_date.addItemDecoration(new SpacesItemDecoration(10));

        recyclerView_rest = (RecyclerView) getActivity().findViewById(R.id.recyclerView_rest);
        recyclerView_rest.setLayoutManager(layoutManager2);
        mAdapter_rest = new SelectRestListAdapter();
        recyclerView_rest.setAdapter(mAdapter_rest);

        recyclerView_user = (RecyclerView) getActivity().findViewById(R.id.recyclerView_user);
        recyclerView_user.setLayoutManager(gridLayoutManager);
        mAdapter_user = new SelectUserListAdapter();
        recyclerView_user.setAdapter(mAdapter_user);



        txt_explain_reserv = (TextView) getActivity().findViewById(R.id.txt_explain_reserv);

        layout_slidingPanel = (SlidingUpPanelLayout) getActivity().findViewById(R.id.layout_slidingPanel);
        layout_slidingPanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                layout_slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer_feed);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter_date.clearItemList();
                mAdapter_rest.clearItemList();
                mAdapter_user.clearItemList();

//                String str_date = year + String.valueOf(month) + "/" + String.valueOf(day);
                new SelectFeedListTask(getActivity()).execute(feed_time, area_cd, feed_rest_id, "");
            }
        });

    }

    public View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_reserv:
                    new ReservFeedTask(getActivity()).execute(to_id, feed_rest_id, feed_time);
//                    Intent intent2 = new Intent(getActivity(), LoginActivity.class);
//                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent2);

                    break;
//                case R.id.txt_date:
//                    DatePickerDialog dialog = new DatePickerDialog(getActivity(), dateSetListener,
//                            year, month - 1, day);
//                    dialog.show();
//
//                    break;
            }
        }
    };

//    public DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
//        @Override
//        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//            year = i;
//            month = i1 + 1;
//            day = i2;
//
//            calendar.set(Calendar.YEAR, year);
//            calendar.set(Calendar.MONTH, i1);
//            calendar.set(Calendar.DAY_OF_MONTH, day);
//
//            txt_date.setText(month + "/" + day);
//
//            //현재시간보다 이후인지 체크
//            Calendar curCal = Calendar.getInstance();
//            long time_setting = calendar.getTimeInMillis();
//            long time_current = curCal.getTimeInMillis();
//
//            if (time_setting <= time_current) {
//                Toast.makeText(getActivity(), R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
//            }
//        }
//    };

    public void alertShow(final Activity mActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("AlertDialog Title");
        builder.setMessage(R.string.already_reserved_godmuk);
        builder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(getActivity(), MyFeedListActivity.class);
                        Intent intent = new Intent(getActivity(), mActivity.getClass());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(intent);
                    }
                });
        builder.setNegativeButton(R.string.reserve_new_godmuk,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.make_new_godmuk, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), ReservActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(intent);
                    }
                });
        builder.show();
    }

}