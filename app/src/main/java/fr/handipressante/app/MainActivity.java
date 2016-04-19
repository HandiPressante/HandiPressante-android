package fr.handipressante.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.util.UUID;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {

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
        super.onCreate(savedInstanceState);
        Log.i("MainActivity", "onCreate");


        if (!hasUuid())
            generateUuid();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String uuid = sharedPreferences.getString(getString(R.string.saved_uuid), "no-uuid");
        Log.i("MainActivity", "UUID : " + uuid);

        setContentView(R.layout.activity_main);

        setToolBar();


        mTitle = mDrawerTitle = getTitle();
        mTitles = new String[]{"Home", "Réglages", "Mémos"};

        // List of icon in the menu
        mIcon = new Integer[]{R.drawable.home_icon, R.drawable.settings_icon, R.drawable.memo_icon};
        initDrawer();

        if (savedInstanceState == null) {
            selectItem(0);
        }

    }

    private boolean hasUuid() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String uuid = sharedPref.getString(getString(R.string.saved_uuid), "");
        return !uuid.isEmpty();
    }

    private void generateUuid() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String uuid = UUID.randomUUID().toString();
        editor.putString(getString(R.string.saved_uuid), uuid);
        editor.apply();
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
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


        createDrawerToggle();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    //method used with a button to send coords to external map app. Don't touch at the moment!!

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // synchroniser le drawerToggle après la restauration via onRestoreInstanceState
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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



    private void selectItem(int position) {
        if (position == 0) {
            //removes frag Fragment if created before, because not compatible TODO: improve selectItem ?
            getFragmentManager()
                    .beginTransaction()
                    .remove(mSettingsFragment)
                    .commit();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, mMainFragment)
                    .commit();

        } else if (position == 1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mMainFragment)
                    .commit();
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mMemoListFragment)
                    .commit();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, mSettingsFragment)
                    .commit();
            PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.settings, false);

        } else if (position == 2) {
            getFragmentManager().beginTransaction()
                    .remove(mSettingsFragment)
                    .commit();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, mMemoListFragment)
                    .commit();
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
