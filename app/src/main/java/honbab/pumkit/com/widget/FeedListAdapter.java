package honbab.pumkit.com.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import honbab.pumkit.com.data.ReservData;
import honbab.pumkit.com.tete.R;

public class FeedListAdapter extends BaseAdapter {

    public ArrayList<ReservData> listViewItemList = new ArrayList<ReservData>() ;

    public FeedListAdapter() {
    }

    public FeedListAdapter(ArrayList<ReservData> listViewItemList) {
        this.listViewItemList = listViewItemList;
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    public class ViewHolder {
        TextView txt_userName;
        TextView txt_hashName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_row_hash, parent, false);

            holder = new ViewHolder();
            holder.txt_userName = (TextView) convertView.findViewById(R.id.txt_userName);
            holder.txt_hashName = (TextView) convertView.findViewById(R.id.txt_hashName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ReservData data = listViewItemList.get(position);

        holder.txt_userName.setText(data.getUser_name());
        holder.txt_hashName.setText("#" + data.getLocation());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    public void addItem(String sid, String user_id, String user_name, String img_url, String rest_name, String gps, String location, String style, String time) {
        ReservData item = new ReservData(sid, user_id, user_name, img_url, rest_name, gps, location, style, time);

        listViewItemList.add(item);
    }

    public void clearItemList() {
        listViewItemList.clear();
    }
}