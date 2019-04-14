package honbab.voltage.com.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.voltage.com.data.UserData;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.CircleTransform;

public class AutoCompleteAdapter extends ArrayAdapter<UserData> {
    private Context mContext;

    private ArrayList<UserData> items = new ArrayList<>();
    private ArrayList<UserData> itemsAll = new ArrayList<>();
    private ArrayList<UserData> suggestions = new ArrayList<>();


    public AutoCompleteAdapter(Context mContext, ArrayList<UserData> items) {
        super(mContext, 0, items);

        this.mContext = mContext;
//        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();;
        this.items = (ArrayList<UserData>) items.clone();
        this.itemsAll = (ArrayList<UserData>) items.clone();
        this.suggestions = new ArrayList<UserData>();
    }

//    private Filter userFilter = new Filter() {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            FilterResults results = new FilterResults();
//            List<UserData> suggestions = new ArrayList<>();
//
//            if (constraint == null || constraint.length() == 0) {
//                suggestions.addAll(listViewItemList)
//            } else {
//                String filterPattern = constraint.toString().toLowerCase().trim();
//
////                for (UserData item : listViewItemList) {
////                    if (item.getUser_name().contains())
////                }
//            }
//
//            return
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//
//        }
//    };
    @Override
    public Filter getFilter() {
        return usersFilter;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_row_babfriends, parent, false
            );
        }

        ImageView img_user = convertView.findViewById(R.id.img_user);
        TextView txt_userName = convertView.findViewById(R.id.txt_userName);

        UserData data = getItem(position);

        if (data != null) {
            Log.e("abc", "data.getUser_name() = " + data.getUser_name());
            Log.e("abc", "data.getImg_url() = " + data.getImg_url());
            txt_userName.setText(data.getUser_name());

            Picasso.get().load(data.getImg_url())
                    .transform(new CircleTransform())
                    .into(img_user);
        }

        return v;
    }



    Filter usersFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((UserData)(resultValue)).getUser_name();
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (UserData customer : itemsAll) {
                    if(customer.getUser_name().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<UserData> filteredList = (ArrayList<UserData>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (UserData c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}