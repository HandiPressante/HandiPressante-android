package fr.handipressante.app.show;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.handipressante.app.data.Comment;
import fr.handipressante.app.R;
import fr.handipressante.app.edit.ConfirmReportCommentDialogFragment;
import fr.handipressante.app.server.CommentDownloader;
import fr.handipressante.app.server.Downloader;

public class CommentActivity extends AppCompatActivity {
    CommentListAdapter mAdapter;
    Integer mToiletId;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toiletsheet_comment);

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
            syncCommentsWithServer();
        } else {
            finish();
        }
    }

    private void syncCommentsWithServer() {
        CommentDownloader downloader = new CommentDownloader(getApplicationContext());
        downloader.downloadCommentList(mToiletId, new Downloader.Listener<List<Comment>>() {
            @Override
            public void onResponse(List<Comment> response) {
                updateComments(response);
            }
        });
    }

    private void updateComments(List<Comment> comments) {
        mAdapter.swapItems(comments);
        mAdapter.setReportButtonListener(new CommentListAdapter.ReportButtonListener() {
            @Override
            public void onClick(final Integer commentId) {
                ConfirmReportCommentDialogFragment dialogFragment = new ConfirmReportCommentDialogFragment();
                dialogFragment.setListener(new ConfirmReportCommentDialogFragment.ConfirmReportCommentDialogListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        new CommentDownloader(getApplicationContext()).postCommentReport(commentId, new Downloader.Listener<Boolean>() {
                            @Override
                            public void onResponse(Boolean response) {
                                if (response) {
                                    Toast.makeText(getApplicationContext(), R.string.report_success, Toast.LENGTH_LONG).show();
                                    syncCommentsWithServer();
                                }
                            }
                        });
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {

                    }
                });
                dialogFragment.show(getSupportFragmentManager(), getResources().getString(R.string.confirm_report));
            }
        });
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
