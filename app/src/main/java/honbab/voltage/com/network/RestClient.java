package honbab.voltage.com.network;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static Retrofit retrofit = null;
    private static RetroInterface RETRO_INTERFACE = null;

    protected Context mContext;

    public static Retrofit initRestClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        String mUrl = "http://200volt.com/";

        retrofit = new Retrofit.Builder()
                .baseUrl(mUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static Retrofit getRestClient() {
        if (retrofit == null)
            initRestClient();

        return retrofit;
    }

    public static <S> void createService(Class<S> serviceClass) {
        RETRO_INTERFACE = (RetroInterface) getRestClient().create(serviceClass);
    }

    public static RetroInterface getService() {
        if (RETRO_INTERFACE == null)
            createService(RetroInterface.class);

        return RETRO_INTERFACE;
    }
}