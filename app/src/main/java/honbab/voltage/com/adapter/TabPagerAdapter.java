package honbab.voltage.com.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import honbab.voltage.com.fragment.MyFeedFragment;
import honbab.voltage.com.fragment.NoProfileFragment;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager mManager;
    private Context mContext;
    public List<Fragment> fragmentList;
    private int tabCount;
    protected int currentPosition = -1;
    protected Fragment currentFragment;

//    public SelectFeedFragment tabFragment1;
    public MyFeedFragment tabFragment2;
    public NoProfileFragment tabFragment2_2;

    public TabPagerAdapter(FragmentManager fm, Context mContext, List<Fragment> fragmentList, int tabCount) {
        super(fm);

        this.mManager = fm;
        this.mContext = mContext;
        this.fragmentList = fragmentList;
        this.tabCount = tabCount;
//        ArrayListFragment.newInstance(tabCount);
    }

    public void addFragment(Fragment fragment)
    {
        fragmentList.add(fragment);
//        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
//                tabFragment1 = new RestLikeFragment();
//                tabFragment1 = new SelectFeedFragment();
//                fm.pa.put(position, tabFragment1);
//                return tabFragment1;
//                return Fragment.instantiate(mContext, String.valueOf(fragmentList.get(position)));
                return SelectFeedFragment.newInstance(position);
            case 1:
                if (Statics.my_id == null) {
                    tabFragment2_2 = new NoProfileFragment();
//                    return tabFragment2_2;
//                    return Fragment.instantiate(mContext, String.valueOf(fragmentList.get(position)));
                    return NoProfileFragment.newInstance(position);
                } else {
                    tabFragment2 = new MyFeedFragment();
//                    return tabFragment2;
//                    return Fragment.instantiate(mContext, String.valueOf(fragmentList.get(position)));
                    return MyFeedFragment.newInstance(position);
                }

            default:
                return null;
        }
//        return ArrayListFragment.newInstance(position);
//        fm.beginTransaction()
//                .replace(R.id.MainFrameLayout,fragmentA,"YOUR_TARGET_FRAGMENT_TAG")
//                .addToBackStack("YOUR_SOURCE_FRAGMENT_TAG").commit();

//        if (position == 0) {
//            fm.beginTransaction()
////                .replace(R.id.MainFrameLayout,fragmentA,"YOUR_TARGET_FRAGMENT_TAG")
//                    .addToBackStack("tab1").commit();
//        } else {
//            fm.beginTransaction().addToBackStack("tab2").commit();
//        }
//
//        return fragmentList.get(position);
    }

    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
//        // save the appropriate reference depending on position
//        switch (position) {
//            case 0:
////                tabFragment1 = (RestLikeFragment) createdFragment;
//                String firstTag = createdFragment.getTag();
////                createdFragment.setTargetFragment(tabFragment1, 0);
//                Log.e("abc", "tab.getTag() = " + tabFragment1.getTag());
//                Log.e("abc", "tab.getTag() = " + firstTag);
//                break;
//            case 1:
//                if (Statics.my_id == null) {
//                    tabFragment2_2 = (NoProfileFragment) createdFragment;
//                    String secondTag = createdFragment.getTag();
////                    createdFragment.setTargetFragment(tabFragment2_2, 1);
//                } else {
//                    tabFragment2 = (MyFeedFragment) createdFragment;
//                    String secondTag = createdFragment.getTag();
////                    createdFragment.setTargetFragment(tabFragment2, 1);
//                }
//                break;
//        }
//        return createdFragment;

        if (mCurTransaction == null) {
            mCurTransaction = mManager.beginTransaction();
        }

        final long itemId = position;

        // Do we already have this fragment?
        String name = makeFragmentName(itemId);
        Fragment fragment = mManager.findFragmentByTag(name);
        if (fragment != null) {
            mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            mCurTransaction.add(container.getId(), fragment, makeFragmentName(itemId));
        }

        if (mCurrentPrimaryItem != fragment) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mManager.executePendingTransactions();
        } else{
            super.finishUpdate(container);
        }
    }

    private String makeFragmentName(long itemId) {
        return "page:" + itemId;
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    private String getFragmentTag(int viewPagerId, int fragmentPosition)
    {
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
    }

//    @Override
//    public void setPrimaryItem(ViewGroup container, int position, Object object) {
//        super.setPrimaryItem(container, position, object);
//        this.currentPosition = position;
//        if (object instanceof Fragment) {
//            this.currentFragment = (Fragment) object;
//        }
//    }
//
//    public int getCurrentPosition() {
//        return currentPosition;
//    }
//
//    public Fragment getCurrentFragment() {
//        return currentFragment;
//    }
//
//    public void replaceFragment(Fragment fragment, int index) {
//        this.getItem(1).setTargetFragment(fragment, 0);
//    }



    public static class ArrayListFragment extends ListFragment {
        int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
//            View tv = v.findViewById(R.id.text);
//            ((TextView)tv).setText("Fragment #" + mNum);
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, 0));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
    }
}