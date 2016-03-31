package fr.handipressante.app;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;

import fr.handipressante.app.Data.Toilet;

/**
 * Created by marc on 26/03/2016.
 */
public class ModificationSheet extends FragmentActivity    {
        private Toilet mToilet;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_sheet);

        // get info from parent view
            Intent intent = getIntent();
            mToilet = intent.getParcelableExtra("toilet");

        getActionBar().setLogo(R.drawable.back_icon);
            getActionBar().setTitle("Retour");


            ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);



        fillToiletSheet(mToilet);
    }

    public void onStart() {
        super.onStart();


        //opens camera app
        //TODO: Save thumbnail in carousel(viewpager)
        /*findViewById(R.id.photo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 0);
                }
            }
        });*/
    }
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView pics= (ImageView) findViewById(R.id.picture_block2);
            pics.setImageBitmap(imageBitmap);
        }
    }*/
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
        name.setHint(toilet.getAddress());

        // Set toilet's description (wiki)
        TextView description=(TextView)findViewById(R.id.toilet_description);

        if(toilet.getDescription().isEmpty()){
            description.setHint("Ces toilettes n'ont pas de description ! Soyez le premier à la remplir !"); // TODO : String res
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
            EditText no_comment = new EditText(this);
            no_comment.setHint(" Ajoutez vos commentaires ici !");
            no_comment.setTypeface(null, Typeface.ITALIC);
            comment_layout.addView(no_comment);
        }

    }


}

