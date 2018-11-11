package honbab.pumkit.com.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class MapData implements Serializable {
    private String sid;
    private String user_id, user_name, user_img, user_age, user_gender;
    private String rest_name, location, rest_img;
    private String time;
    private LatLng latLng;
    private String place_id, reference;
    private int height, width;
    private String status;//vvvvvvvvvvv check 필요

    public MapData() {

    }

    public MapData(String place_id, String rest_name, LatLng latLng, String reference, String rest_img) {
        this.place_id = place_id;
        this.rest_name = rest_name;
        this.latLng = latLng;
        this.reference = reference;
        this.rest_img = rest_img;
    }

    public MapData(String sid, String user_id, String user_name, String user_img, String user_age, String user_gender,
                   String rest_name, String location, LatLng latLng, String rest_img,
                   String time, String place_id) {
        this.sid = sid;

        this.user_id = user_id;
        this.user_name = user_name;
        this.user_img = user_img;
        this.user_age = user_age;
        this.user_gender = user_gender;

        this.rest_name = rest_name;
        this.location = location;
        this.latLng = latLng;
        this.rest_img = rest_img;

        this.time = time;
        this.place_id = place_id;
    }

//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.defaultWriteObject();
//        out.writeDouble(latLng.latitude);
//        out.writeDouble(latLng.longitude);
//    }
//
//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//        latLng = new LatLng(in.readDouble(), in.readDouble());
//    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    //User
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

    //Restaurant
    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRest_img() {
        return rest_img;
    }

    public void setRest_img(String rest_img) {
        this.rest_img = rest_img;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}