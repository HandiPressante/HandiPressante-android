package fr.handipressante.app;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.osmdroid.util.GeoPoint;

import fr.handipressante.app.Data.Toilet;

/**
 * Created by marc on 26/03/2016.
 */
public class ModificationSheet extends FragmentActivity {
    private Toilet mToilet;
    private RatingBar rb;
    private boolean isHandi;

    private ImageView stars;


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
            case R.id.modification:
                //Intent intent = new Intent(getApplicationContext(), ModificationSheet.class);
                //intent.putExtra("toilet", mToilet);
                Toast.makeText(getApplicationContext(), "Saving...", Toast.LENGTH_SHORT).show();
                // startActivity(intent);
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
        MenuItem item_mod = menu.add(Menu.NONE, R.id.modification, 1, R.string.save);
        item_mod.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    public void fillToiletSheet(final Toilet toilet) {
        // Set icon whether adapted toilet or not
        final ImageView handicapped = (ImageView) findViewById(R.id.handicapped);
        final Switch logoIsPresent = (Switch)findViewById(R.id.switchlogo);
        if (toilet.isAdapted()) {
            handicapped.setImageResource(R.drawable.handicap_icon);
            isHandi = true;
        } else {
            handicapped.setImageResource(R.drawable.not_handicap_icon);
            isHandi = false;
        }
        logoIsPresent.setChecked(isHandi);

        logoIsPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHandi) {
                    handicapped.setImageResource(R.drawable.not_handicap_icon);
                    isHandi = false;
                } else {
                    handicapped.setImageResource(R.drawable.handicap_icon);
                    isHandi = true;
                }
            }
        });

        // Set toilet's name
        TextView name = (TextView) findViewById(R.id.toilet_name);
        name.setHint(toilet.getAddress());
        name.setText(toilet.getAddress());

        // Set toilet's description (wiki)
        TextView description = (TextView) findViewById(R.id.toilet_description);

        if (toilet.getDescription().isEmpty()) {
            description.setHint("Ces toilettes n'ont pas de description ! Soyez le premier à la remplir !"); // TODO : String res
            description.setTypeface(null, Typeface.ITALIC);
        } else {
            description.setText(toilet.getDescription());
        }


        // Set general rate
        ImageView global_rate = (ImageView) findViewById(R.id.global_rate);
        global_rate.setImageResource(Converters.rankFromInteger(toilet.getRankAverage()));

        // Set cleanliness rate
        final ImageView cleanliness_rate = (ImageView) findViewById(R.id.cleanliness_rate);
        cleanliness_rate.setImageResource(Converters.rankFromInteger(toilet.getRankCleanliness()));
         Spinner cleanliness_spinner = (Spinner) findViewById(R.id.cleanliness_spinner);

        ArrayAdapter<CharSequence> cleanliness_adapter = ArrayAdapter.createFromResource(this,
                R.array.number_of_stars, android.R.layout.simple_spinner_item);
        cleanliness_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cleanliness_spinner.setAdapter(cleanliness_adapter);

        cleanliness_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                     int arg2, long arg3) {
                switch (arg2){
                    case 0:
                        cleanliness_rate.setImageResource(R.drawable.star_zero);
                        break;
                    case 1:
                        cleanliness_rate.setImageResource(R.drawable.star_one);
                        break;
                    case 2:
                        cleanliness_rate.setImageResource(R.drawable.star_two);
                        break;
                    case 3:
                        cleanliness_rate.setImageResource(R.drawable.star_three);
                        break;
                    case 4:
                        cleanliness_rate.setImageResource(R.drawable.star_four);
                        break;
                    case 5:
                        cleanliness_rate.setImageResource(R.drawable.star_five);
                        break;
                    default:
                        cleanliness_rate.setImageResource(Converters.rankFromInteger(toilet.getRankCleanliness()));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                cleanliness_rate.setImageResource(Converters.rankFromInteger(toilet.getRankCleanliness()));
            }
        });


        // Set facilities rate
        final ImageView facilities_rate = (ImageView) findViewById(R.id.facilities_rate);
        facilities_rate.setImageResource(Converters.rankFromInteger(toilet.getRankFacilities()));

        Spinner equipement_spinner = (Spinner) findViewById(R.id.equipement_spinner);
        ArrayAdapter<CharSequence> equipement_adapter = ArrayAdapter.createFromResource(this,
                R.array.number_of_stars, android.R.layout.simple_spinner_item);
        equipement_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        equipement_spinner.setAdapter(equipement_adapter);

        equipement_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                switch (arg2){
                    case 0:
                        facilities_rate.setImageResource(R.drawable.star_zero);
                        break;
                    case 1:
                        facilities_rate.setImageResource(R.drawable.star_one);
                        break;
                    case 2:
                        facilities_rate.setImageResource(R.drawable.star_two);
                        break;
                    case 3:
                        facilities_rate.setImageResource(R.drawable.star_three);
                        break;
                    case 4:
                        facilities_rate.setImageResource(R.drawable.star_four);
                        break;
                    case 5:
                        facilities_rate.setImageResource(R.drawable.star_five);
                        break;
                    default:
                        facilities_rate.setImageResource(Converters.rankFromInteger(toilet.getRankCleanliness()));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                cleanliness_rate.setImageResource(Converters.rankFromInteger(toilet.getRankCleanliness()));
            }
        });


        // Set accessibility rate
        final ImageView accessibility_rate = (ImageView) findViewById(R.id.accessibility_rate);
        accessibility_rate.setImageResource(Converters.rankFromInteger(toilet.getRankAccessibility()));

        Spinner accessibility_spinner = (Spinner) findViewById(R.id.accessibility_spinner);
        ArrayAdapter<CharSequence> accessibility_adapter = ArrayAdapter.createFromResource(this,
                R.array.number_of_stars, android.R.layout.simple_spinner_item);
        accessibility_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accessibility_spinner.setAdapter(equipement_adapter);

        accessibility_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                switch (arg2){
                    case 0:
                        accessibility_rate.setImageResource(R.drawable.star_zero);
                        break;
                    case 1:
                        accessibility_rate.setImageResource(R.drawable.star_one);
                        break;
                    case 2:
                        accessibility_rate.setImageResource(R.drawable.star_two);
                        break;
                    case 3:
                        accessibility_rate.setImageResource(R.drawable.star_three);
                        break;
                    case 4:
                        accessibility_rate.setImageResource(R.drawable.star_four);
                        break;
                    case 5:
                        accessibility_rate.setImageResource(R.drawable.star_five);
                        break;
                    default:
                        accessibility_rate.setImageResource(Converters.rankFromInteger(toilet.getRankAccessibility()));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                accessibility_rate.setImageResource(Converters.rankFromInteger(toilet.getRankAccessibility()));
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();


    }
}

