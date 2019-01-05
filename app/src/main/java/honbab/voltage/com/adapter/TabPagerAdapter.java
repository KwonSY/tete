package honbab.voltage.com.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import honbab.voltage.com.fragment.RestLikeFragment;
import honbab.voltage.com.fragment.MyFeedFragment;
import honbab.voltage.com.fragment.NoProfileFragment;
import honbab.voltage.com.tete.Statics;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager fm;
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.fm = fm;
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        // Returning the current tabs
        switch (position) {
            case 0:
                if (Statics.my_id == null) {
                    NoProfileFragment tabFragment1 = new NoProfileFragment();
                    fm.beginTransaction().addToBackStack("myfeed").commit();
                    return tabFragment1;
                } else {
                    MyFeedFragment tabFragment1 = new MyFeedFragment();
                    fm.beginTransaction().addToBackStack("myfeed").commit();
                    return tabFragment1;
                }
            case 1:
                RestLikeFragment tabFragment2 = new RestLikeFragment();
                fm.beginTransaction().addToBackStack("restlike").commit();
                return tabFragment2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    public void replaceFragment(Fragment fragment, int index) {
        this.getItem(1).setTargetFragment(fragment, 0);
    }
}