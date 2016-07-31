package fr.handipressante.app.ToiletMap;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_add_toilet_instructions)
                .setTitle(R.string.dialog_add_toilet)
                .setPositiveButton(R.string.gotit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder.create();
    }
}