package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.adapter.AutoCompleteAdapter;
import honbab.voltage.com.adapter.BabFriendsAdapter;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.task.BabFrListTask;
import honbab.voltage.com.task.SearchFrTask;
import honbab.voltage.com.utils.ButtonUtil;

public class BabFriendsActivity extends AppCompatActivity {
//    private OkHttpClient httpClient;

    public RelativeLayout layout_row_babfr;
    public ImageView img_user;
    public TextView txt_userName;

    public RecyclerView recyclerView_fr;
    public BabFriendsAdapter mAdapter;

    public ArrayList<UserData> frList = new ArrayList<UserData>();
    public UserData searchUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_babfriends);

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText("밥친구 리스트");

//        EditText edit_search;
        AutoCompleteTextView edit_search;
        edit_search = (AutoCompleteTextView) findViewById(R.id.edit_search_babfr);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("abc", "CharSequence = " + s);
                try {
                    ArrayList<UserData> userList = new SearchFrTask(BabFriendsActivity.this).execute(s.toString()).get();
                    Log.e("abc", "CharSequence userList = " + userList.size());
                    AutoCompleteAdapter mAdapter = new AutoCompleteAdapter(BabFriendsActivity.this, userList);
                    edit_search.setAdapter(mAdapter);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("abc", "Editable = " + s);


            }
        });


        layout_row_babfr = (RelativeLayout) findViewById(R.id.layout_row_babfr);
        img_user = (ImageView) findViewById(R.id.img_user);
        txt_userName = (TextView) findViewById(R.id.txt_userName);


        ImageButton btn_search;
        btn_search = (ImageButton) findViewById(R.id.btn_search_babfr);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    searchUserData = new SearchFrTask(BabFriendsActivity.this).execute(edit_search.getText().toString().trim()).get();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_fr = (RecyclerView) findViewById(R.id.recyclerView_fr);
        recyclerView_fr.setLayoutManager(layoutManager);
        BabFriendsAdapter mAdapter = new BabFriendsAdapter();
        recyclerView_fr.setAdapter(mAdapter);

//        getPhoneNums();

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        getPhoneNums();
//        new MySettingTask().execute();
        new BabFrListTask(BabFriendsActivity.this).execute();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_user:
                    Intent intent = new Intent(BabFriendsActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("user_id", searchUserData.getUser_id());
                    startActivity(intent);

                    break;
            }
        }
    };

    //전화번호부 가져오기
//    public void getPhoneNums() {
//        String[] arrProjection = {
//                ContactsContract.Contacts._ID,
//                ContactsContract.Contacts.DISPLAY_NAME
//        };
//
//        String[] arrPhoneProjection = {
//                ContactsContract.CommonDataKinds.Phone.NUMBER
//        };
//
//        // get user list
//        Cursor clsCursor = this.getContentResolver().query(
//                ContactsContract.Contacts.CONTENT_URI,
//                arrProjection,
//                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
//                null, null
//        );
//
//        int count = 0;
//
//        while (clsCursor.moveToNext()) {
//            String strContactId = clsCursor.getString(0);
//            String strContactName = clsCursor.getString(1);
//            Log.e("Unity", count + ", 연락처 strContactId : " + strContactId);
//
//            // phone number
//            Cursor clsPhoneCursor = this.getContentResolver().query(
//                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    arrPhoneProjection,
//                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + strContactId,
//                    null, null
//            );
//
//            while (clsPhoneCursor.moveToNext()) {
//                String dial = clsPhoneCursor.getString(0);
//
//                Log.e("Unity", count + ", 연락처 사용자 폰번호 : " + dial);
//                dial = dial.replace("-", "");
//                Log.e("Unity", count + ", 연락처 사용자 dial : " + dial);
////                phones_arr.add(dial);
////                names_arr.add(strContactName);
//                UserData userData = new UserData();
//                userData.setUser_name(strContactName);
//                userData.setPhone(dial);
//                frList.add(userData);
//
//                count++;
//            }
//
//            clsPhoneCursor.close();
//        }
//
//        clsCursor.close();
//
//        BabFriendsAdapter mAdapter = new BabFriendsAdapter(BabFriendsActivity.this, frList);
//        recyclerView_fr.setAdapter(mAdapter);
//    }
}