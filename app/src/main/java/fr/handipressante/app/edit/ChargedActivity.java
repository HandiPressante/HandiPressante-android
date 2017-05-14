package fr.handipressante.app.edit;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import fr.handipressante.app.Converters;
import fr.handipressante.app.R;
import fr.handipressante.app.data.Toilet;

public class ChargedActivity extends AbstractFieldEditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toiletedition_charged);

        Switch chargedSwitch = (Switch) findViewById(R.id.chargedSwitch);
        final ImageView chargedView = (ImageView) findViewById(R.id.chargedView);

        chargedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chargedView.setImageResource(Converters.chargedFromBoolean(isChecked));
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
        Switch chargedSwitch = (Switch) findViewById(R.id.chargedSwitch);
        chargedSwitch.setChecked(toilet.isCharged());
    }

    @Override
    protected boolean checkData() {
        return true;
    }

    @Override
    protected void saveData(Toilet toilet) {
        Switch chargedSwitch = (Switch) findViewById(R.id.chargedSwitch);
        toilet.setCharged(chargedSwitch.isChecked());
    }

    @Override
    protected Class<?> nextEditActivity() {
        return DescriptionActivity.class;
    }

    @Override
    protected void save() {
        Switch chargedSwitch = (Switch) findViewById(R.id.chargedSwitch);
        chargedSwitch.setEnabled(false);
        Button validate = (Button) findViewById(R.id.validate);
        validate.setEnabled(false);

        super.save();
    }
}
