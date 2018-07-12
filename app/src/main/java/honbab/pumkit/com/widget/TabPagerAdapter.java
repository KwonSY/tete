package honbab.pumkit.com.widget;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import honbab.pumkit.com.fragment.FeedFragment;
import honbab.pumkit.com.fragment.ProfileFragment;
import honbab.pumkit.com.fragment.ReservMapFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    // Count number of tabs
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                FeedFragment tabFragment1 = new FeedFragment();
                return tabFragment1;
            case 1:
//                ReservFragment tabFragment2 = new ReservFragment();
//                return tabFragment2;
//                return ReservFragment.newInstance();
                return ReservMapFragment.newInstance();
            case 2:
                ProfileFragment tabFragment3 = new ProfileFragment();
                return tabFragment3;
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