package fr.handipressante.app.ToiletEdition;

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

import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.R;

/**
 * Created by marc on 23/05/2016.
 */
public class CommentEdition extends AppCompatActivity {
    private Toilet mToilet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_edition);

        Intent intent = getIntent();

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
                   /* EditText toiletNameField = (EditText) findViewById(R.id.toilet_name);
                    String toiletName = toiletNameField.getText().toString();
                    mToilet.setAddress(toiletName);

                    Intent intent = new Intent(getApplicationContext(), AccessibleActivity.class);
                    intent.putExtra("toilet", mToilet);
                    intent.putExtra("new", mNewToilet);
                    startActivityForResult(intent, 1);*/
                    Toast.makeText(getApplicationContext(),"Bien tenté, mais ce n'est pas encore prêt", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        } else if (comment.length() < 2){
            validComment = false;
            errorComment = getResources().getQuantityString(R.plurals.minchars,2, 2);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setResult(resultCode, data);
        finish();
    }
}
