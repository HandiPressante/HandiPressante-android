package fr.handipressante.app.list;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.handipressante.app.Converters;
import fr.handipressante.app.data.Toilet;
import fr.handipressante.app.R;

/**
 * Created by Nico on 02/02/2016.
 */
public class ToiletListAdapter extends ArrayAdapter<Toilet> {
    static class ViewHolder {
        ImageView pmr;
        TextView address;
        ImageView rank;
        TextView distance;
        ImageView charged;
    }

    public ToiletListAdapter(Context context, List<Toilet> toilets) {
        super(context, -1, toilets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listitem_toilet, parent, false);

            holder = new ViewHolder();
            if(holder.pmr != null) {
                ((BitmapDrawable)holder.pmr.getDrawable()).getBitmap().recycle();
            }
            if(holder.rank != null) {
                ((BitmapDrawable)holder.rank.getDrawable()).getBitmap().recycle();
            }
            if(holder.charged !=null){
                ((BitmapDrawable)holder.charged.getDrawable()).getBitmap().recycle();
            }
            holder.pmr = (ImageView) row.findViewById(R.id.pmr);
            holder.address = (TextView) row.findViewById(R.id.address);
            holder.rank = (ImageView) row.findViewById(R.id.rank);
            holder.distance = (TextView) row.findViewById(R.id.distance);
            holder.charged = (ImageView) row.findViewById(R.id.charged);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Toilet toilet = getItem(position);
        holder.pmr.setImageResource(Converters.pmrFromBoolean(toilet.isAdapted()));
        holder.address.setText(toilet.getName());
        holder.rank.setImageResource(Converters.resourceFromRank(toilet.getRankAverage(), toilet.getRateWeight()));
        holder.distance.setText(Converters.formattedDistanceFromDouble(toilet.getDistance()));
        holder.charged.setImageResource(Converters.chargedFromBoolean(toilet.isCharged()));
        if (toilet.isCharged())
            holder.charged.setVisibility(View.VISIBLE);
        else
            holder.charged.setVisibility(View.INVISIBLE);

        return row;
    }

    public void swapItems(List<Toilet> toiletList) {
        clear();
        addAll(toiletList);
        notifyDataSetChanged();
    }
}
