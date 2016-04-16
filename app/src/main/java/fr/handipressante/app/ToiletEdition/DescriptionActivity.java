package fr.handipressante.app.ToiletEdition;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.R;
import fr.handipressante.app.Server.Downloader;
import fr.handipressante.app.Server.ToiletDownloader;

public class DescriptionActivity extends AppCompatActivity implements Downloader.Listener<JSONObject> {
    private Toilet mToilet;
    private boolean mNewToilet;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toiletedition_activity_description);

        Intent intent = getIntent();
        mNewToilet = intent.getBooleanExtra("new", true);
        mToilet = intent.getParcelableExtra("toilet");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (!mNewToilet) {
            EditText toiletDescriptionField = (EditText) findViewById(R.id.toilet_description);
            toiletDescriptionField.setText(mToilet.getDescription());
            toolbar.setTitle(getString(R.string.edittoilet_edit));
        } else {
            toolbar.setTitle(getString(R.string.edittoilet_new));
        }

        Button validate = (Button) findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText toiletDescriptionField = (EditText) findViewById(R.id.toilet_description);
                String toiletDescription = toiletDescriptionField.getText().toString();
                mToilet.setDescription(toiletDescription);

                save();
            }
        });
    }

    private void save() {
        EditText toiletDescriptionField = (EditText) findViewById(R.id.toilet_description);
        toiletDescriptionField.setEnabled(false);
        Button validate = (Button) findViewById(R.id.validate);
        validate.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Enregistrement en cours ...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        new ToiletDownloader(this).postToilet(mToilet, mNewToilet, this);
    }

    @Override
    public void onResponse(JSONObject response) {
        mProgressDialog.dismiss();

        try {
            if (response != null && response.has("success") && response.getBoolean("success")) {
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
}
