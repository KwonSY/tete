package honbab.voltage.com.data;

import java.io.Serializable;

public class SelectDateData implements Serializable {
    private String time, timeName, day_of_week;
    private int cnt;
    private String status;
    private boolean checked = false;
    int position;

    public SelectDateData() {

    }

    public SelectDateData(String time, String timeName, String day_of_week, int cnt, String status) {
        this.time = time;
        this.timeName = timeName;
        this.day_of_week = day_of_week;
        this.cnt = cnt;
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeName() {
        return timeName;
    }

    public void setTimeName(String timeName) {
        this.timeName = timeName;
    }

    public String getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(String day_of_week) {
        this.day_of_week = day_of_week;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}