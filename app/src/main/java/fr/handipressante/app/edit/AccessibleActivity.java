package fr.handipressante.app.edit;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import fr.handipressante.app.Converters;
import fr.handipressante.app.data.Toilet;
import fr.handipressante.app.R;

public class AccessibleActivity extends AbstractFieldEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toiletedition_accessible);

        Switch accessibleSwitch = (Switch) findViewById(R.id.accessibleSwitch);
        final ImageView accessibleView = (ImageView) findViewById(R.id.accessibleView);

        accessibleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                accessibleView.setImageResource(Converters.pmrFromBoolean(isChecked));
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
        Switch accessibleSwitch = (Switch) findViewById(R.id.accessibleSwitch);
        accessibleSwitch.setChecked(toilet.isAdapted());
    }

    @Override
    protected boolean checkData() {
        return true;
    }

    @Override
    protected void saveData(Toilet toilet) {
        Switch accessibleSwitch = (Switch) findViewById(R.id.accessibleSwitch);
        toilet.setAdapted(accessibleSwitch.isChecked());
    }

    @Override
    protected Class<?> nextEditActivity() {
        return ChargedActivity.class;
    }

    @Override
    protected void save() {
        Switch accessibleSwitch = (Switch) findViewById(R.id.accessibleSwitch);
        accessibleSwitch.setEnabled(false);
        Button validate = (Button) findViewById(R.id.validate);
        validate.setEnabled(false);

        super.save();
    }
}
