package honbab.pumkit.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.pumkit.com.tete.GridViewActivity;
import honbab.pumkit.com.tete.OneImageActivity;
import honbab.pumkit.com.tete.R;

public class ViewPagerAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    private Context mContext;

    private ArrayList<Integer> layouts2 = new ArrayList<>();
    private ArrayList<String> img_arr = new ArrayList<>();
    private ArrayList<String> imgAllArr = new ArrayList<>();

    public ViewPagerAdapter(Context mContext, ArrayList<Integer> layouts2, ArrayList<String> img_arr, ArrayList<String> imgAllArr) {
        this.mContext = mContext;
        this.layouts2 = layouts2;
        this.img_arr = img_arr;
        this.imgAllArr = imgAllArr;
    }

    public class ViewHolder {
        ImageView img_food;
        TextView alpha;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.e("abc", "layouts2.get(position) = " + layouts2.get(position));
        View view = layoutInflater.inflate(layouts2.get(position), container, false);

        ViewHolder holder = new ViewHolder();

        holder.img_food = (ImageView) view.findViewById(R.id.img_food);
        holder.alpha = (TextView) view.findViewById(R.id.alpha);

        Log.e("abc", "img_arr.size() = " + img_arr.size());
        if (position < img_arr.size()) {
            Picasso.get().load(img_arr.get(position))
                    .placeholder(R.drawable.icon_no_image)
                    .into(holder.img_food);
        }
        holder.img_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OneImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("img_url", img_arr.get(position));
                mContext.startActivity(intent);
            }
        });

        if (position == 3) {
            holder.alpha.setVisibility(View.VISIBLE);

            holder.alpha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Intent intent = new Intent(mContext, GridViewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("arrayList", imgAllArr);
                        mContext.startActivity(intent);

                }
            });
        }



        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return layouts2.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}