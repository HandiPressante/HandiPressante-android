package fr.handipressante.app.edit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import fr.handipressante.app.R;
import fr.handipressante.app.data.Toilet;
import fr.handipressante.app.server.Downloader;
import fr.handipressante.app.server.ToiletDownloader;

public abstract class AbstractFieldEditActivity extends AppCompatActivity implements Downloader.Listener<Boolean> {
    private Toilet mToilet;
    private boolean mNewToilet;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mNewToilet = intent.getBooleanExtra("new", true);
        mToilet = intent.getParcelableExtra("toilet");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mNewToilet) {
            fillExistingData(mToilet);
        }
    }

    protected abstract void fillExistingData(final Toilet toilet);

    protected abstract boolean checkData();

    protected abstract void saveData(Toilet toilet);

    protected abstract Class<?> nextEditActivity();

    protected boolean isNewToilet() {
        return mNewToilet;
    }

    protected void validate() {
        if (checkData()) {
            saveData(mToilet);

            if (mNewToilet && nextEditActivity() != null) {
                Intent intent = new Intent(getApplicationContext(), nextEditActivity());
                intent.putExtra("toilet", mToilet);
                intent.putExtra("new", mNewToilet);
                startActivityForResult(intent, 1);
            } else {
                save();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setResult(resultCode, data);
        finish();
    }

    protected void save() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Enregistrement en cours ...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        new ToiletDownloader(this).postToilet(mToilet, mNewToilet, this);
    }

    @Override
    public void onResponse(Boolean success) {
        mProgressDialog.dismiss();

        if (success) {
            Intent result = new Intent();
            result.putExtra("toilet", mToilet);
            Toast.makeText(getApplicationContext(), R.string.thanks_for_contributing, Toast.LENGTH_LONG).show();

            setResult(0, result);
        } else {
            setResult(-1, null);
        }

        finish();
    }
}
