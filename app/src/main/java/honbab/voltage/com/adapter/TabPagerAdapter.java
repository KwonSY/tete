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
        switch (position) {
            case 0:
                RestLikeFragment tabFragment2 = new RestLikeFragment();
                return tabFragment2;
            case 1:
                if (Statics.my_id == null) {
                    NoProfileFragment tabFragment1 = new NoProfileFragment();
                    return tabFragment1;
                } else {
                    MyFeedFragment tabFragment1 = new MyFeedFragment();
                    return tabFragment1;
                }

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