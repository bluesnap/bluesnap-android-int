package com.bluesnap.androidapi.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CustomListObject;

import java.util.ArrayList;

/**
 * Created by roy.biber on 14/06/2016.
 */
public class CustomListAdapter extends BaseAdapter implements Filterable {

    private final Activity context;
    public ArrayList<CustomListObject> customListObjects;
    CustomFilter filter;
    ArrayList<CustomListObject> filterList;
    private String sharedLanguage;

    public CustomListAdapter(Activity context, ArrayList<CustomListObject> customListObjects, String sharedLanguage) {

        this.sharedLanguage = sharedLanguage;
        this.context = context;
        this.customListObjects = customListObjects;
        this.filterList = customListObjects;

    }

    @Override
    public int getCount() {
        return customListObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return customListObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return customListObjects.indexOf(getItem(position));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_list_view, null);
        }

        TextView txtTitle = convertView.findViewById(R.id.bluesnap_customlist_list_view_text);
        ImageView imageView = convertView.findViewById(R.id.bluesnap_customlist_list_view_icon);

        txtTitle.setText(customListObjects.get(position).getName());
        if (sharedLanguage.equals(customListObjects.get(position).getName())) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }

        return convertView;

    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter();
        }

        return filter;
    }

    class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                //CONSTARINT TO UPPER
                constraint = constraint.toString().toUpperCase();

                ArrayList<CustomListObject> filters = new ArrayList<>();

                //get specific items
                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getName().toUpperCase().contains(constraint)) {
                        CustomListObject p = new CustomListObject(filterList.get(i).getName());

                        filters.add(p);
                    }
                }

                results.count = filters.size();
                results.values = filters;

            } else {
                results.count = filterList.size();
                results.values = filterList;

            }

            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            customListObjects = (ArrayList<CustomListObject>) results.values;
            notifyDataSetChanged();
        }

    }

}
