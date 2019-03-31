package honbab.voltage.com.data;

import java.io.Serializable;

public class AreaData implements Serializable {
    private String area_cd, area_name;
    private boolean checked = false;
    private int position = -1;

    public AreaData() {
        this.area_cd = null;
        this.area_name = null;
    }

    public AreaData(String area_cd, String area_name) {
        this.area_cd = area_cd;
        this.area_name = area_name;
    }

    public String getArea_cd() {
        return area_cd;
    }

    public void setArea_cd(String area_cd) {
        this.area_cd = area_cd;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
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