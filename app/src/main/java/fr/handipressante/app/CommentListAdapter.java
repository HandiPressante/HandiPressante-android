package fr.handipressante.app;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.handipressante.app.Data.Comment;
import fr.handipressante.app.Data.Toilet;

/**
 * Created by Nico on 02/02/2016.
 */
public class CommentListAdapter extends ArrayAdapter<Comment> {
    static class ViewHolder {
        TextView username;
        TextView content;
        TextView date;
    }

    public CommentListAdapter(Context context, List<Comment> comments) {
        super(context, -1, comments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.comment_item, parent, false);

            holder = new ViewHolder();
            holder.username = (TextView) row.findViewById(R.id.username);
            holder.content = (TextView) row.findViewById(R.id.content);
            holder.date = (TextView) row.findViewById(R.id.date);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Comment comment = getItem(position);
        holder.username.setText(comment.getUsername());
        holder.content.setText(comment.getContent());
        holder.date.setText(comment.getPostdate());

        return row;
    }

    public void swapItems(List<Comment> commentList) {
        clear();
        addAll(commentList);
        notifyDataSetChanged();
    }
}
