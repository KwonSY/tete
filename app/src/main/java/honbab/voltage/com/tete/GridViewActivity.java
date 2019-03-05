package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

import honbab.voltage.com.adapter.GridViewAdapter;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.SpacesItemDecoration;

public class GridViewActivity extends AppCompatActivity {

    private String rest_name;
    ArrayList<String> arrayList;
    int spacingInPixels = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);

        Intent intent = getIntent();
        rest_name = intent.getStringExtra("rest_name");
        arrayList = (ArrayList<String>) intent.getSerializableExtra("arrayList");
//        Log.e("abc", "ar size = " + arrayList.size());

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        if (rest_name != null)
            title_topbar.setText(rest_name);

        GridLayoutManager layoutManager = new GridLayoutManager(GridViewActivity.this, 2);
        RecyclerView recyclerGridView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerGridView.setLayoutManager(layoutManager);
        GridViewAdapter mAdapter = new GridViewAdapter(GridViewActivity.this, arrayList);
        recyclerGridView.setAdapter(mAdapter);
        recyclerGridView.addItemDecoration(new SpacesItemDecoration(0, 0, 0, 0));

        ButtonUtil.setBackButtonClickListener(this);
    }
}