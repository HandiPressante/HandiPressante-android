package fr.handipressante.app.ToiletEdition;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import fr.handipressante.app.Converters;
import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.R;
import fr.handipressante.app.Server.Downloader;
import fr.handipressante.app.Server.ToiletDownloader;

public class RatingActivity extends AppCompatActivity implements Downloader.Listener<JSONObject> {
    private Toilet mToilet;
    private ProgressDialog mProgressDialog;

    private int mCleanliness = 0;
    private int mFacilities = 0;
    private int mAccessibility = 0;

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
    public void onResponse(JSONObject response) {
        mProgressDialog.dismiss();

        try {
            if (response != null && response.has("success") && response.getBoolean("success")) {
                if (response.has("toilet_cleanliness"))
                    mToilet.setRankCleanliness(response.getInt("toilet_cleanliness"));
                if (response.has("toilet_facilities"))
                    mToilet.setRankFacilities(response.getInt("toilet_facilities"));
                if (response.has("toilet_accessibility"))
                    mToilet.setRankAccessibility(response.getInt("toilet_accessibility"));

                Intent result = new Intent();
                result.putExtra("toilet", mToilet);

                setResult(0, result);
                finish();
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setResult(-1, null);
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
                cleanlinessView.setImageResource(Converters.rankFromInteger(position));
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
                facilitiesView.setImageResource(Converters.rankFromInteger(position));
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
                placeAccessibilityView.setImageResource(Converters.rankFromInteger(position));
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
