package honbab.pumkit.com.data;

public class ReservData {
    private String sid;
    private String user_id;
    private String user_name;
    private String user_profile;
    private String rest_name;
    private String gps;
    private String location;
    private String style;
    private String time;

    public ReservData() {

    }

    public ReservData(String sid, String user_id, String user_name, String user_profile, String rest_name, String gps, String location, String style, String time) {
        this.sid = sid;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_profile = user_profile;
        this.rest_name = rest_name;
        this.gps = gps;
        this.location = location;
        this.style = style;
        this.time = time;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_profile() {
        return user_profile;
    }

    public void setUser_profile(String user_profile) {
        this.user_profile = user_profile;
    }

    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}