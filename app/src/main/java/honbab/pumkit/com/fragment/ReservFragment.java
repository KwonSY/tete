package honbab.pumkit.com.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.widget.CustomTimePickerDialog;

public class ReservFragment extends Fragment {

    String TAG = "abc";
    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;

    TextView textClock;

    public static ReservFragment newInstance() {
        ReservFragment f = new ReservFragment();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reserv, container, false);

//        return inflater.inflate(R.layout.fragment_reserv, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        initControls();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }



    private void initControls() {
        ImageView btn_gomap = (ImageView) getActivity().findViewById(R.id.btn_gomap);
        btn_gomap.setOnClickListener(mOnClickListener);

        Date currentTime = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(currentTime);
        final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        textClock = (TextView) getActivity().findViewById(R.id.txt_clock);
//        setTimePickerInterval(textClock);
//        textClock.setFormat12Hour(null);
        textClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Date currentTime = Calendar.getInstance().getTime();

                CustomTimePickerDialog dialog = new CustomTimePickerDialog(getActivity(), listener, hourOfDay, minute, false);
//                dialog.updateTime();
                dialog.show();
            }
        });

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

//            View parent = (View) v.getParent().getParent();


            switch (v.getId()) {
                case R.id.btn_gomap:

//                    ViewGroup viewGroup = (ViewGroup) ((ViewGroup) (getActivity().findViewById(android.R.id.content))).getChildAt(0);
//                    viewGroup.setBackgroundColor(Color.YELLOW);

//                    View currentView = v.getRootView();

                    View view2 = (View) ((ViewGroup) v.getParent().getParent()).getChildAt(1);
                    int fragment_id = view2.getId();

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                    transaction.remove(ReservFragment.newInstance());
                    transaction.replace(fragment_id, ReservMapFragment.newInstance());
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.addToBackStack(null);
                    transaction.commit();

//                    getFragmentManager().beginTransaction().replace(fragment_id, reservMapFragment).addToBackStack(null).commit();

//                    int id_fragment = v.getId();
////                    int id_fragment = v.getParent().getLayoutDirection();
////                    v.getParent().g

//
//                    android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                    transaction.replace(id_fragment, new ReservMapFragment());
//
//                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                    transaction.addToBackStack(null);

//                    Fragment fragment = MainActivity.pagerAdapter.getItem(2);
//                    fragment.change();

//                    ReservMapFragment reservMapFragment = new ReservMapFragment();
////                    MainActivity.pagerAdapter.replaceFragment(reservMapFragment, 1);
//                    MainActivity.viewPager.setAdapter(MainActivity.pagerAdapter);
//                    MainActivity.viewPager.setCurrentItem(1);

//                    MainActivity.viewPager.setAdapter();



                    break;
            }
        }
    };

    private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // 설정버튼 눌렀을 때
//            DateFormat dateFormat;
//            dateFormat.format()
            Log.e(TAG, "hourOfDay = " + hourOfDay);
            String timeString;
            if (hourOfDay < 12) {
                timeString = "오전 " + hourOfDay + "시 " + minute + "분";
            } else {
                if (hourOfDay == 12) {

                } else {
                    hourOfDay = hourOfDay - 12;
                }

                timeString = "오후 " + hourOfDay + "시 " + minute + "분";
            }
            textClock.setText(timeString);


//            Toast.makeText(getActivity().getApplicationContext(), hourOfDay + "시 " + minute + "분으로 설정하셨습니다.", Toast.LENGTH_SHORT).show();
        }
    };

}