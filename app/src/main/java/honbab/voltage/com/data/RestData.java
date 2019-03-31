package honbab.voltage.com.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class RestData implements Parcelable {

    private String rest_id, rest_name, compound_code, place_id, rest_img, rest_phone, vicinity;
    private int cnt;
    private LatLng latLng;
    private double latitude, longtitue;
    private String like_yn;
    private boolean checked = false;
    private int position = -1;

    public RestData() {
        this.rest_id = null;
        this.rest_name = null;
        this.compound_code = null;
        this.latLng = null;
            this.latitude = 0;
            this.longtitue = 0;
        this.place_id = null;
        this.rest_img = null;
        this.rest_phone = null;
        this.vicinity = null;
    }

    // parameter 16EA
    public RestData(String rest_id, String rest_name,
                    String compound_code, LatLng latLng, String place_id, String rest_img, String rest_phone, String vicinity, int cnt) {
        this.rest_id = rest_id;
        this.rest_name = rest_name;
        this.compound_code = compound_code;
        this.latLng = latLng;
        if (latLng != null) {
            this.latitude = latLng.latitude;
            this.longtitue = latLng.longitude;
        } else {
            this.latitude = 0;
            this.longtitue = 0;
        }
        this.place_id = place_id;
        this.rest_img = rest_img;
        this.rest_phone = rest_phone;
        this.vicinity = vicinity;
        this.cnt = cnt;
    }

    protected RestData(Parcel in) {
        rest_id = in.readString();
        rest_name = in.readString();
        compound_code = in.readString();
        place_id = in.readString();
        rest_img = in.readString();
        rest_phone = in.readString();
        vicinity = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        latitude = in.readDouble();
        longtitue = in.readDouble();
//            latitude = latLng.latitude;
//            longtitue = latLng.longitude;
    }

    public static final Creator<RestData> CREATOR = new Creator<RestData>() {
        @Override
        public RestData createFromParcel(Parcel in) {
            return new RestData(in);
        }

        @Override
        public RestData[] newArray(int size) {
            return new RestData[size];
        }
    };

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

    public String getRest_img() {
        return rest_img;
    }

    public void setRest_img(String rest_img) {
        this.rest_img = rest_img;
    }

    public String getRest_phone() {
        return rest_phone;
    }

    public void setRest_phone(String rest_phone) {
        this.rest_phone = rest_phone;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitue() {
        return longtitue;
    }

    public void setLongtitue(double longtitue) {
        this.longtitue = longtitue;
    }

    public String getLike_yn() {
        return like_yn;
    }

    public void setLike_yn(String like_yn) {
        this.like_yn = like_yn;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rest_id);
        dest.writeString(rest_name);
        dest.writeString(compound_code);
        dest.writeString(place_id);
        dest.writeString(rest_img);
        dest.writeString(rest_phone);
        dest.writeString(vicinity);
        dest.writeParcelable(latLng, flags);
        dest.writeDouble(latitude);
        dest.writeDouble(longtitue);
    }

    private void readFromParcel(Parcel in){
        rest_id = in.readString();
        rest_name = in.readString();
        compound_code = in.readString();
        place_id = in.readString();
        rest_img = in.readString();
        rest_phone = in.readString();
        vicinity = in.readString();
//        latLng = in.read;
        latitude = in.readInt();
        longtitue = in.readInt();
    }
}