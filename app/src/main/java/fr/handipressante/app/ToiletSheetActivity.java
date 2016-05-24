package fr.handipressante.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.osmdroid.util.GeoPoint;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.handipressante.app.Data.Memo;
import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.Server.Downloader;
import fr.handipressante.app.Server.ToiletDownloader;
import fr.handipressante.app.ToiletEdition.AddToiletDialog;
import fr.handipressante.app.ToiletEdition.CommentEdition;
import fr.handipressante.app.ToiletEdition.ConfirmPhotoDialogFragment;
import fr.handipressante.app.ToiletEdition.DescriptionActivity;
import fr.handipressante.app.ToiletEdition.NameActivity;
import fr.handipressante.app.ToiletEdition.RatingActivity;

public class ToiletSheetActivity extends AppCompatActivity {
    private Toilet mToilet;
    final int REQUEST_IMAGE_CAPTURE = 1;
    final int REQUEST_TOILET_EDIT = 2;
    final int REQUEST_ADD_COMMENT = 3;

    SharedPreferences sharedPrefs;

    private String mCurrentPhotoPath;

    //TODO:finir de changer l'enum en liste
   /* public static ArrayList<ImageView> imgList = new ArrayList<>();

    public static ArrayList<ImageView> getImgList() {
        return imgList;
    }
*/
    //enum for Viewpager slider
    public enum CustomPagerEnum {

        RED(0, R.layout.pics_test),
        BLUE(1, R.layout.pics_test2),
        ORANGE(2, R.layout.pics_test);

        private int mTitleResId;
        private int mLayoutResId;

        CustomPagerEnum(int titleResId, int layoutResId) {
            mTitleResId = titleResId;
            mLayoutResId = layoutResId;
        }

        public int getTitleResId() {
            return mTitleResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }

    }

    //public static ArrayList<ImageView> listPics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_sheet);

        // get info from parent view
        Intent intent = getIntent();
        mToilet = intent.getParcelableExtra("toilet");

        initToolbar();
        initScrollToolbar();

        fillToiletSheet(mToilet);
        addComment(mToilet);

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

        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        if (viewPager != null)
            viewPager.setAdapter(new CustomPagerAdapter(getApplicationContext()));
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

        findViewById(R.id.add_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommentEdition.class);
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
        boolean can_scroll = sharedPrefs.getBoolean("slide",false);

        if(can_scroll && toolbarBottom != null) {
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

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
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

    private void rescaleAndUploadPhoto() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            new CompressAndUploadPhotoTask().execute();

        } else if (requestCode == REQUEST_TOILET_EDIT && resultCode == 0 && data != null) {
            Toilet toilet = data.getParcelableExtra("toilet");
            if (toilet != null) {
                mToilet = toilet;
                fillToiletSheet(mToilet);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
         /*   case R.id.modification:
                //Intent intent = new Intent(getApplicationContext(), ModificationSheet.class);
                Intent intent = new Intent(getApplicationContext(), NameActivity.class);
                intent.putExtra("toilet", mToilet);
                intent.putExtra("new", false);
                //Toast.makeText(getApplicationContext(), "Open Sheet Modification", Toast.LENGTH_SHORT).show();
                startActivity(intent);*/
            case R.id.help:
                Intent intentHelp = new Intent(getApplicationContext(), HelpSlideToiletSheet.class);
                startActivity(intentHelp);
            break;
            // Something else
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
        if (toilet.isAdapted()) {
            handicapped.setImageResource(R.drawable.handicap_icon);
        } else {
            handicapped.setImageResource(R.drawable.not_handicap_icon);
        }

        // Set toilet's name
        TextView name=(TextView)findViewById(R.id.toilet_name);
        name.setText(toilet.getAddress());

        // Set toilet's description (wiki)
        TextView description=(TextView)findViewById(R.id.toilet_description);
        if(toilet.getDescription().isEmpty()){
            description.setText("Ces toilettes n'ont pas de description ! Soyez le premier à la remplir !"); // TODO : String res
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

    public void addComment(Toilet toilet){
        LinearLayout container = (LinearLayout) findViewById(R.id.comments_layout);
        boolean comment = false;

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
            comment_text.setText("Ceci est mon commentaire pour ces toilettes bla bla bla bla bla bla bla bla");
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
            no_comment.setText(" Il n'y a pas encore de commentaires pour ces toilettes !");
            no_comment.setTypeface(null, Typeface.ITALIC);
            comment_layout.addView(no_comment);
        }
    }


    private ProgressDialog mPhotoUploadDialog;
    private class CompressAndUploadPhotoTask extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPhotoUploadDialog = new ProgressDialog(ToiletSheetActivity.this);
            mPhotoUploadDialog.setTitle("Veuillez patienter");
            mPhotoUploadDialog.setMessage("Envoi du fichier en cours ...");
            mPhotoUploadDialog.setIndeterminate(false);
            mPhotoUploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mPhotoUploadDialog.setMax(100);
            mPhotoUploadDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (progress.length != 1) return;
            mPhotoUploadDialog.setProgress(progress[0]);
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO : compress image resolution
            return uploadFile();
        }

        private String uploadFile() {
            String serverResponse = null;

            HttpClient httpClient = new DefaultHttpClient();


            return "test";
        }

        private void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                    + fileName + "\"" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);

            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024 * 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(lineEnd);
        }

        @Override
        protected void onPostExecute(String result) {
            mPhotoUploadDialog.dismiss();
            showAlert(result);
        }
    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
