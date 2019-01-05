package honbab.voltage.com.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class FeedData implements Serializable {

    private String feed_id, feed_time;
    private String user_id, user_name, user_img, user_age, user_gender, token;

    private String rest_id;
    private String compound_code, place_id, rest_name, rest_phone, rest_img, vicinity;
    private String status;

    private transient LatLng latLng;
    private Double latitude, longtitue;
    private int height, width;

    public FeedData() {

    }

    // parameter 16EA
    public FeedData(String feed_id, String feed_time,
                    String user_id, String user_name, String user_age, String user_gender, String user_img, String token,
                    String rest_id, String rest_name,
                    String compound_code, LatLng latLng, String place_id, String rest_img, String rest_phone, String vicinity,
                    String status) {
        this.feed_id = feed_id;
        this.feed_time = feed_time;

        this.user_id = user_id;
        this.user_name = user_name;
        this.user_age = user_age;
        this.user_gender = user_gender;
        this.token = token;

        this.user_img = user_img;
        this.rest_id = rest_id;
        this.rest_name = rest_name;
        this.compound_code = compound_code;
        this.latLng = latLng;
            this.latitude = latLng.latitude;
            this.longtitue = latLng.longitude;

        this.place_id = place_id;
        this.rest_phone = rest_phone;
        this.rest_img = rest_img;
        this.vicinity = vicinity;
        this.status = status;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    public String getFeed_time() {
        return feed_time;
    }

    public void setFeed_time(String feed_time) {
        this.feed_time = feed_time;
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

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public String getUser_age() {
        return user_age;
    }

    public void setUser_age(String user_age) {
        this.user_age = user_age;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRest_id() {
        return rest_id;
    }

    public void setRest_id(String rest_id) {
        this.rest_id = rest_id;
    }

    public String getCompound_code() {
        return compound_code;
    }

    public void setCompound_code(String compound_code) {
        this.compound_code = compound_code;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public String getRest_phone() {
        return rest_phone;
    }

    public void setRest_phone(String rest_phone) {
        this.rest_phone = rest_phone;
    }

    public String getRest_img() {
        return rest_img;
    }

    public void setRest_img(String rest_img) {
        this.rest_img = rest_img;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitue() {
        return longtitue;
    }

    public void setLongtitue(Double longtitue) {
        this.longtitue = longtitue;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}