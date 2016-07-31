package fr.handipressante.app.ToiletSheet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.handipressante.app.Data.Comment;
import fr.handipressante.app.R;
import fr.handipressante.app.Server.CommentDownloader;
import fr.handipressante.app.Server.Downloader;

public class CommentActivity extends AppCompatActivity {
    CommentListAdapter mAdapter;
    Integer mToiletId;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mAdapter = new CommentListAdapter(getApplicationContext(), new ArrayList<Comment>());

        Toolbar toolbarTop = (Toolbar)findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbarTop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTop.setTitle("Retour");

        Toolbar toolbarBottom = (Toolbar) findViewById(R.id.scroll_toolbar);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean scroll_help = sharedPrefs.getBoolean("scroll_help", false);

        if(!scroll_help) {
            toolbarBottom.setVisibility(View.GONE);
        }

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        findViewById(R.id.upList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int offset = computeVisibleItemCount();
                mListView.smoothScrollByOffset(-offset);
            }
        });
        findViewById(R.id.downList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int offset = computeVisibleItemCount();
                mListView.smoothScrollByOffset(offset);
            }
        });


        Intent intent = getIntent();
        mToiletId = intent.getIntExtra("toiletId", 0);
        if (mToiletId > 0) {
            CommentDownloader downloader = new CommentDownloader(getApplicationContext());
            downloader.downloadCommentList(mToiletId, new Downloader.Listener<List<Comment>>() {
                @Override
                public void onResponse(List<Comment> response) {
                    mAdapter.swapItems(response);
                }
            });
        } else {
            finish();
        }
    }

    private int computeVisibleItemCount() {
        if (mAdapter.getCount() == 0)
            return 0;

        int listViewHeight = mListView.getHeight();
        View listItem = mAdapter.getView(0, null, mListView);
        listItem.measure(0, 0);
        int childItemHeight = listItem.getMeasuredHeight() + mListView.getDividerHeight();

        return listViewHeight / childItemHeight;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.help).setVisible(false);
        return true;
    }
}
