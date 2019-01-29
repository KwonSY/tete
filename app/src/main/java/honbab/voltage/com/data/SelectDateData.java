package honbab.voltage.com.data;

import java.io.Serializable;

public class SelectDateData implements Serializable {
    private String time;
    private int cnt;
    private String feed_yn;

    public SelectDateData() {

    }

    public SelectDateData(String time, int cnt, String feed_yn) {
        this.time = time;
        this.cnt = cnt;
        this.feed_yn = feed_yn;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getFeed_yn() {
        return feed_yn;
    }

    public void setFeed_yn(String feed_yn) {
        this.feed_yn = feed_yn;
    }
}