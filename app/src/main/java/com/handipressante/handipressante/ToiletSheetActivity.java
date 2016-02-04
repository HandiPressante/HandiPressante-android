package com.handipressante.handipressante;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;

public class ToiletSheetActivity extends FragmentActivity {
    private Toilet mToilet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_sheet);

        // get info from parent view
        Intent intent = getIntent();
        Integer id  = intent.getIntExtra("toiletId", -1);

        DataModel dataModel = DataModel.instance();
        mToilet = dataModel.getToilet(id);

        getActionBar().setLogo(R.drawable.back_icon);
        getActionBar().setTitle("Retour");

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);

        fillToiletSheet(mToilet);
    }

    public void onStart(){
        super.onStart();

        GeoPoint geo = mToilet.getCoordinates();
        final Uri mUri = Uri.parse("geo:" + geo.getLatitude() + "," + geo.getLongitude() + "?q=" + geo.getLatitude() + "," + geo.getLongitude());
        //Listener that opens Maps when tou click on Itinerary button
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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
        getMenuInflater().inflate(R.menu.menu_toilet_sheet, menu);
        return true;
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
