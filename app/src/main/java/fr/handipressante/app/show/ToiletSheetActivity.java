package fr.handipressante.app.show;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import fr.handipressante.app.Converters;
import fr.handipressante.app.data.Toilet;
import fr.handipressante.app.help.HelpSlideToiletSheet;
import fr.handipressante.app.R;
import fr.handipressante.app.server.Downloader;
import fr.handipressante.app.server.ToiletDownloader;
import fr.handipressante.app.edit.CommentEdition;
import fr.handipressante.app.edit.RatingActivity;

public class ToiletSheetActivity extends AppCompatActivity {

    private final String LOG_TAG = "ToiletSheetActivity";

    private Toilet mToilet;

    final int REQUEST_TOILET_EDIT = 1;
    final int REQUEST_ADD_COMMENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toiletsheet);

        // get info from parent view
        Intent intent = getIntent();
        mToilet = intent.getParcelableExtra("toilet");

        initToolbar();

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

    private void gotoToiletPosition() {
        LatLng toiletPosition = mToilet.getPosition();
        final Uri mUri = Uri.parse("geo:" + toiletPosition.latitude + "," + toiletPosition.longitude + "?q=" + toiletPosition.latitude + "," + toiletPosition.longitude);

        //intent to start a new activity
        Intent intent = new Intent(Intent.ACTION_VIEW, mUri);
        intent.setData(mUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //Listener that opens Maps when you click on Itinerary button
        findViewById(R.id.map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoToiletPosition();
            }
        });

        // "Go to" action
        RelativeLayout presentationBlock = (RelativeLayout) findViewById(R.id.presentation_block);
        presentationBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoToiletPosition();
            }
        });

        findViewById(R.id.more_infos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "more infos clicked");
                Intent intent = new Intent(getApplicationContext(), MoreInfosActivity.class);
                intent.putExtra("toilet", mToilet);
                startActivity(intent);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TOILET_EDIT && resultCode == 0 && data != null) {
            Toilet toilet = data.getParcelableExtra("toilet");
            if (toilet != null) {
                mToilet = toilet;
                fillToiletSheet(mToilet);
            }
        } else if (requestCode == REQUEST_ADD_COMMENT && resultCode == 0) {
            // nothing to do ?
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
        // Set icon whether adapted toilet or not
        ImageView handicapped= (ImageView) findViewById(R.id.handicapped);
        handicapped.setImageResource(Converters.pmrFromBoolean(toilet.isAdapted()));

        ImageView charged= (ImageView) findViewById(R.id.charged);
        charged.setImageResource(Converters.chargedFromBoolean(toilet.isCharged()));

        // Set toilet's name
        TextView name=(TextView)findViewById(R.id.toilet_name);
        name.setText(toilet.getName());

        /*
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
        });*/

        // Set general rate
        ImageView global_rate = (ImageView) findViewById(R.id.global_rate);
        global_rate.setImageResource(Converters.resourceFromRank(toilet.getRankAverage(), toilet.getRateWeight()));

        // Set cleanliness rate
        ImageView cleanliness_rate = (ImageView) findViewById(R.id.cleanliness_rate);
        cleanliness_rate.setImageResource(Converters.resourceFromRank(toilet.getRankCleanliness(), toilet.getRateWeight()));

        // Set facilities rate
        ImageView facilities_rate = (ImageView) findViewById(R.id.facilities_rate);
        facilities_rate.setImageResource(Converters.resourceFromRank(toilet.getRankFacilities(), toilet.getRateWeight()));

        // Set accessibility rate
        ImageView accessibility_rate = (ImageView) findViewById(R.id.accessibility_rate);
        accessibility_rate.setImageResource(Converters.resourceFromRank(toilet.getRankAccessibility(), toilet.getRateWeight()));

    }

    private void enableEdition() {
        findViewById(R.id.edit_rate).setEnabled(true);
        findViewById(R.id.add_comment).setEnabled(true);
    }
}
