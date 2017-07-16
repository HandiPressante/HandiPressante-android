package fr.handipressante.app;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import fr.handipressante.app.list.ToiletListFragment;
import fr.handipressante.app.map.ToiletMapFragment;

public class MainFragment extends Fragment {
    private FragmentTabHost mTabHost;
    private ToiletMapFragment mToiletMapFragment;
    private boolean mToiletAddPendingRequest = false;

    //Mandatory Constructor
    public MainFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.menu_toilets);
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
         navigationView.getMenu().getItem(0).setChecked(true);

        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("toilet_list").setIndicator(getTabIndicator(mTabHost.getContext(), R.string.tab_list_title)),
                ToiletListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("toilet_map").setIndicator(getTabIndicator(mTabHost.getContext(), R.string.tab_map_title)),
                ToiletMapFragment.class, null);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabTag) {
                if (!tabTag.equals("toilet_map")) {
                    if (mToiletMapFragment != null && mToiletMapFragment.isToiletAddEnabled()) {
                        mToiletMapFragment.onToiletCreationCanceled();
                    }
                }
            }
        });

        return mTabHost;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_toilets, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem toiletAddAction = menu.findItem(R.id.add_toilet);
        if (mToiletMapFragment != null) {
            if (mToiletMapFragment.isToiletAddEnabled()) {
                toiletAddAction.setTitle(getString(R.string.cancel));
            } else {
                toiletAddAction.setTitle(getString(R.string.add));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_toilet:
                if (!mTabHost.getCurrentTabTag().equals("toilet_map")) {
                    mTabHost.setCurrentTabByTag("toilet_map");
                }

                if (mToiletMapFragment != null) {
                    if (mToiletMapFragment.isToiletAddEnabled()) {
                        mToiletMapFragment.onToiletCreationCanceled();
                    } else {
                        mToiletMapFragment.onToiletCreationRequested();
                    }
                } else {
                    mToiletAddPendingRequest = true;
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment.getClass() == ToiletMapFragment.class) {
            mToiletMapFragment = (ToiletMapFragment) fragment;

            if (mToiletAddPendingRequest) {
                mToiletAddPendingRequest = false;
                mToiletMapFragment.onToiletCreationRequested();
            }
        }
    }

    private View getTabIndicator(Context context, int title) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(title);
        return view;
    }
}