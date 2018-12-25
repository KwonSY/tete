package honbab.voltage.com.tete;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class TestActivity extends AppCompatActivity {

    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}