package com.bluesnap.androidapi.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CustomListObject;

import java.util.ArrayList;

/**
 * Created by roy.biber on 14/06/2016.
 */
public class CountryListAdapter extends BaseAdapter implements Filterable {

    private final Activity context;
    public ArrayList<CustomListObject> countryFullNameListObjects;
    public ArrayList<CustomListObject> countryInitialListObjects;
    CustomFilter filter;
    ArrayList<CustomListObject> filterList;
    private String sharedLanguage;

    public CountryListAdapter(Activity context, ArrayList<CustomListObject> countryFullNameListObjects, ArrayList<CustomListObject> countryInitialListObjects, String sharedLanguage) {

        this.sharedLanguage = sharedLanguage;
        this.context = context;
        this.countryFullNameListObjects = countryFullNameListObjects;
        this.countryInitialListObjects = countryInitialListObjects;
        this.filterList = countryFullNameListObjects;

    }

    @Override
    public int getCount() {
        return countryFullNameListObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return countryFullNameListObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return countryFullNameListObjects.indexOf(getItem(position));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.country_list_view, null);
        }

        TextView txtTitle = (TextView) convertView.findViewById(R.id.countryTextView);
        ImageView bluensap_customlist_list_view_icon = (ImageView) convertView.findViewById(R.id.bluensap_customlist_list_view_icon);
        ImageView countryImage = (ImageView) convertView.findViewById(R.id.countryImage);

        String countryFullName = countryFullNameListObjects.get(position).getName();
        String countryInitial = countryInitialListObjects.get(position).getName();

        int countryId = context.getResources().getIdentifier(countryInitial.toLowerCase(), "drawable", context.getPackageName());
        // int The associated resource identifier.  Returns 0 if no such resource was found.  (0 is not a valid resource ID.)
        if (countryId > 0)
            countryImage.setImageDrawable(context.getResources().getDrawable(countryId));

        txtTitle.setText(countryFullName);
        if (sharedLanguage.equals(countryFullNameListObjects.get(position).getName())) {
            bluensap_customlist_list_view_icon.setVisibility(View.VISIBLE);
        } else {
            bluensap_customlist_list_view_icon.setVisibility(View.INVISIBLE);
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

                ArrayList<CustomListObject> filters = new ArrayList<CustomListObject>();

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

            countryFullNameListObjects = (ArrayList<CustomListObject>) results.values;
            notifyDataSetChanged();
        }

    }

}
