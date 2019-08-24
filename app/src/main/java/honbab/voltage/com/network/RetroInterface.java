package honbab.voltage.com.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetroInterface {

    @POST("/account")
    Call<JsonObject> account(@Body JsonObject body);


}