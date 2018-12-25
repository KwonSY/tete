package honbab.voltage.com.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import honbab.voltage.com.fragment.FeedFragment;
import honbab.voltage.com.fragment.NoProfileFragment;
import honbab.voltage.com.fragment.ProfileFragment;
import honbab.voltage.com.tete.Statics;

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
                if (Statics.my_id == null) {
                    NoProfileFragment tabFragment2 = new NoProfileFragment();
                    return tabFragment2;
                } else {
                    ProfileFragment tabFragment2 = new ProfileFragment();
                    return tabFragment2;
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