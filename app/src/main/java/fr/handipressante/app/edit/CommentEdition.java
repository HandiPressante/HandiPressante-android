package fr.handipressante.app.edit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fr.handipressante.app.R;
import fr.handipressante.app.server.CommentDownloader;
import fr.handipressante.app.server.Downloader;
import fr.handipressante.app.server.ToiletDownloader;

/**
 * Created by marc on 23/05/2016.
 */
public class CommentEdition extends AppCompatActivity implements Downloader.Listener<Boolean> {
    private Integer mToiletId;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        mToiletId = intent.getIntExtra("toiletId", 0);

        EditText usernameField = (EditText) findViewById(R.id.username);
        EditText commentField = (EditText)findViewById(R.id.comment_edition);

        changeListener(usernameField);
        changeListener(commentField);


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lastUsername = sharedPrefs.getString("last_username", new String());
        if (!lastUsername.isEmpty()) {
            usernameField.setText(lastUsername);
            commentField.requestFocus();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ajout d'un nouveau commentaire");


        Button validate = (Button) findViewById(R.id.validate_comment);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) {
                    save();
                }
            }
        });
    }

    private void save() {
        EditText usernameField = (EditText) findViewById(R.id.username);
        EditText commentField = (EditText)findViewById(R.id.comment_edition);
        Button validate = (Button) findViewById(R.id.validate_comment);

        if (usernameField == null || commentField == null || validate == null)
            return;

        usernameField.setEnabled(false);
        commentField.setEnabled(false);
        validate.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Veuillez patienter");
        mProgressDialog.setMessage("Enregistrement en cours ...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        String username = usernameField.getText().toString();
        String comment = commentField.getText().toString();

        new CommentDownloader(this).postToiletComment(mToiletId, username, comment, this);
    }

    private boolean checkData() {
        EditText usernameField = (EditText) findViewById(R.id.username);
        EditText commentField = (EditText) findViewById(R.id.comment_edition) ;

        String userName = usernameField.getText().toString();
        String comment = commentField.getText().toString();
        boolean validUserName = true;
        boolean validComment = true;
        String errorUsername = "";
        String errorComment = "";

        if (userName.isEmpty()) {
            validUserName = false;
            errorUsername = getString(R.string.required_field);
        } else if (userName.length() > 40) {
            validUserName = false;
            errorUsername = getResources().getQuantityString(R.plurals.maxchars, 40, 40);
        }
        TextView errorUserNameView = (TextView) findViewById(R.id.error_username);
        errorUserNameView.setText(errorUsername);


        if (comment.isEmpty()){
            validComment = false;
            errorComment = getString(R.string.required_field);
        } else if (comment.length() < 2) {
            validComment = false;
            errorComment = getResources().getQuantityString(R.plurals.minchars, 2, 2);
        } else if (comment.length() > 255) {
            validComment = false;
            errorComment = getResources().getQuantityString(R.plurals.maxchars, 255, 255);
        }

        TextView errorCommentView = (TextView) findViewById(R.id.error_comment);
        errorCommentView.setText(errorComment);

        Button validate = (Button) findViewById(R.id.validate_comment);
        validate.setEnabled(validUserName && validComment);

        return (validUserName && validComment);
    }

    private void changeListener(EditText field){
        field.addTextChangedListener(new TextWatcher() {
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
    }

    @Override
    public void onResponse(Boolean success) {
        mProgressDialog.dismiss();

        if (success) {
            Toast.makeText(getApplicationContext(), R.string.thanks_for_contributing, Toast.LENGTH_LONG).show();

            EditText usernameField = (EditText) findViewById(R.id.username);
            String username = usernameField.getText().toString();

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor e = sharedPrefs.edit();
            e.putString("last_username", username);
            e.apply();

            setResult(0, null);
        } else {
            setResult(-1, null);
        }

        finish();
    }
}
