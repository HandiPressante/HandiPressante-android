package fr.handipressante.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MainActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private MainFragment mMainFragment = new MainFragment();
    private SettingsFragment mSettingsFragment = new SettingsFragment();
    private MemoListFragment mMemoListFragment = new MemoListFragment();

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private String[] mTitles;
    private Integer[] mIcon;

    public void onCreate(Bundle savedInstanceState) {
        Log.i("MainActivity", "onCreate");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getActionBar().setIcon(R.drawable.menu_icon);

        mTitle = mDrawerTitle = getTitle();
        mTitles = new String[]{"Home", "Réglages", "Mémos"};

        // List of icon in the menu
        mIcon = new Integer[]{R.drawable.home_icon, R.drawable.settings_icon, R.drawable.memo_icon};
        initDrawer();

        if (savedInstanceState == null) {
            selectItem(0);
        }

    }

    private DrawerListener createDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                //R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        return mDrawerToggle;
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


        MenuList adapter = new
                MenuList(MainActivity.this, mTitles, mIcon);
        mDrawerList.setAdapter(adapter);


        // set up the drawer's list view with items and click listener
        /*mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mTitles));
                */




        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setHomeButtonEnabled(true);

        createDrawerToggle();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    //method used with a button to send coords to external map app. Don't touch at the moment!!





    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        if (position == 0) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, mMainFragment).commit();

        } else if (position == 1) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, mSettingsFragment).commit();

        } else if (position == 2) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, mMemoListFragment).commit();
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

}