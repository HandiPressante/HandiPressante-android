package fr.handipressante.app.memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fr.handipressante.app.data.Memo;
import fr.handipressante.app.R;


/**
 * Created by Nico on 06/03/2016.
 */
public class MemoListAdapter extends ArrayAdapter<Memo> {
    static class ViewHolder {
        TextView title;
    }

    public MemoListAdapter(Context context, List<Memo> memoList) {
        super(context, -1, memoList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listitem_memo, parent, false);

            holder = new ViewHolder();
            holder.title = (TextView) row.findViewById(R.id.title);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Memo memo = getItem(position);
        holder.title.setText(memo.getTitle());

        return row;
    }

    public void swapItems(List<Memo> memoList) {
        clear();
        addAll(memoList);
    }
}


