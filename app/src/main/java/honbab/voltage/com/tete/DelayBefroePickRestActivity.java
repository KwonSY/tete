package honbab.voltage.com.tete;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.adapter.OneImageAdapter;
import honbab.voltage.com.data.AreaData;
import honbab.voltage.com.task.AreaRestTask;
import honbab.voltage.com.task.OneDayFeedCheckTask;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.CustomTimePickerDialog;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class DelayBefroePickRestActivity extends AppCompatActivity {
    private OkHttpClient httpClient;

    public Spinner spinner;
    public SpinnerAdapter spinnerAdapter;
    public TextView title_reserved_rest;
    public RecyclerView recyclerView;
    public OneImageAdapter mAdapter;
    public Button btn_go_pick_rest;

    ArrayList<AreaData> areaList;
    ArrayList<String> areaNameList;
    String feed_location = "SUGNS1", feed_time;
    public String today_status = "n";
    int cnt_reserved = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delay_befre_pick_rest);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();


        areaNameList = new ArrayList<>();
        areaNameList.add("강남역");

        spinner = (Spinner) findViewById(R.id.spinner_location);
        spinnerAdapter = new ArrayAdapter(DelayBefroePickRestActivity.this,
                R.layout.support_simple_spinner_dropdown_item, areaNameList);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int r = 0;

                for (int i = 0; i < areaList.size(); i++) {
                    if (areaList.get(i).getArea_name().equals(spinner.getSelectedItem().toString()))
                        r = i;
                }

                feed_location = areaList.get(r).getArea_cd();
                Log.e("abc", "onItemSelected areaCd = " + feed_location);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                feed_location = areaList.get(0).getArea_name();
                Log.e("abc", "onNothingSelected areaCd = " + feed_location);
            }
        });

        setDrawerReserv();

        btn_go_pick_rest = (Button) findViewById(R.id.btn_go_pick_rest);
        btn_go_pick_rest.setOnClickListener(mOnClickListener);
//        btn_go_pick_rest.setEnabled(false);

        ButtonUtil.setBackButtonClickListener(this);

        title_reserved_rest = (TextView) findViewById(R.id.title_reserved_rest);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_reserved);
        mAdapter = new OneImageAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new ItemDecorator(-640));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            areaList = new AreaRestTask(DelayBefroePickRestActivity.this).execute().get();

            cnt_reserved = doOneDayFeedCheckTask();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_pick_rest:
                    Log.e("abc", "btn_go_pick_rest today_status = " + today_status);
                    if (today_status.equals("y")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DelayBefroePickRestActivity.this);
                        builder.setMessage(R.string.already_reserved_you_cannot_eat);
                        builder.setPositiveButton(R.string.select_diff_day,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.setNegativeButton(R.string.go_to_my_reserved,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(DelayBefroePickRestActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("position", 1);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                        builder.show();
                    } else if(today_status.equals("n")) {
                        DecimalFormat formatter = new DecimalFormat("00");

                        if (cnt_reserved > 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DelayBefroePickRestActivity.this);
                            builder.setMessage(String.format(getResources().getString(R.string.already_reserved_godmuk), String.valueOf(day)));
                            builder.setPositiveButton(R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
//                                            String[] feed_time = {String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(min)};
                                            feed_time = String.valueOf(year) + "-" + formatter.format(month) + "-" + String.valueOf(day) + " "
                                                    + String.valueOf(hour) + ":" + "00:00";

                                            finish();
                                            Intent intent = new Intent(DelayBefroePickRestActivity.this, GodTinderActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("feed_location", feed_location);
                                            intent.putExtra("feed_time", feed_time);
                                            startActivity(intent);
                                        }
                                    });
                            builder.setNegativeButton(R.string.no,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(DelayBefroePickRestActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            builder.show();
                        } else {
//                            String[] feed_time = {String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(min)};
                            feed_time = String.valueOf(year) + "-" + formatter.format(month) + "-" + String.valueOf(day) + " "
                                    + String.valueOf(hour) + ":" + "00:00";

                            finish();
                            Intent intent = new Intent(DelayBefroePickRestActivity.this, GodTinderActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("feed_location", feed_location);
                            intent.putExtra("feed_time", feed_time);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(DelayBefroePickRestActivity.this, "날짜를 다시 확인하세요.", Toast.LENGTH_SHORT);
                    }

                    break;
                case R.id.txt_date:
                    DatePickerDialog dialog = new DatePickerDialog(DelayBefroePickRestActivity.this, dateSetListener,
                            year, month - 1, day);
                    dialog.show();

                    break;
                case R.id.txt_clock:
                    CustomTimePickerDialog dialog2 = new CustomTimePickerDialog(DelayBefroePickRestActivity.this, timeSetListener,
                            hour, min, false);
//                dialog.updateTime();
                    dialog2.show();

                    break;
            }
        }
    };

    private TextView txt_date, txt_clock;
    private Calendar calendar;
    int year, month, day, hour, min;

    private void setDrawerReserv() {
        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_clock = (TextView) findViewById(R.id.txt_clock);

//        Date currentTime = new Date();
        calendar = Calendar.getInstance();
//        calendar.setTime(currentTime);
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 19) {
            calendar.add(Calendar.HOUR, 2);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 19);
        }

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        int ampm = calendar.get(Calendar.AM_PM);
        Log.e("abc", "ampm = " + ampm);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);


        String str_date, str_time;

        if (ampm == 0) {
            str_time = "오전 ";
        } else {
            str_time = "오후 ";
        }

        if (hour > 12) {
            Log.e("abc", "calendar2 = " + calendar.toString());
            str_time += calendar.get(Calendar.HOUR) + "시 ";
        } else {
            if (hour == 0)
                str_time += "12시 ";
            else
                str_time += String.valueOf(hour) + "시 ";
        }

