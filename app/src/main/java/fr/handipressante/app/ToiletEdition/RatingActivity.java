package fr.handipressante.app.ToiletEdition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import fr.handipressante.app.Converters;
import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.R;

public class RatingActivity extends AppCompatActivity {
    private Toilet mToilet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toiletedition_activity_rating);

        Intent intent = getIntent();
        mToilet = intent.getParcelableExtra("toilet");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mToilet.getAddress());

        initSpinners();

        Button validate = (Button) findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRates();
            }
        });
    }

    private void submitRates() {
        // TODO
    }

    private void initSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.array_rating_values,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner cleanlinessSpinner = (Spinner) findViewById(R.id.cleanlinessSpinner);
        cleanlinessSpinner.setAdapter(adapter);
        cleanlinessSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImageView cleanlinessView = (ImageView) findViewById(R.id.cleanlinessView);
                cleanlinessView.setImageResource(Converters.rankFromInteger(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner facilitiesSpinner = (Spinner) findViewById(R.id.facilitiesSpinner);
        facilitiesSpinner.setAdapter(adapter);
        facilitiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImageView facilitiesView = (ImageView) findViewById(R.id.facilitiesView);
                facilitiesView.setImageResource(Converters.rankFromInteger(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner placeAccessibilitySpinner = (Spinner) findViewById(R.id.placeAccessibilitySpinner);
        placeAccessibilitySpinner.setAdapter(adapter);
        placeAccessibilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImageView placeAccessibilityView = (ImageView) findViewById(R.id.placeAccessibilityView);
                placeAccessibilityView.setImageResource(Converters.rankFromInteger(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
