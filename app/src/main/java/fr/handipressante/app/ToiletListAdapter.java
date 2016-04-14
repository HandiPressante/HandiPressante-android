package fr.handipressante.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.handipressante.app.Data.Toilet;

/**
 * Created by Nico on 02/02/2016.
 */
public class ToiletListAdapter extends ArrayAdapter<Toilet> {
    static class ViewHolder {
        ImageView pmr;
        TextView address;
        ImageView rank;
        TextView distance;
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
            row = inflater.inflate(R.layout.item_fragment_list, parent, false);

            holder = new ViewHolder();
            holder.pmr = (ImageView) row.findViewById(R.id.pmr);
            holder.address = (TextView) row.findViewById(R.id.address);
            holder.rank = (ImageView) row.findViewById(R.id.rank);
            holder.distance = (TextView) row.findViewById(R.id.distance);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Toilet toilet = getItem(position);
        holder.pmr.setImageResource(Converters.pmrFromBoolean(toilet.isAdapted()));
        holder.address.setText(toilet.getAddress());
        holder.rank.setImageResource(Converters.rankFromInteger(toilet.getRankAverage()));
        holder.distance.setText(Converters.formattedDistanceFromDouble(toilet.getDistance()));

        return row;
    }

    public void swapItems(List<Toilet> toiletList) {
        clear();
        addAll(toiletList);
        notifyDataSetChanged();
    }
}
