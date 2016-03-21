package fr.handipressante.app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import fr.handipressante.app.Data.MemoDAO;


/**
 * Created by Nico on 06/03/2016.
 */
public class MemoListAdapter extends CursorAdapter {
    static class ViewHolder {
        TextView title;
    }

    public MemoListAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.item_fragment_memolist, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.title = (TextView) row.findViewById(R.id.title);

        row.setTag(holder);
        return row;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.title.setText(cursor.getString(cursor.getColumnIndex(MemoDAO.COL_TITLE)));
    }
}

