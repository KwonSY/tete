package honbab.voltage.com.data;

import java.io.Serializable;
import java.util.ArrayList;

public class OneTimeRestLikeListData implements Serializable {
    private String result;
    private ArrayList<CityData> cityList;
    private ArrayList<RestData> restList;
//    private int position = -1;
//    private boolean checked = false;

    public OneTimeRestLikeListData() {

    }

    public OneTimeRestLikeListData(ArrayList<CityData> cityList, ArrayList<RestData> restList) {
        this.cityList = cityList;
        this.restList = restList;
    }

    public ArrayList<CityData> getCityList() {
        return cityList;
    }

    public void setCityList(ArrayList<CityData> cityList) {
        this.cityList = cityList;
    }

    public ArrayList<RestData> getRestList() {
        return restList;
    }

    public void setRestList(ArrayList<RestData> restList) {
        this.restList = restList;
    }
}