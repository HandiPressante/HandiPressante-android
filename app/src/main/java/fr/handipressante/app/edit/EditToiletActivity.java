package fr.handipressante.app.edit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.handipressante.app.Converters;
import fr.handipressante.app.R;
import fr.handipressante.app.data.Photo;
import fr.handipressante.app.data.PhotoDAO;
import fr.handipressante.app.data.Toilet;
import fr.handipressante.app.server.Downloader;
import fr.handipressante.app.server.PhotoDownloader;
import fr.handipressante.app.show.PhotoPagerAdapter;

public class EditToiletActivity extends AppCompatActivity {

    private final String LOG_TAG = "EditToiletActivity";

    private Toilet mToilet;
    private PhotoPagerAdapter mPhotoAdapter;

    private String mCurrentPhotoPath;
    private boolean mReturningPhoto = false;

    final int REQUEST_TOILET_EDIT = 1;
    final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittoilet);

        // get info from parent view
        Intent intent = getIntent();
        mToilet = intent.getParcelableExtra("toilet");

        initToolbar();
        initScrollToolbar();

        fillToiletSheet(mToilet);
        initEditActions();

        mPhotoAdapter = new PhotoPagerAdapter(getApplicationContext());

        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        if (viewPager != null)
            viewPager.setAdapter(mPhotoAdapter);

        new LoadDatabaseTask().execute();
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

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

    private void initEditActions() {
        Button editName = (Button) findViewById(R.id.edit_name);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NameActivity.class);
                intent.putExtra("toilet", mToilet);
                intent.putExtra("new", false);
                startActivityForResult(intent, REQUEST_TOILET_EDIT);
            }
        });

        Button editAdapted = (Button) findViewById(R.id.edit_adapted);
        editAdapted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AccessibleActivity.class);
                intent.putExtra("toilet", mToilet);
                intent.putExtra("new", false);
                startActivityForResult(intent, REQUEST_TOILET_EDIT);
            }
        });

        Button editCharged = (Button) findViewById(R.id.edit_charged);
        editCharged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChargedActivity.class);
                intent.putExtra("toilet", mToilet);
                intent.putExtra("new", false);
                startActivityForResult(intent, REQUEST_TOILET_EDIT);
            }
        });

        Button addPicture = (Button) findViewById(R.id.add_picture);
        addPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmPhotoDialogFragment dialogFragment = new ConfirmPhotoDialogFragment();
                dialogFragment.setListener(new ConfirmPhotoDialogFragment.ConfirmPhotoDialogListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        dispatchTakePictureIntent();
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {

                    }
                });
                dialogFragment.show(getSupportFragmentManager(), getResources().getString(R.string.confirm_send));
            }
        });

        Button editDescription = (Button) findViewById(R.id.edit_description);
        editDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
                intent.putExtra("toilet", mToilet);
                intent.putExtra("new", false);
                startActivityForResult(intent, REQUEST_TOILET_EDIT);
            }
        });
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

        // Set toilet's name
        TextView name = (TextView)findViewById(R.id.toilet_name);
        name.setText(toilet.getName());

        // Set icon whether adapted toilet or not
        ImageView adapted = (ImageView) findViewById(R.id.adapted);
        adapted.setImageResource(Converters.pmrFromBoolean(toilet.isAdapted()));

        ImageView charged = (ImageView) findViewById(R.id.charged);
        charged.setImageResource(Converters.chargedFromBoolean(toilet.isCharged()));

        // Set toilet's description
        TextView description = (TextView)findViewById(R.id.toilet_description);
        if(toilet.getDescription().isEmpty()){
            description.setText(R.string.still_no_description);
            description.setTypeface(null, Typeface.ITALIC);
        }else{
            description.setText(toilet.getDescription());
        }

        //Set pictures
        //Previous arrow
        ImageView previous = (ImageView) findViewById(R.id.previous);
        //previous.setImageResource(R.drawable.precedent);

        //next arrow
        ImageView next = (ImageView) findViewById(R.id.next);
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;

        File storageDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // TODO : using this dir (not visible from gallery) is better, but managing is then needed
            // storageDir = getExternalCacheDir();
            storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        } else {
            storageDir = getFilesDir();
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void dispatchTakePictureIntent() {
        // Checking camera availability
        if (!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(getApplicationContext(),
                    "Désolé ! Votre appareil ne gère pas de caméra.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private class LoadDatabaseTask extends AsyncTask<Void, Void, List<Photo>> {
        @Override
        protected List<Photo> doInBackground(Void... params) {
            PhotoDAO dao = new PhotoDAO(EditToiletActivity.this);
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

            PhotoDAO dao = new PhotoDAO(EditToiletActivity.this);
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
