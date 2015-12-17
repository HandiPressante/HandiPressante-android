package com.handipressante.handipressante;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.LinearLayout.LayoutParams;

import org.osmdroid.util.GeoPoint;

import java.util.List;

public class ToiletSheetActivity extends FragmentActivity {

    private IDataModel _model = new TestDataModel();
    private Integer _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_sheet);

        // get info from parent view
        Intent intent = getIntent();
        Integer id  = intent.getIntExtra("idSheet", -1);
        _id = id;

        getActionBar().setLogo(R.drawable.back_icon);
        getActionBar().setTitle("Retour");

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);

        new DownloadSheetTask().execute(id);

        Sheet s = new Sheet();
        fillToiletSheet(s);
    }

    public void onStart(){
        super.onStart();
        //Toilet toilet = testModel.getToilet(_id);
        Toilet toilet = new Toilet(1, true, "adresse", new GeoPoint(48.3, -1.1), 0.0);
        toilet.setRankAverage(3);
        GeoPoint geo = toilet.getGeo();
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

    public void getSheet(int id){
        //methode existant dans le datamodel peut servir
        Log.d("Test", "test");
    }

    public void fillToiletSheet(Sheet sheet){

        // Set icon whether adapted toilet or not
        ImageView handicapped= (ImageView) findViewById(R.id.handicapped);
        if (sheet.get_isAdapted()) {
            handicapped.setImageResource(R.drawable.handicap_icon);
        } else {
            handicapped.setImageResource(R.drawable.not_handicap_icon);
        }

        // Set toilet's name
        TextView name=(TextView)findViewById(R.id.toilet_name);
        name.setText(sheet.get_name());

        // Set toilet's description (wiki)
        TextView description=(TextView)findViewById(R.id.toilet_description);
        if(sheet.get_description().isEmpty()){
            description.setText("Ces toilettes n'ont pas de description ! Soyez le premier à la remplir !");
            description.setTypeface(null, Typeface.ITALIC);
        }else{
            description.setText(sheet.get_description());
        }

        // Set general rate
        ImageView global_rate= (ImageView) findViewById(R.id.global_rate);
        switch (sheet.get_rankGeneral()){
            case 0 :
                global_rate.setImageResource(R.drawable.star_zero);
                break;
            case 1 :
                global_rate.setImageResource(R.drawable.star_one);
                break;
            case 2 :
                global_rate.setImageResource(R.drawable.star_two);
                break;
            case 3 :
                global_rate.setImageResource(R.drawable.star_three);
                break;
            case 4 :
                global_rate.setImageResource(R.drawable.star_four);
                break;
            case 5 :
                global_rate.setImageResource(R.drawable.star_five);
                break;
            default:
                global_rate.setImageResource(R.drawable.no_rate_stars);
                break;
        }


        // Set cleanliness rate
        ImageView cleanliness_rate= (ImageView) findViewById(R.id.cleanliness_rate);
        switch (sheet.get_rankCleanliness()){
            case 0 :
                cleanliness_rate.setImageResource(R.drawable.star_zero);
                break;
            case 1 :
                cleanliness_rate.setImageResource(R.drawable.star_one);
                break;
            case 2 :
                cleanliness_rate.setImageResource(R.drawable.star_two);
                break;
            case 3 :
                cleanliness_rate.setImageResource(R.drawable.star_three);
                break;
            case 4 :
                cleanliness_rate.setImageResource(R.drawable.star_four);
                break;
            case 5 :
                cleanliness_rate.setImageResource(R.drawable.star_five);
                break;
            default:
                cleanliness_rate.setImageResource(R.drawable.no_rate_stars);
                break;
        }


        // Set facilities rate
        ImageView facilities_rate= (ImageView) findViewById(R.id.facilities_rate);
        switch (sheet.get_rankFacilities()){
            case 0 :
                facilities_rate.setImageResource(R.drawable.star_zero);
                break;
            case 1 :
                facilities_rate.setImageResource(R.drawable.star_one);
                break;
            case 2 :
                facilities_rate.setImageResource(R.drawable.star_two);
                break;
            case 3 :
                facilities_rate.setImageResource(R.drawable.star_three);
                break;
            case 4 :
                facilities_rate.setImageResource(R.drawable.star_four);
                break;
            case 5 :
                facilities_rate.setImageResource(R.drawable.star_five);
                break;
            default:
                facilities_rate.setImageResource(R.drawable.no_rate_stars);
                break;
        }


        // Set accessibility rate
        ImageView accessibility_rate= (ImageView) findViewById(R.id.accessibility_rate);
        switch (sheet.get_rankAccessibility()){
            case 0 :
                accessibility_rate.setImageResource(R.drawable.star_zero);
                break;
            case 1 :
                accessibility_rate.setImageResource(R.drawable.star_one);
                break;
            case 2 :
                accessibility_rate.setImageResource(R.drawable.star_two);
                break;
            case 3 :
                accessibility_rate.setImageResource(R.drawable.star_three);
                break;
            case 4 :
                accessibility_rate.setImageResource(R.drawable.star_four);
                break;
            case 5 :
                accessibility_rate.setImageResource(R.drawable.star_five);
                break;
            default:
                accessibility_rate.setImageResource(R.drawable.no_rate_stars);
                break;
        }

        addComment(sheet);

    }



    public void addComment(Sheet sheetDownload){
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

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadSheetTask extends AsyncTask<Integer, Void, Sheet> {
        @Override
        protected Sheet doInBackground(Integer... params) {
            //IDataModel model = new OnlineDataModel(getBaseContext());
            IDataModel model = new TestDataModel();
            return new Sheet();
            //return model.getSheet(params[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Sheet result) {
            System.out.println("Sheet : " + result.get_id());
            Log.d("Debug", "########################");
            Log.d("Debug", "Toilette " + result.get_id());
            Log.d("Debug", "Name : " + result.get_name());
            Log.d("Debug", "PMR : " + result.get_isAdapted());
            Log.d("Debug", "Rank : " + result.get_rankGeneral());
            Log.d("Debug", "########################");

            fillToiletSheet(result);
        }


    }
}
