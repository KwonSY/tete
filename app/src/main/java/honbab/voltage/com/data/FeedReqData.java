package honbab.voltage.com.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedReqData implements Serializable {
    private String feed_id;
    private String compound_code, feed_time, place_id, status, vicinity;
    private transient LatLng latLng;
    private Double latitude, longtitue;
    private String host_id;
    private String host_name, host_img;
    private String rest_id, rest_name, rest_phone, rest_img;
    private ArrayList<UserData> usersList;
    private ArrayList<CommentData> commentsList;

    public FeedReqData() {

    }

    //예약 전, 음식점만 좋아요
    public FeedReqData(String rest_id, String rest_name, String rest_img, String rest_phone,
                       String compound_code, LatLng latLng, String place_id, String vicinity,
                       ArrayList<UserData> usersList) {
        this.rest_id = rest_id;
        this.rest_name = rest_name;
        this.rest_img = rest_img;
        this.rest_phone = rest_phone;

        this.compound_code = compound_code;
        this.latLng = latLng;
            this.latitude = latLng.latitude;
            this.longtitue = latLng.longitude;
        this.place_id = place_id;
        this.vicinity = vicinity;

        this.usersList = usersList;
    }

    public FeedReqData(String feed_id, String feed_time, String status,
                       String host_id, String host_name, String host_img,
                       String rest_id, String rest_name, String rest_phone, String rest_img,
                       String compound_code, LatLng latLng, String place_id, String vicinity,
                       ArrayList<UserData> usersList) {
        //피드
        this.feed_id = feed_id;
        this.feed_time = feed_time;
        this.status = status;
        //호스트
        this.host_id = host_id;
        this.host_name = host_name;
        this.host_img = host_img;
        //레스토랑
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
        //신청자
        this.usersList = usersList;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeed_time() {
        return feed_time;
    }

    public void setFeed_time(String feed_time) {
        this.feed_time = feed_time;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getCompound_code() {
        return compound_code;
    }

    public void setCompound_code(String compound_code) {
        this.compound_code = compound_code;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
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

    public String getHost_id() {
        return host_id;
    }

    public void setHost_id(String host_id) {
        this.host_id = host_id;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public String getHost_img() {
        return host_img;
    }

    public void setHost_img(String host_img) {
        this.host_img = host_img;
    }

    public String getRest_id() {
        return rest_id;
    }

    public void setRest_id(String rest_id) {
        this.rest_id = rest_id;
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

    public ArrayList<UserData> getUsersList() {
        return usersList;
    }

    public void setUsersList(ArrayList<UserData> usersList) {
        this.usersList = usersList;
    }

    public ArrayList<CommentData> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(ArrayList<CommentData> commentsList) {
        this.commentsList = commentsList;
    }
}