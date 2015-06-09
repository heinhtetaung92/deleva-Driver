package knayi.delevadriver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;



public class TabMainFragment extends Fragment  implements Thread.UncaughtExceptionHandler {

    //private View mHeaderView;
    private View mToolbarView;
    private int mBaseTranslationY;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    SharedPreferences sPref;

    int scrollY = 0;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_tab_main, container, false);

        sPref = getActivity().getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);

        mPagerAdapter = new NavigationAdapter(getActivity().getSupportFragmentManager());
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        //overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);



        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mPager);

        return view;
    }













    private Fragment getCurrentFragment() {
        return mPagerAdapter.getItemAt(mPager.getCurrentItem());
    }



    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.i("Error", "Memory Heap error");
    }

    private static class NavigationAdapter extends FragmentStatePagerAdapter {

        private static final String[] TITLES = new String[]{"Available Jobs", "My Jobs"};//, "Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb", "Ice Cream Sandwich", "Jelly Bean", "KitKat", "Lollipop"};

        private SparseArray<Fragment> mPages;
        private int mScrollY;

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
            mPages = new SparseArray<Fragment>();
        }

        public void setScrollY(int scrollY) {
            mScrollY = scrollY;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment f;
            //final int pattern = position % 2;
            switch (position) {

                case 1:
                    f = new MyJobsList();
                    if (0 < mScrollY) {
                        Bundle args = new Bundle();
                        args.putInt(MyJobsList.ARG_INITIAL_POSITION, 1);
                        f.setArguments(args);
                    }
                    break;
                case 0:

                default: {
                    f = new AvaliableJobsList();
                    if (0 < mScrollY) {
                        Bundle args = new Bundle();
                        args.putInt(AvaliableJobsList.ARG_INITIAL_POSITION, 1);
                        f.setArguments(args);
                    }
                    break;
                    /*f = new ViewPagerTabListViewFragment();
                    if (0 < mScrollY) {
                        Bundle args = new Bundle();
                        args.putInt(ViewPagerTabListViewFragment.ARG_INITIAL_POSITION, 1);
                        f.setArguments(args);
                    }
                    break;*/
                }
            }
            // We should cache fragments manually to access to them later
            mPages.put(position, f);
            return f;

            /*Fragment f = new ViewPagerTabScrollViewFragment();
            if (0 <= mScrollY) {
                Bundle args = new Bundle();
                args.putInt(ViewPagerTabScrollViewFragment.ARG_SCROLL_Y, mScrollY);
                f.setArguments(args);
            }
            mPages.put(position, f);
            return f;*/
        }

        public Fragment getItemAt(int position) {
            return mPages.get(position);
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (0 <= mPages.indexOfKey(position)) {
                mPages.remove(position);
            }
            super.destroyItem(container, position, object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

    /*public boolean isGPSEnabled(){

        LocationManager locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);

        return locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

    }*/

    /*public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }*/


}