//        if (min < 30) {
//            calendar.set(Calendar.MINUTE, 30);
//            min = 30;
//            str_time += "30분";
//        } else {
//            calendar.set(Calendar.MINUTE, 0);
//            min = 0;
//            str_time += "00분";
//        }
        calendar.set(Calendar.MINUTE, 0);
        min = 0;
        str_date = String.valueOf(month) + "/" + String.valueOf(day);

        txt_date.setText(str_date);
        txt_date.setOnClickListener(mOnClickListener);
        txt_clock.setText(str_time);
        txt_clock.setOnClickListener(mOnClickListener);
    }

    public DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            year = i;
            month = i1 + 1;
            day = i2;

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            txt_date.setText(month + "/" + day);

//            cnt_reserved = doOneDayFeedCheckTask();

            //현재시간보다 이후인지 체크
            Calendar curCal = Calendar.getInstance();
            long time_setting = calendar.getTimeInMillis();
            long time_current = curCal.getTimeInMillis();

            if (time_setting > time_current) {
                btn_go_pick_rest.setEnabled(true);

                cnt_reserved = doOneDayFeedCheckTask();
            } else {
                btn_go_pick_rest.setEnabled(false);
                btn_go_pick_rest.setBackgroundResource(R.drawable.btn_circle_gr1);
                Toast.makeText(getApplicationContext(), R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
            }

        }
    };

    public TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Log.e("abc", "timeSetListener ");
            String str_time;

            hour = hourOfDay;
            min = minute;

            if (hourOfDay < 12) {
                calendar.set(Calendar.AM_PM, 0);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                str_time = "오전 " + hourOfDay + "시";
            } else {
                calendar.set(Calendar.AM_PM, 1);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                if (hourOfDay != 12) {
                    hourOfDay = hourOfDay - 12;
                }

                str_time = "오후 " + hourOfDay + "시";
            }

            txt_clock.setText(str_time);


            //현재시간보다 이후인지 체크
            Calendar curCal = Calendar.getInstance();
            long time_setting = calendar.getTimeInMillis();
            long time_current = curCal.getTimeInMillis();

            if (time_setting > time_current) {
                btn_go_pick_rest.setEnabled(true);

                cnt_reserved = doOneDayFeedCheckTask();
            } else {
                btn_go_pick_rest.setEnabled(false);
                btn_go_pick_rest.setBackgroundResource(R.drawable.btn_circle_gr1);
                Toast.makeText(getApplicationContext(), R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
            }

        }
    };

    private int doOneDayFeedCheckTask() {
        int cnt_reserved = 0;

        try {
            DecimalFormat formatter = new DecimalFormat("00");
            String str_month = formatter.format(month);
            String str_day = formatter.format(day);
            String feed_date = String.valueOf(year) + "-" + str_month + "-" + str_day;
//            String feed_date = year + "-" + month + "-" + day;
//            String feed_date = String.valueOf(calendar.get(Calendar.YEAR)) + calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH);

            cnt_reserved = new OneDayFeedCheckTask(DelayBefroePickRestActivity.this, httpClient).execute(feed_date).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return cnt_reserved;
    }
}