package fr.handipressante.app.ToiletEdition;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.R;

public class NameActivity extends AppCompatActivity {
    private Toilet mToilet;
    private boolean mNewToilet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toiletedition_activity_name);

        Intent intent = getIntent();
        mNewToilet = intent.getBooleanExtra("new", true);
        mToilet = intent.getParcelableExtra("toilet");

        EditText toiletNameField = (EditText) findViewById(R.id.toilet_name);
        toiletNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkData();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (!mNewToilet) {
            toiletNameField.setText(mToilet.getAddress());
            toolbar.setTitle(getString(R.string.edittoilet_edit));
        } else {
            toolbar.setTitle(getString(R.string.edittoilet_new));
        }

        Button validate = (Button) findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) {
                    EditText toiletNameField = (EditText) findViewById(R.id.toilet_name);
                    String toiletName = toiletNameField.getText().toString();
                    mToilet.setAddress(toiletName);

                    Intent intent = new Intent(getApplicationContext(), AccessibleActivity.class);
                    intent.putExtra("toilet", mToilet);
                    intent.putExtra("new", mNewToilet);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    private boolean checkData() {
        EditText toiletNameField = (EditText) findViewById(R.id.toilet_name);
        String toiletName = toiletNameField.getText().toString();
        boolean valid = true;
        String error = "";

        if (toiletName.isEmpty()) {
            valid = false;
            error = getString(R.string.required_field);
        } else if (toiletName.length() > 30) {
            valid = false;
            error = getResources().getQuantityString(R.plurals.maxchars, 30, 30);
        }

        TextView errorView = (TextView) findViewById(R.id.error);
        errorView.setText(error);

        Button validate = (Button) findViewById(R.id.validate);
        validate.setEnabled(valid);

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setResult(resultCode, data);
        finish();
    }
}
