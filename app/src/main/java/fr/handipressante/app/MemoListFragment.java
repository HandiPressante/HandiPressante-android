package fr.handipressante.app;


/**
 * Created by Nico on 06/03/2016.
 */

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.List;


public class MemoListFragment extends ListFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("MemoListFragment", "onActivityCreated");

        MemoListAdapter adapter = new MemoListAdapter(getActivity(), MemoManager.instance(getActivity().getApplicationContext()).getMemoList());
        MemoManager.instance(getActivity().getApplicationContext()).addMemoListListener(adapter);
        setListAdapter(adapter);
    }

    /* Request updates at startup */
    @Override
    public void onResume() {
        super.onResume();
        Log.i("MemoListFragment", "onResume");

        for (String file : getActivity().getApplicationContext().fileList()) {
            Log.i("MemoListFragment", "File : " + file);
        }

        if (MemoManager.instance(getActivity().getApplicationContext()).getMemoList().size() == 0) {
            MemoManager.instance(getActivity().getApplicationContext()).update();

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("MemoListFragment", "onPause");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Memo m = (Memo) getListAdapter().getItem(position);
        if (m != null) {
            // TODO : Load pdf

            File file = new File(getContext().getFilesDir(), m.getLocalPath());
            Log.i("MemoListFragment", file.getAbsolutePath());
            Uri contentUri = FileProvider.getUriForFile(getContext(), "fr.handipressante.app", file);
            Log.i("MemoListFragment", "Content uri : " + contentUri.toString());

            Intent intent = new Intent(Intent.ACTION_VIEW, contentUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.i("MemoListFragment", "PDF Activity Not Found Exception");
            }
        }
    }
}

