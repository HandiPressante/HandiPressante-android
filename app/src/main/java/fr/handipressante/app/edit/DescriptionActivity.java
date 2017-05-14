package fr.handipressante.app.edit;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import fr.handipressante.app.data.Toilet;
import fr.handipressante.app.R;

public class DescriptionActivity extends AbstractFieldEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toiletedition_description);

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
        EditText toiletDescriptionField = (EditText) findViewById(R.id.toilet_description);
        toiletDescriptionField.setText(toilet.getDescription());
    }

    @Override
    protected boolean checkData() {
        return true;
    }

    @Override
    protected void saveData(Toilet toilet) {
        EditText toiletDescriptionField = (EditText) findViewById(R.id.toilet_description);
        String toiletDescription = toiletDescriptionField.getText().toString();
        toilet.setDescription(toiletDescription);
    }

    @Override
    protected Class<?> nextEditActivity() {
        return null;
    }

    @Override
    protected void save() {
        EditText toiletDescriptionField = (EditText) findViewById(R.id.toilet_description);
        toiletDescriptionField.setEnabled(false);
        Button validate = (Button) findViewById(R.id.validate);
        validate.setEnabled(false);

        super.save();
    }
}
