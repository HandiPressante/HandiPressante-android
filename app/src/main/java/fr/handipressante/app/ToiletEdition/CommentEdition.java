package fr.handipressante.app.ToiletEdition;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.R;
import fr.handipressante.app.Server.Downloader;
import fr.handipressante.app.Server.ToiletDownloader;

/**
 * Created by marc on 23/05/2016.
 */
public class CommentEdition extends AppCompatActivity implements Downloader.Listener<JSONObject> {
    private Integer mToiletId;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_edition);

        Intent intent = getIntent();
        mToiletId = intent.getIntExtra("toiletId", 0);

        EditText usernameField = (EditText) findViewById(R.id.username);
        EditText commentField = (EditText)findViewById(R.id.comment_edition);

        changeListener(usernameField);
        changeListener(commentField);


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

        new ToiletDownloader(this).postToiletComment(mToiletId, username, comment, this);
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
    public void onResponse(JSONObject response) {
        mProgressDialog.dismiss();

        try {
            if (response != null && response.has("error")) {
                int errorCode = response.getInt("error");
                if (errorCode == 0) {
                    Toast.makeText(getApplicationContext(), "Commentaire ajouté !", Toast.LENGTH_SHORT).show();
                    setResult(0, null);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Impossible d'envoyer le commentaire. (Code d'erreur : " +
                            errorCode + ").", Toast.LENGTH_LONG).show();
                    setResult(-1, null);
                    finish();
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "Désolé, une erreur s'est produite.", Toast.LENGTH_SHORT).show();
        setResult(-1, null);
        finish();
    }
}
