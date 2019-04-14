package honbab.voltage.com.data;

import java.io.Serializable;
import java.util.ArrayList;

public class SelectDateData implements Serializable {
    private String timelike_id, time, timeName, day_of_week;
//    private ArrayList<AreaData> areasList;
    private ArrayList<RestData> restList;
    private int cnt;
    private String status;
    private boolean checked = false;
    int position;

    public SelectDateData() {

    }

    public SelectDateData(String timelike_id, String time, String timeName, String day_of_week, ArrayList<RestData> restList, int cnt, String status) {
        this.timelike_id = timelike_id;
        this.time = time;
        this.timeName = timeName;
        this.day_of_week = day_of_week;
        this.restList = restList;
        this.cnt = cnt;
        this.status = status;
    }

    public String getTimelike_id() {
        return timelike_id;
    }

    public void setTimelike_id(String timelike_id) {
        this.timelike_id = timelike_id;
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

    public ArrayList<RestData> getRestList() {
        return restList;
    }

    public void setRestList(ArrayList<RestData> restList) {
        this.restList = restList;
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