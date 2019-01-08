package honbab.voltage.com.tete;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import honbab.voltage.com.task.OneDayFeedCheckTask;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.CustomTimePickerDialog;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class DelayBefroePickRestActivity extends AppCompatActivity {
    private OkHttpClient httpClient;

    String feed_location, feed_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delay_befre_pick_rest);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();



        ArrayList<String> locationList = new ArrayList<>();
        locationList.add("강남역");

        Spinner spinner = (Spinner) findViewById(R.id.spinner_location);
        SpinnerAdapter spinnerAdapter = new ArrayAdapter(DelayBefroePickRestActivity.this,
                R.layout.support_simple_spinner_dropdown_item, locationList);
        spinner.setAdapter(spinnerAdapter);

        setDrawerReserv();

        Button btn_go_pick_rest = (Button) findViewById(R.id.btn_go_pick_rest);
        btn_go_pick_rest.setOnClickListener(mOnClickListener);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DecimalFormat formatter = new DecimalFormat("00");
        String str_month = formatter.format(month);
        String str_day = formatter.format(day);
        feed_time = year + "-" + str_month + "-" + str_day;
        new OneDayFeedCheckTask(DelayBefroePickRestActivity.this, httpClient).execute("GNS1", feed_time);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_pick_rest:
                    String[] feed_time = {String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(min)};

                    finish();
                    Intent intent = new Intent(DelayBefroePickRestActivity.this, GodTinderActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("feed_location", "GNS1");
                    intent.putExtra("feed_time", feed_time);
                    startActivity(intent);

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
//        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
//        View view = nav_view.getHeaderView(0);

        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_clock = (TextView) findViewById(R.id.txt_clock);

        Date currentTime = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.HOUR, 2);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        int ampm = calendar.get(Calendar.AM_PM);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        String str_date, str_time;
        if (ampm == 0) {
            str_time = "오전 ";
        } else {
            str_time = "오후 ";
        }

        if (hour > 12) {
            str_time += String.valueOf(hour - 12) + "시 ";
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

            txt_date.setText(month + "/" + i2);

            DecimalFormat formatter = new DecimalFormat("00");
            String str_month = formatter.format(month);
            String str_day = formatter.format(day);
            feed_time = year + "-" + str_month + "-" + str_day;
            new OneDayFeedCheckTask(DelayBefroePickRestActivity.this, httpClient).execute("GNS1", feed_time);
        }
    };

    public TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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

                if (hourOfDay == 12) {

                } else {
                    hourOfDay = hourOfDay - 12;
                }

                str_time = "오후 " + hourOfDay + "시";
            }

            txt_clock.setText(str_time);

            DecimalFormat formatter = new DecimalFormat("00");
            String str_month = formatter.format(month);
            String str_day = formatter.format(day);
            feed_time = year + "-" + str_month + "-" + str_day;
            new OneDayFeedCheckTask(DelayBefroePickRestActivity.this, httpClient).execute("GNS1", feed_time);
        }
    };
}