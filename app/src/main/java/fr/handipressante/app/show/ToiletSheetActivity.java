package fr.handipressante.app.show;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.handipressante.app.Converters;
import fr.handipressante.app.data.Comment;
import fr.handipressante.app.data.Photo;
import fr.handipressante.app.data.PhotoDAO;
import fr.handipressante.app.data.Toilet;
import fr.handipressante.app.help.HelpSlideToiletSheet;
import fr.handipressante.app.R;
import fr.handipressante.app.server.CommentDownloader;
import fr.handipressante.app.server.Downloader;
import fr.handipressante.app.server.MultipartRequest;
import fr.handipressante.app.server.PhotoDownloader;
import fr.handipressante.app.server.RequestManager;
import fr.handipressante.app.server.ToiletDownloader;
import fr.handipressante.app.edit.CommentEdition;
import fr.handipressante.app.edit.ConfirmPhotoDialogFragment;
import fr.handipressante.app.edit.DescriptionActivity;
import fr.handipressante.app.edit.NameActivity;
import fr.handipressante.app.edit.RatingActivity;

public class ToiletSheetActivity extends AppCompatActivity {
    private Toilet mToilet;
    private PhotoPagerAdapter mPhotoAdapter;

    final int REQUEST_IMAGE_CAPTURE = 1;
    final int REQUEST_TOILET_EDIT = 2;
    final int REQUEST_ADD_COMMENT = 3;

    SharedPreferences sharedPrefs;

