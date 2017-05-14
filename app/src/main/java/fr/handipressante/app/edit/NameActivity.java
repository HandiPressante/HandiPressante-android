package fr.handipressante.app.edit;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fr.handipressante.app.data.Toilet;
import fr.handipressante.app.R;

public class NameActivity extends AbstractFieldEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toiletedition_name);

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

        if (!isNewToilet()) {
            toolbar.setTitle(getString(R.string.edittoilet_edit));
        } else {
            toolbar.setTitle(getString(R.string.edittoilet_new));
        }

        Button validate = (Button) findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    @Override
    protected void fillExistingData(final Toilet toilet) {
        EditText toiletNameField = (EditText) findViewById(R.id.toilet_name);
        toiletNameField.setText(toilet.getName());
    }

    @Override
    protected boolean checkData() {
        EditText toiletNameField = (EditText) findViewById(R.id.toilet_name);
        String toiletName = toiletNameField.getText().toString();
        boolean valid = true;
        String error = "";

        if (toiletName.isEmpty()) {
            valid = false;
            error = getString(R.string.required_field);
        } else if (toiletName.length() > 40) {
            valid = false;
            error = getResources().getQuantityString(R.plurals.maxchars, 40, 40);
        }

        TextView errorView = (TextView) findViewById(R.id.error);
        errorView.setText(error);

        Button validate = (Button) findViewById(R.id.validate);
        validate.setEnabled(valid);

        return valid;
    }

    @Override
    protected void saveData(Toilet toilet) {
        EditText toiletNameField = (EditText) findViewById(R.id.toilet_name);
        String toiletName = toiletNameField.getText().toString();
        toilet.setName(toiletName);
    }

    @Override
    protected Class<?> nextEditActivity() {
        return AccessibleActivity.class;
    }

    @Override
    protected void save() {
        EditText toiletNameField = (EditText) findViewById(R.id.toilet_name);
        toiletNameField.setEnabled(false);
        Button validate = (Button) findViewById(R.id.validate);
        validate.setEnabled(false);

        super.save();
    }
}
