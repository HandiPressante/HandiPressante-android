package fr.handipressante.app.ToiletEdition;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import fr.handipressante.app.R;

/**
 * Created by marc on 25/05/2016.
 */
public class AddWithLongPressDialog extends DialogFragment
{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Appuyez longtemps à l'endroit désiré pour démarrer l'ajout.")
                .setTitle(R.string.dialog_ready_to_add)
                .setPositiveButton(R.string.gotit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}