    private String mCurrentPhotoPath;
    private boolean mReturningPhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toiletsheet);

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

        syncCommentWithServer();
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

    public void onStart() {
        super.onStart();

        GeoPoint geo = mToilet.getCoordinates();
        final Uri mUri = Uri.parse("geo:" + geo.getLatitude() + "," + geo.getLongitude() + "?q=" + geo.getLatitude() + "," + geo.getLongitude());
        //Listener that opens Maps when you click on Itinerary button
        findViewById(R.id.map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri geoLocation = mUri;

                //intent to start a new activity
                Intent intent = new Intent(Intent.ACTION_VIEW, geoLocation);
                intent.setData(geoLocation);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.photo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        findViewById(R.id.edit_toilet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NameActivity.class);
                intent.putExtra("toilet", mToilet);
                intent.putExtra("new", false);
                startActivityForResult(intent, REQUEST_TOILET_EDIT);
            }
        });

        findViewById(R.id.edit_description).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
                intent.putExtra("toilet", mToilet);
                intent.putExtra("new", false);
                startActivityForResult(intent, REQUEST_TOILET_EDIT);
            }
        });

        findViewById(R.id.edit_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RatingActivity.class);
                intent.putExtra("toilet", mToilet);
                startActivityForResult(intent, REQUEST_TOILET_EDIT);
            }
        });

        findViewById(R.id.show_comments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                intent.putExtra("toiletId", mToilet.getId());
                startActivity(intent);
            }
        });

        /*
        findViewById(R.id.delete_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDeleteComment deleteDialog = new ConfirmDeleteComment();
                Bundle args = new Bundle();
                args.putParcelable("toilet", mToilet);
                deleteDialog.setArguments(args);

                deleteDialog.show(getSupportFragmentManager(),"test");
            }
        });
        */

        findViewById(R.id.add_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommentEdition.class);
                intent.putExtra("toiletId", mToilet.getId());
                startActivityForResult(intent, REQUEST_ADD_COMMENT);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            mReturningPhoto = true;

        } else if (requestCode == REQUEST_TOILET_EDIT && resultCode == 0 && data != null) {
            Toilet toilet = data.getParcelableExtra("toilet");
            if (toilet != null) {
                mToilet = toilet;
                fillToiletSheet(mToilet);
            }
        } else if (requestCode == REQUEST_ADD_COMMENT && resultCode == 0) {
            syncCommentWithServer();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mReturningPhoto) {
            mReturningPhoto = false;
            new CompressAndUploadPhotoTask().execute();
        }
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
        // Set icon whether adapted toilet or not
        ImageView handicapped= (ImageView) findViewById(R.id.handicapped);
        handicapped.setImageResource(Converters.pmrFromBoolean(toilet.isAdapted()));

        ImageView charged= (ImageView) findViewById(R.id.charged);
        charged.setImageResource(Converters.chargedFromBoolean(toilet.isCharged()));

        // Set toilet's name
        TextView name=(TextView)findViewById(R.id.toilet_name);
        name.setText(toilet.getAddress());

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

        // Set general rate
        ImageView global_rate = (ImageView) findViewById(R.id.global_rate);
        global_rate.setImageResource(Converters.rankFromInteger(toilet.getRankAverage()));

        // Set cleanliness rate
        ImageView cleanliness_rate = (ImageView) findViewById(R.id.cleanliness_rate);
        cleanliness_rate.setImageResource(Converters.rankFromInteger(toilet.getRankCleanliness()));

        // Set facilities rate
        ImageView facilities_rate = (ImageView) findViewById(R.id.facilities_rate);
        facilities_rate.setImageResource(Converters.rankFromInteger(toilet.getRankFacilities()));

        // Set accessibility rate
        ImageView accessibility_rate = (ImageView) findViewById(R.id.accessibility_rate);
        accessibility_rate.setImageResource(Converters.rankFromInteger(toilet.getRankAccessibility()));

    }

    private void enableEdition() {
        findViewById(R.id.edit_toilet).setEnabled(true);
        findViewById(R.id.photo_button).setEnabled(true);
        findViewById(R.id.edit_description).setEnabled(true);
        findViewById(R.id.edit_rate).setEnabled(true);
        findViewById(R.id.add_comment).setEnabled(true);
    }

    private void updateCommentList(List<Comment> commentList) {
        if (commentList.size() == 0) {
            findViewById(R.id.no_comment).setVisibility(View.VISIBLE);
            findViewById(R.id.show_comments).setVisibility(View.GONE);
        } else {
            findViewById(R.id.no_comment).setVisibility(View.GONE);
            findViewById(R.id.show_comments).setVisibility(View.VISIBLE);
        }

        /*
        findViewById(R.id.no_comment).setVisibility(View.GONE);
        int count = commentList.size();
        for (int i = 0; i < 3; ++i) {
            if (i < count) {
                Comment c = commentList.get(i);
                View row = findViewById(kCommentViews[i]);

                TextView username = (TextView) row.findViewById(R.id.username);
                TextView content = (TextView) row.findViewById(R.id.content);
                TextView date = (TextView) row.findViewById(R.id.date);

                username.setText(c.getUsername());
                content.setText(c.getContent());
                date.setText(c.getContent());

                row.setVisibility(View.VISIBLE);
            } else {
                View row = findViewById(kCommentViews[i]);
                row.setVisibility(View.GONE);
            }
        }

        if (commentList.size() > 3) {
            findViewById(R.id.show_comments).setVisibility(View.VISIBLE);
        }
        */
    }

    private void syncCommentWithServer() {
        CommentDownloader commentDownloader = new CommentDownloader(getApplicationContext());
        commentDownloader.downloadCommentList(mToilet.getId(), new Downloader.Listener<List<Comment>>() {
            @Override
            public void onResponse(List<Comment> response) {
                updateCommentList(response);
            }
        });
    }

    /*
    public void addComment(Toilet toilet){
        LinearLayout container = (LinearLayout) findViewById(R.id.comments_layout);
        boolean comment = true;

        if (comment) {
            // Create LinearLayout
            LinearLayout comment_layout = new LinearLayout(this);
            comment_layout.setOrientation(LinearLayout.VERTICAL);
            container.addView(comment_layout);


            // Create TextView for name
            TextView name = new TextView(this);
            name.setText("Marie Babel");
            name.setTypeface(null, Typeface.BOLD);
            comment_layout.addView(name);


            // Create TextView for comment text
            TextView comment_text = new TextView(this);
            comment_text.setText("Les toilettes sont bien équipées mais la poubelle a disparu oO");
            comment_layout.addView(comment_text);


            // Create TextView for comment text
            TextView comment_date = new TextView(this);
            comment_date.setTypeface(null, Typeface.ITALIC);
            comment_date.setText("17/12/15");
            comment_layout.addView(comment_date);

            // Create View for line separator
            View separator = new View(this);
            separator.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 2));
            separator.setBackgroundColor(Color.parseColor("#dfdfdf"));
            comment_layout.addView(separator);

        }else{
            // Create LinearLayout
            LinearLayout comment_layout = new LinearLayout(this);
            comment_layout.setOrientation(LinearLayout.VERTICAL);
            container.addView(comment_layout);


            // Create TextView for name
            TextView no_comment = new TextView(this);
            no_comment.setText(R.string.still_no_comment);
            no_comment.setTypeface(null, Typeface.ITALIC);
            comment_layout.addView(no_comment);
        }
    }

    public void deleteComment(Toilet toilet){
        ImageButton deleteButton = (ImageButton)findViewById(R.id.delete_comment);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Bouton pour supprimer un commentaire", Toast.LENGTH_SHORT).show();
            }
        });

    }
    */

    /**
     * Helper functions
     */

    private void uploadPhoto(byte[] photoData) {
        MultipartRequest.Builder builder = new MultipartRequest.Builder();
        builder.setUrl("http://www.handipressante.fr/api.php/toilet-add-photo");

        try {
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String uuid = sharedPreferences.getString(getString(R.string.saved_uuid), "no-uuid");

            builder.addTextPart("uuid", uuid);
            builder.addTextPart("toilet_id", mToilet.getId().toString());
            builder.addFilePart("photo", photoData, "photo.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultipartRequest request = builder.build(
                new Response.Listener<NetworkResponse>() {
                     @Override
                     public void onResponse(NetworkResponse response) {
                         mPhotoUploadDialog.dismiss();

                         String strJsonResponse = new String(response.data);
                         boolean error = true;
                         int errorCode = -1;

                         try {
                             JSONObject jsonResponse = new JSONObject(strJsonResponse);
                             if (jsonResponse.has("error")) {
                                errorCode = jsonResponse.getInt("error");
                                if (errorCode == 0) error = false;
                             }
                         } catch (JSONException e) {
                             e.printStackTrace();
                         }

                         if (!error) {
                             Toast.makeText(ToiletSheetActivity.this, "Photo envoyée avec succès !", Toast.LENGTH_SHORT).show();
                             syncPhotoWithServer();
                         } else
                             Toast.makeText(ToiletSheetActivity.this, "L'envoi a échoué. (Code d'erreur : " + errorCode + ")", Toast.LENGTH_LONG).show();
                     }
                 },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mPhotoUploadDialog.dismiss();
                        Toast.makeText(ToiletSheetActivity.this, "L'envoi a échoué.", Toast.LENGTH_SHORT).show();
                    }
                });

        String key = request.getCacheKey();
        Cache cache = RequestManager.getInstance(getApplicationContext()).getRequestQueue().getCache();
        if (cache != null) {
            if (cache.get(key) != null) {
                cache.remove(key);
            }
        }

        RequestManager.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private ProgressDialog mPhotoUploadDialog;
    private class CompressAndUploadPhotoTask extends AsyncTask<Void, Integer, byte[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPhotoUploadDialog = new ProgressDialog(ToiletSheetActivity.this);
            mPhotoUploadDialog.setTitle("Veuillez patienter");
            mPhotoUploadDialog.setMessage("Compression en cours ...");
            mPhotoUploadDialog.setIndeterminate(true);
            mPhotoUploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mPhotoUploadDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (progress.length != 1) return;
            mPhotoUploadDialog.setProgress(progress[0]);
        }

        @Override
        protected byte[] doInBackground(Void... params) {
            return compressFile();
        }

        private byte[] compressFile() {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, options);

            // Calculate inSampleSize

            if (options.outHeight >= options.outWidth) {
                options.inSampleSize = calculateInSampleSize(options, 720, 1280);
            } else {
                options.inSampleSize = calculateInSampleSize(options, 1280, 720);
            }

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        }

        @Override
        protected void onPostExecute(byte[] result) {
            mPhotoUploadDialog.dismiss();

            mPhotoUploadDialog.setMessage("Envoi en cours ...");
            mPhotoUploadDialog.show();

            uploadPhoto(result);
        }

        private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }
    }


    private class LoadDatabaseTask extends AsyncTask<Void, Void, List<Photo>> {
        @Override
        protected List<Photo> doInBackground(Void... params) {
            PhotoDAO dao = new PhotoDAO(ToiletSheetActivity.this);
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

            PhotoDAO dao = new PhotoDAO(ToiletSheetActivity.this);
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
