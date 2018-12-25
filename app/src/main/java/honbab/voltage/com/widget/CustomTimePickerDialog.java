package honbab.voltage.com.widget;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CustomTimePickerDialog extends TimePickerDialog {

    private final static int TIME_PICKER_INTERVAL = 15;
    private TimePicker mTimePicker;
    private final OnTimeSetListener mTimeSetListener;

    public CustomTimePickerDialog(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, TimePickerDialog.THEME_HOLO_LIGHT, null, hourOfDay,minute / TIME_PICKER_INTERVAL, is24HourView);
        Log.e("abc", "xx = " + hourOfDay + minute);
        mTimeSetListener = listener;

//        mTimePicker.setCurrentHour(hourOfDay);
    }

    @Override
    public void updateTime(int hourOfDay, int minuteOfHour) {
        Log.e("abc", "updateTime hourOfDay = " + hourOfDay);
        mTimePicker.setCurrentHour(hourOfDay);
        mTimePicker.setCurrentMinute(minuteOfHour / TIME_PICKER_INTERVAL);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                Log.e("abc", "mTimePicker.getCurrentHour() = " + mTimePicker.getCurrentHour());
                Log.e("abc", "mTimePicker.getCurrentMinute() = " + mTimePicker.getCurrentMinute());
                if (mTimeSetListener != null) {
                    mTimeSetListener.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                            mTimePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            mTimePicker = (TimePicker) findViewById(timePickerField.getInt(null));
            Field field = classForid.getField("minute");

            NumberPicker minuteSpinner = (NumberPicker) mTimePicker.findViewById(field.getInt(null));
            minuteSpinner.setMinValue(0);
            minuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minuteSpinner.setDisplayedValues(displayedValues.toArray(new String[displayedValues.size()]));
            Log.e("abc", "displayedValues = " + displayedValues.size());
            Log.e("abc", "displayedValues = " + displayedValues.get(0));
            Log.e("abc", "displayedValues = " + displayedValues.get(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}