package fr.handipressante.app.show;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import fr.handipressante.app.R;
import fr.handipressante.app.data.Comment;
import fr.handipressante.app.data.Photo;
import fr.handipressante.app.data.PhotoDAO;
import fr.handipressante.app.data.Toilet;
import fr.handipressante.app.edit.ConfirmReportCommentDialogFragment;
import fr.handipressante.app.edit.ConfirmReportPhotoDialogFragment;
import fr.handipressante.app.help.HelpSlideToiletSheet;
import fr.handipressante.app.server.CommentDownloader;
import fr.handipressante.app.server.Downloader;
import fr.handipressante.app.server.PhotoDownloader;
import fr.handipressante.app.server.ToiletDownloader;

public class MoreInfosActivity extends AppCompatActivity {

    private final String LOG_TAG = "MoreInfosActivity";

    private Toilet mToilet;
    private PhotoPagerAdapter mPhotoAdapter;

    final int MAX_COMMENTS_VISIBLE = 5;

    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toiletsheet_moreinfos);

        // get info from parent view
        Intent intent = getIntent();
        mToilet = intent.getParcelableExtra("toilet");

        initToolbar();
        initScrollToolbar();

        fillToiletSheet(mToilet);


        ToiletDownloader downloader = new ToiletDownloader(this);
        downloader.requestToilet(mToilet.getId(), new Downloader.Listener<List<Toilet>>() {
            @Override
            public void onResponse(List<Toilet> response) {
                if (response != null && response.size() == 1) {
                    mToilet = response.get(0);
                    fillToiletSheet(mToilet);
                    enableEdition();
                }
            }
        });

        mPhotoAdapter = new PhotoPagerAdapter(getApplicationContext());

        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        if (viewPager != null)
            viewPager.setAdapter(mPhotoAdapter);

        new LoadDatabaseTask().execute();
        // onPostExecute : data are loaded from the server

        syncCommentsWithServer();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

        findViewById(R.id.report_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConfirmReportPhotoDialogFragment dialogFragment = new ConfirmReportPhotoDialogFragment();
                dialogFragment.setListener(new ConfirmReportPhotoDialogFragment.ConfirmReportPhotoDialogListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        if (mPhotoAdapter != null) {
                            ViewPager picturesViewer = (ViewPager) findViewById(R.id.viewpager);
                            int pictureIndex = picturesViewer.getCurrentItem();
                            final List<Photo> pictures = mPhotoAdapter.getPhotoList();

                            if (pictureIndex < pictures.size()) {
                                final Photo picture = pictures.get(pictureIndex);

                                new PhotoDownloader(getApplicationContext()).postPictureReport(picture.getId(), new Downloader.Listener<Boolean>() {
                                    @Override
                                    public void onResponse(Boolean response) {
                                        if (response) {
                                            Toast.makeText(getApplicationContext(), R.string.report_success, Toast.LENGTH_LONG).show();
                                            syncPhotoWithServer();
                                        }
                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {

                    }
                });
                dialogFragment.show(getSupportFragmentManager(), getResources().getString(R.string.confirm_report));
            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar =   (Toolbar) findViewById(R.id.toolbar_sheet);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitle("Retour");
    }

    private void initScrollToolbar() {
        Toolbar toolbarBottom = (Toolbar) findViewById(R.id.scroll_toolbar);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean scroll_help = sharedPrefs.getBoolean("scroll_help", false);

        if(!scroll_help && toolbarBottom != null) {
            toolbarBottom.setVisibility(View.GONE);
            return;
        }

        ImageButton up = (ImageButton) findViewById(R.id.upList);
        if (up != null) {
            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                    if (scrollView != null) {
                        int scrollViewHeight = scrollView.getHeight();
                        int dy = scrollViewHeight / 2;
                        scrollView.smoothScrollBy(0, -dy);
                    }
                }
            });
        }

        ImageButton down = (ImageButton) findViewById(R.id.downList);
        if (down != null) {
            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
                    if (scrollView != null) {
                        int scrollViewHeight = scrollView.getHeight();
                        int dy = scrollViewHeight / 2;
                        scrollView.smoothScrollBy(0, dy);
                    }
                }
            });
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.help:
                Intent intentHelp = new Intent(getApplicationContext(), HelpSlideToiletSheet.class);
                startActivity(intentHelp);
            break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
        // Inflate the menu; this adds items to the action bar if it is present.
        /*MenuItem item_mod = menu.add(Menu.NONE, R.id.modification,1,R.string.modification);
        item_mod.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);*/

        //return super.onCreateOptionsMenu(menu);
    }

    public void fillToiletSheet(Toilet toilet) {

        // Set toilet's description (wiki)
        TextView description=(TextView)findViewById(R.id.toilet_description);
        if(toilet.getDescription().isEmpty()){
            description.setText(R.string.still_no_description);
            description.setTypeface(null, Typeface.ITALIC);
        }else{
            description.setText(toilet.getDescription());
        }


        //Set pictures
        //Previous arrow
        ImageView previous= (ImageView) findViewById(R.id.previous);
        //previous.setImageResource(R.drawable.precedent);

        //next arrow
        ImageView next= (ImageView) findViewById(R.id.next);
        //next.setImageResource(R.drawable.suivant);
        //picture slider
        //listPics.add((ImageView)findViewById(R.id.picture_block1));
        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);


        //changes the visible photo in the carousel to the previous one
        previous.setOnClickListener(new View.OnClickListener() {
            private int getItem(int i) {
                return viewPager.getCurrentItem() + i;
            }
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(getItem(-1), true);
            }
        });

        //changes the visible photo in the carousel to the next one
        next.setOnClickListener(new View.OnClickListener() {
            private int getItem(int i) {
                return viewPager.getCurrentItem() + i;
            }
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(getItem(+1), true);
            }
        });

    }

    private void enableEdition() {
        if (mPhotoAdapter != null && !mPhotoAdapter.getPhotoList().isEmpty()) {
            findViewById(R.id.report_picture).setEnabled(true);
        }
    }

    private void updateCommentList(List<Comment> commentList) {
        if (commentList.size() == 0) {
            findViewById(R.id.no_comment).setVisibility(View.VISIBLE);
            findViewById(R.id.show_comments).setVisibility(View.GONE);
        } else {
            findViewById(R.id.no_comment).setVisibility(View.GONE);

            ViewGroup commentView = (ViewGroup) findViewById(R.id.comments);
            commentView.removeAllViewsInLayout();

            for (int i = 0; i < commentList.size() && i < MAX_COMMENTS_VISIBLE; ++i) {
                final Comment comment = commentList.get(i);

                View row;
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (i + 1 == commentList.size()) {
                    // No separator if last element
                    row = inflater.inflate(R.layout.listitem_comment, commentView, false);
                } else {
                    row = inflater.inflate(R.layout.listitem_comment_and_separator, commentView, false);
                }

                TextView username = (TextView) row.findViewById(R.id.username);
                TextView content = (TextView) row.findViewById(R.id.content);
                TextView date = (TextView) row.findViewById(R.id.date);
                Button report = (Button) row.findViewById(R.id.report_comment);

                username.setText(comment.getUsername());
                content.setText(comment.getContent());
                date.setText(comment.getPostdate());

                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ConfirmReportCommentDialogFragment dialogFragment = new ConfirmReportCommentDialogFragment();
                        dialogFragment.setListener(new ConfirmReportCommentDialogFragment.ConfirmReportCommentDialogListener() {
                            @Override
                            public void onDialogPositiveClick(DialogFragment dialog) {
                                new CommentDownloader(getApplicationContext()).postCommentReport(comment.getId(), new Downloader.Listener<Boolean>() {
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

                commentView.addView(row);
            }

            if (commentList.size() > MAX_COMMENTS_VISIBLE) {
                findViewById(R.id.show_comments).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.show_comments).setVisibility(View.GONE);
            }
        }

    }

    private void syncCommentsWithServer() {
        CommentDownloader commentDownloader = new CommentDownloader(getApplicationContext());
        commentDownloader.downloadCommentList(mToilet.getId(), new Downloader.Listener<List<Comment>>() {
            @Override
            public void onResponse(List<Comment> response) {
                updateCommentList(response);
            }
        });
    }


    /**
     * Helper functions
     */




    private class LoadDatabaseTask extends AsyncTask<Void, Void, List<Photo>> {
        @Override
        protected List<Photo> doInBackground(Void... params) {
            PhotoDAO dao = new PhotoDAO(MoreInfosActivity.this);
            dao.open();
            List<Photo> result = dao.selectByToilet(mToilet.getId());
            dao.close();

            return result;
        }

        @Override
        protected void onPostExecute(List<Photo> photoList) {
            if (!photoList.isEmpty()) {

                final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
                if (viewPager != null)
                    mPhotoAdapter.swapItems(viewPager, photoList);
            }

            syncPhotoWithServer();
        }
    }

    private void syncPhotoWithServer() {
        new PhotoDownloader(getApplicationContext()).downloadPhotoList(mToilet.getId(), new Downloader.Listener<List<Photo>>() {
            @Override
            public void onResponse(List<Photo> response) {
                new UpdatePhotoListTask().execute(response);

                final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
                if (viewPager != null)
                    mPhotoAdapter.swapItems(viewPager, response);
            }
        });
    }

    private class UpdatePhotoListTask extends AsyncTask<List<Photo>, Void, Void> {
        @Override
        protected Void doInBackground(List<Photo>... params) {
            if (params.length != 1) return null;

            List<Photo> photoList = params[0];
            if (photoList == null) return null;

            PhotoDAO dao = new PhotoDAO(MoreInfosActivity.this);
            dao.open();
            List<Photo> oldPhotoList = dao.selectByToilet(mToilet.getId());

            // Removed photos
            for (Photo oldPhoto : oldPhotoList) {
                boolean removed = true;
                for (Photo photo : photoList) {
                    if (photo.getId().equals(oldPhoto.getId())) {
                        removed = false;
                        break;
                    }
                }

                if (removed) {
                    File file = new File(getFilesDir(), oldPhoto.getLocalPath());
                    file.delete();
                    dao.remove(oldPhoto.getId());
                }
            }

            // Added photos
            for (Photo photo : photoList) {
                boolean updated = false;
                for (Photo oldPhoto : oldPhotoList) {
                    if (photo.getId().equals(oldPhoto.getId())) {
                        updated = true;
                        break;
                    }
                }

                if (updated) {
                    dao.update(photo);
                } else {
                    dao.add(photo);
                }
            }

            dao.close();
            return null;
        }
    }
}
