package fr.handipressante.app.ToiletEdition;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import fr.handipressante.app.Data.Toilet;
import fr.handipressante.app.R;

/**
 * Created by marc on 10/04/2016.
 */
public class AddToiletDialog extends DialogFragment
{
    private Toilet mToilet;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        mToilet = args.getParcelable("toilet");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_add_toilet_confirm_message)
                .setTitle(R.string.dialog_add_toilet_confirm)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), NameActivity.class);
                        intent.putExtra("toilet", mToilet);
                        intent.putExtra("new", true);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });

        return builder.create();
    }
}