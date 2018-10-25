package honbab.pumkit.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.squareup.picasso.Picasso;

import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;
import uk.co.senab.photoview.PhotoView;

public class OneImageActivity extends AppCompatActivity {

    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oneimage);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        Intent intent = getIntent();
        String img_url = intent.getStringExtra("img_url");

        PhotoView photoView = (PhotoView) findViewById(R.id.photoView);
//        photoView.setImageResource(R.drawable.image);
        Picasso.get().load(img_url)
                .placeholder(R.drawable.icon_no_image)
                .error(R.drawable.icon_no_image)
                .into(photoView);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        new OneRestTask().execute();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back:
                    onBackPressed();

                    break;
            }
        }
    };
}