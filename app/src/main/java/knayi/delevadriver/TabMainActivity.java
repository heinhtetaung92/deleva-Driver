package knayi.delevadriver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.provider.Settings;
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
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;


public class TabMainActivity extends ActionBarActivity  implements ObservableScrollViewCallbacks {

    private View mHeaderView;
    private View mToolbarView;
    private int mBaseTranslationY;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    SharedPreferences sPref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(!isGPSEnabled()){
            showSettingsAlert();
        }




        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        sPref = getSharedPreferences(Config.TOKEN_PREF, Context.MODE_PRIVATE);

        mHeaderView = findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbarView = findViewById(R.id.toolbar);
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        //overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mPager);

        // When the page is selected, other fragments' scrollY should be adjusted
        // according to the toolbar status(shown/hidden)
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                propagateToolbarState(toolbarIsShown());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        propagateToolbarState(toolbarIsShown());




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_tab_main, menu);


        final MenuItem item = menu.add(0, 12, 0, "profile");
        //menu.removeItem(12);
        item.setIcon(R.drawable.profilemenu);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                | MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == 12) {



            startActivity(new Intent(this, ProfileActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private Fragment getCurrentFragment() {
        return mPagerAdapter.getItemAt(mPager.getCurrentItem());
    }

    private void propagateToolbarState(boolean isShown) {
        int toolbarHeight = mToolbarView.getHeight();



        // Set scrollY for the fragments that are not created yet
        int data = isShown ? 0 : toolbarHeight;

        Log.i("toolbarshow", String.valueOf(toolbarIsShown()) + String.valueOf(data));

        mPagerAdapter.setScrollY(data);

        // Set scrollY for the active fragments
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            // Skip current item
            if (i == mPager.getCurrentItem()) {
                continue;
            }

            // Skip destroyed or not created item
            Fragment f = mPagerAdapter.getItemAt(i);
            if (f == null) {
                continue;
            }

            View view = f.getView();
            if (view == null) {
                continue;
            }
            propagateToolbarState(isShown, view, toolbarHeight);
        }
    }





    private void propagateToolbarState(boolean isShown, View view, int toolbarHeight) {
        Scrollable scrollView = (Scrollable) view.findViewById(R.id.scroll);
        if (scrollView == null) {
            return;
        }
        if (isShown) {
            // Scroll up
            if (0 < scrollView.getCurrentScrollY()) {
                scrollView.scrollVerticallyTo(0);
            }
        } else {
            // Scroll down (to hide padding)
            if (scrollView.getCurrentScrollY() < toolbarHeight) {
                scrollView.scrollVerticallyTo(toolbarHeight);
            }
        }
    }



    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mHeaderView) == 0;
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
        }
        propagateToolbarState(true);
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
        }
        propagateToolbarState(false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
            if (firstScroll) {
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            int headerTranslationY = Math.min(0, Math.max(-toolbarHeight, -(scrollY - mBaseTranslationY)));
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
        }

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return;
        }
        View view = fragment.getView();
        if (view == null) {
            return;
        }



        // ObservableXxxViews have same API
        // but currently they don't have any common interfaces.
        adjustToolbar(scrollState, view);


    }

    private void adjustToolbar(ScrollState scrollState, View view) {
        int toolbarHeight = mToolbarView.getHeight();
        final Scrollable scrollView = (Scrollable) view.findViewById(R.id.scroll);
        if (scrollView == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (toolbarHeight < scrollView.getCurrentScrollY()) {
                hideToolbar();
            } else if (scrollView.getCurrentScrollY() < toolbarHeight) {
                showToolbar();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (toolbarHeight < scrollView.getCurrentScrollY()) {
                showToolbar();
            }
        }
    }

    private static class NavigationAdapter extends FragmentStatePagerAdapter {

        private static final String[] TITLES = new String[]{"Avaliable Jobs", "My Jobs"};//, "Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb", "Ice Cream Sandwich", "Jelly Bean", "KitKat", "Lollipop"};

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

    public boolean isGPSEnabled(){

        LocationManager locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);

        return locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    public void showSettingsAlert(){
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
    }


}
