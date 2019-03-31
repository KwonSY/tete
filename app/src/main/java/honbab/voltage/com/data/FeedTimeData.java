package honbab.voltage.com.data;

import java.io.Serializable;

public class FeedTimeData implements Serializable {

    private String time;
    private String timeName;
    private int cnt;
    private String status;

    public FeedTimeData() {

    }

    public FeedTimeData(String time, String timeName, int cnt, String status) {
        this.time = time;
        this.timeName = timeName;
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
}