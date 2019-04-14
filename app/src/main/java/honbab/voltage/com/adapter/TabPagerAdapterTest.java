package honbab.voltage.com.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import honbab.voltage.com.fragment.NoProfileFragment;

public class TabPagerAdapterTest extends FragmentStatePagerAdapter {

    // Count number of tabs
    private int tabCount;

    public TabPagerAdapterTest(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                NoProfileFragment tabFragment1 = new NoProfileFragment();
                return tabFragment1;
            case 1:
                NoProfileFragment tabFragment2 = new NoProfileFragment();
                return tabFragment2;
            case 2:
                NoProfileFragment tabFragment3 = new NoProfileFragment();
                return tabFragment3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}