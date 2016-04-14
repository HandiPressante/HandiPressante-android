package fr.handipressante.app;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fr.handipressante.app.Data.Toilet;

public class ToiletSheetActivity extends AppCompatActivity {
    private Toilet mToilet;
    final int REQUEST_IMAGE_CAPTURE = 1;


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
        ORANGE(2, R.layout.pics_test),
        GREEN(3,R.layout.pics_test);

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

    public static ArrayList<ImageView> listPics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_sheet);

        // get info from parent view
        Intent intent = getIntent();
        mToilet = intent.getParcelableExtra("toilet");

        Toolbar toolbar =   (Toolbar) findViewById(R.id.toolbar_sheet);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitle("Retour");



        fillToiletSheet(mToilet);
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
        //opens camera app
        //TODO: Save thumbnail in carousel(viewpager)
        findViewById(R.id.photo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                Toast.makeText(getApplicationContext(), "Open camera App", Toast.LENGTH_SHORT ).show();
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

            }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView pics= (ImageView) findViewById(R.id.picture_block2);
            pics.setImageBitmap(imageBitmap);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.modification:
                Intent intent = new Intent(getApplicationContext(), ModificationSheet.class);
                intent.putExtra("toilet", mToilet);
                Toast.makeText(getApplicationContext(), "Open Sheet Modification", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            break;
            // Something else
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item_mod = menu.add(Menu.NONE, R.id.modification,1,R.string.modification);
        item_mod.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);
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
        previous.setImageResource(R.drawable.precedent);

        //next arrow
        ImageView next= (ImageView) findViewById(R.id.next);
        next.setImageResource(R.drawable.suivant);
        //picture slider
        listPics.add((ImageView)findViewById(R.id.picture_block1));
        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(this));


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

        addComment(toilet);

    }


    public void addComment(Toilet toilet){
        LinearLayout container = (LinearLayout) findViewById(R.id.comment_bubble);
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


}
