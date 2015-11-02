package com.handipressante.handipressante; /**
 * Created by juliettegonzalez on 23/10/2015.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handipressante.handipressante.R;

public class MenuList extends ArrayAdapter<String>{

        private final Activity context;
        private final String[] menu_item;
        private final Integer[] imageId;
        public MenuList(Activity context,
                          String[] menu_item, Integer[] imageId) {
            super(context, R.layout.drawer_list_item, menu_item);
            this.context = context;
            this.menu_item = menu_item;
            this.imageId = imageId;

        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView= inflater.inflate(R.layout.drawer_list_item, null, true);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
            //txtTitle.setText(menu_item[position]);

            imageView.setImageResource(imageId[position]);

            return rowView;
        }
}