package honbab.voltage.com.data;

import java.io.Serializable;

public class SelectDateData implements Serializable {
    private String time;
    private int cnt;
    private String status;
    private boolean checked = false;
    int position;

    public SelectDateData() {

    }

    public SelectDateData(String time, int cnt, String status) {
        this.time = time;
        this.cnt = cnt;
        this.status = status;
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