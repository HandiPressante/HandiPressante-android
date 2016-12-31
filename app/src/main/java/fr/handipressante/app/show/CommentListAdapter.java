package fr.handipressante.app.show;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import fr.handipressante.app.data.Comment;
import fr.handipressante.app.R;

/**
 * Created by Nico on 02/02/2016.
 */
public class CommentListAdapter extends ArrayAdapter<Comment> {
    static class ViewHolder {
        TextView username;
        TextView content;
        TextView date;
        Button alert;
    }

    public interface AlertButtonListener {
        void onClick(final Integer commentId);
    }
    private AlertButtonListener mAlertButtonListener;

    public CommentListAdapter(Context context, List<Comment> comments) {
        super(context, -1, comments);
    }

    public void setAlertButtonListener(AlertButtonListener listener) {
        mAlertButtonListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listitem_comment, parent, false);

            holder = new ViewHolder();
            holder.username = (TextView) row.findViewById(R.id.username);
            holder.content = (TextView) row.findViewById(R.id.content);
            holder.date = (TextView) row.findViewById(R.id.date);
            holder.alert = (Button) row.findViewById(R.id.alert_comment);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Comment comment = getItem(position);
        holder.username.setText(comment.getUsername());
        holder.content.setText(comment.getContent());
        holder.date.setText(comment.getPostdate());
        holder.alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAlertButtonListener != null) {
                    mAlertButtonListener.onClick(comment.getId());
                }
            }
        });

        return row;
    }

    public void swapItems(List<Comment> commentList) {
        clear();
        addAll(commentList);
        notifyDataSetChanged();
    }
}
