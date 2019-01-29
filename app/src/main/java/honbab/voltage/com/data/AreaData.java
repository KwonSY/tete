package honbab.voltage.com.data;

import java.io.Serializable;

public class AreaData implements Serializable {
    private String area_cd, area_name;

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
}