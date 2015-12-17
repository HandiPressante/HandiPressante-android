package com.handipressante.handipressante;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

public class ToiletSheetActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_sheet);

        // get info from parent view
        Intent intent = getIntent();
        Integer id  = intent.getIntExtra("idSheet", -1);

        getActionBar().setLogo(R.drawable.back_icon);
        getActionBar().setTitle("Liste");

        fillToiletSheet(id);

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
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
        Log.d("Test", "test");
    }

    public void fillToiletSheet(int id){
        OnlineDataModel odm = new OnlineDataModel(getBaseContext());
        //Sheet sheetDownload = odm.getSheet(id);
        Sheet sheetDownload = new Sheet(/*id */ 80,
                                        /*nom*/ "Toilette de la Mairie",
                                        /*Description*/ "L'accès à ces toilettes est relativement facile. La cabine est un peu petite mais elle est régulièrement nettoyée.",
                                        /*Général Rank*/ 4,
                                        /*Cleanliness Rank*/ 5,
                                        /*Facilities Rank*/ 2,
                                        /*Accessibility Rank*/ 4,
                                        /*isAdapted*/ true);

        // Set icon whether adapted toilet or not
        ImageView handicapped= (ImageView) findViewById(R.id.handicapped);
        if (sheetDownload.get_isAdapted()){
            handicapped.setImageResource(R.drawable.handicap_icon);
        }else{
            handicapped.setImageResource(R.drawable.not_handicap_icon);
        }

        // Set toilet's name
        TextView name=(TextView)findViewById(R.id.toilet_name);
        name.setText(sheetDownload.get_name());

        // Set toilet's description (wiki)
        TextView description=(TextView)findViewById(R.id.toilet_description);
        if(sheetDownload.get_description().isEmpty()){
            description.setText("Ces toilettes n'ont pas de description ! Soyez le premier à la remplir !");
            description.setTypeface(null, Typeface.ITALIC);
        }else{
            description.setText(sheetDownload.get_description());
        }

        // Set general rate
        ImageView global_rate= (ImageView) findViewById(R.id.global_rate);
        switch (sheetDownload.get_rankGeneral()){
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
        switch (sheetDownload.get_rankCleanliness()){
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
        switch (sheetDownload.get_rankFacilities()){
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
        switch (sheetDownload.get_rankAccessibility()){
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

        addComment(sheetDownload);

    }



    public void addComment(Sheet sheetDownload){
        LinearLayout container = (LinearLayout) findViewById(R.id.comment_bubble);

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

    }
}
