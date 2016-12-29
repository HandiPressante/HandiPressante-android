package fr.handipressante.app.edit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import fr.handipressante.app.Converters;
import fr.handipressante.app.data.Toilet;
import fr.handipressante.app.R;
import fr.handipressante.app.server.Downloader;
import fr.handipressante.app.server.ToiletDownloader;

public class RatingActivity extends AppCompatActivity implements Downloader.Listener<Boolean> {
    private Toilet mToilet;
    private ProgressDialog mProgressDialog;

    private int mCleanliness = 0;
    private int mFacilities = 0;
    private int mAccessibility = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toiletedition_rating);

        Intent intent = getIntent();
        mToilet = intent.getParcelableExtra("toilet");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mToilet.getName());

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
        Spinner placeAccessibilitySpinner = (Spinner) findViewById(R.id.placeAccessibilitySpinner);
        Spinner cleanlinessSpinner = (Spinner) findViewById(R.id.cleanlinessSpinner);
        Spinner facilitiesSpinner = (Spinner) findViewById(R.id.facilitiesSpinner);
        Button validate = (Button) findViewById(R.id.validate);

        placeAccessibilitySpinner.setEnabled(false);
        cleanlinessSpinner.setEnabled(false);
        facilitiesSpinner.setEnabled(false);
        validate.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Enregistrement en cours ...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        new ToiletDownloader(this).postToiletRate(mToilet, mCleanliness, mFacilities, mAccessibility, this);
    }

    @Override
    public void onResponse(Boolean success) {
        mProgressDialog.dismiss();

        if (success) {
            Integer oldWeight = mToilet.getRateWeight();
            Float oldCleanlinessAvg = mToilet.getRankCleanliness();
            Float oldFacilitiesAvg = mToilet.getRankFacilities();
            Float oldAccessibilityAvg = mToilet.getRankAccessibility();

            Float newCleanlinessAvg = (oldCleanlinessAvg*oldWeight + mCleanliness) / (oldWeight + 1);
            Float newFacilitiesAvg = (oldFacilitiesAvg*oldWeight + mFacilities) / (oldWeight + 1);
            Float newAccessibilityAvg = (oldAccessibilityAvg*oldWeight + mAccessibility) / (oldWeight + 1);

            mToilet.setRankCleanliness(newCleanlinessAvg);
            mToilet.setRankFacilities(newFacilitiesAvg);
            mToilet.setRankAccessibility(newAccessibilityAvg);
            mToilet.setRateWeight(oldWeight + 1);

            Intent result = new Intent();
            result.putExtra("toilet", mToilet);
            Toast.makeText(getApplicationContext(), R.string.thanks_for_contributing, Toast.LENGTH_LONG).show();

            setResult(0, result);
        } else {
            setResult(-1, null);
        }

        finish();
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
                cleanlinessView.setImageResource(Converters.resourceFromRank(position));
                mCleanliness = position;
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
                facilitiesView.setImageResource(Converters.resourceFromRank(position));
                mFacilities = position;
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
                placeAccessibilityView.setImageResource(Converters.resourceFromRank(position));
                mAccessibility = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setResult(resultCode, data);
        finish();
    }
}
