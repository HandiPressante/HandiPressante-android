package fr.handipressante.app.ToiletEdition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.R;

public class AccessibleActivity extends AppCompatActivity {
    private Toilet mToilet;
    private boolean mNewToilet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toiletedition_activity_accessible);

        Intent intent = getIntent();
        mNewToilet = intent.getBooleanExtra("new", true);
        mToilet = intent.getParcelableExtra("toilet");

        Switch accessibleSwitch = (Switch) findViewById(R.id.accessibleSwitch);
        final ImageView accessibleView = (ImageView) findViewById(R.id.accessibleView);

        accessibleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    accessibleView.setImageResource(R.drawable.handicap_icon);
                else
                    accessibleView.setImageResource(R.drawable.not_handicap_icon);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (!mNewToilet) {
            accessibleSwitch.setChecked(mToilet.isAdapted());
            toolbar.setTitle(getString(R.string.edittoilet_edit));
        } else {
            toolbar.setTitle(getString(R.string.edittoilet_new));
        }

        Button validate = (Button) findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch accessibleSwitch = (Switch) findViewById(R.id.accessibleSwitch);
                mToilet.setAdapted(accessibleSwitch.isChecked());

                Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
                intent.putExtra("toilet", mToilet);
                intent.putExtra("new", mNewToilet);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setResult(resultCode, data);
        finish();
    }
}
