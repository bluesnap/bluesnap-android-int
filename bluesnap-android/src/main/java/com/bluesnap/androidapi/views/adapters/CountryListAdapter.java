package com.bluesnap.androidapi.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CountryListObject;

import java.util.ArrayList;

/**
 * Created by roy.biber on 14/06/2016.
 */
public class CountryListAdapter extends BaseAdapter implements Filterable {

    private final Activity context;
    public ArrayList<CountryListObject> countryListObjects;
    CustomFilter filter;
    ArrayList<CountryListObject> filterList;
    private String sharedLanguage;

    public CountryListAdapter(Activity context, ArrayList<CountryListObject> countryListObjects, String sharedLanguage) {

        this.sharedLanguage = sharedLanguage;
        this.context = context;
        this.countryListObjects = countryListObjects;
        this.filterList = countryListObjects;

    }

    @Override
    public int getCount() {
        return countryListObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return countryListObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return countryListObjects.indexOf(getItem(position));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.country_list_view, null);
        }

        TextView txtTitle = convertView.findViewById(R.id.countryTextView);
        ImageView bluesnap_customlist_list_view_icon = convertView.findViewById(R.id.bluesnap_customlist_list_view_icon);
        ImageView countryImage = convertView.findViewById(R.id.countryImage);

        String countryFullName = countryListObjects.get(position).getCountryFullName();
        String countryInitial = countryListObjects.get(position).getCountryInitial();
        int countryDrawable = countryListObjects.get(position).getDrawable();

        /*int countryId = context.getResources().getIdentifier(countryInitial.toLowerCase(), "drawable", context.getPackageName());
        // int The associated resource identifier.  Returns 0 if no such resource was found.  (0 is not a valid resource ID.)
        if (countryId > 0)
            countryImage.setImageDrawable(context.getResources().getDrawable(countryId));
        else
            countryImage.setImageDrawable(context.getResources().getDrawable(R.drawable.unknown));*/

        countryImage.setImageResource(countryDrawable);
        txtTitle.setText(countryFullName);
        if (sharedLanguage.equals(countryListObjects.get(position).getCountryFullName())) {
            bluesnap_customlist_list_view_icon.setVisibility(View.VISIBLE);
        } else {
            bluesnap_customlist_list_view_icon.setVisibility(View.INVISIBLE);
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

                ArrayList<CountryListObject> filters = new ArrayList<>();

                //get specific items
                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getCountryFullName().toUpperCase().contains(constraint)) {
                        CountryListObject p = new CountryListObject(filterList.get(i).getCountryFullName(), filterList.get(i).getCountryInitial(), filterList.get(i).getDrawable());

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

            countryListObjects = (ArrayList<CountryListObject>) results.values;
            notifyDataSetChanged();
        }

    }

}
