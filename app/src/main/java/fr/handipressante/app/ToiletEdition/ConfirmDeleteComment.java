package fr.handipressante.app.ToiletEdition;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.R;

/**
 * Created by marc on 24/05/2016.
 */
public class ConfirmDeleteComment extends DialogFragment
{
    private Toilet mToilet;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        mToilet = (Toilet) args.getParcelable("toilet");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(R.string.dialog_delete_comment)
                .setSingleChoiceItems(R.array.array_why_delete_comment,-1,  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }

                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getContext(),"Ce commentaire est en cours de suppression..",Toast.LENGTH_SHORT).show();
                        //TODO: delete the associated comment
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getContext(),"Opération annulée",Toast.LENGTH_SHORT).show();
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}