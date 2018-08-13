package com.bluesnap.androidapi.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.StateListObject;

import java.util.ArrayList;

/**
 * Created by roy.biber on 14/06/2016.
 */
public class StateListAdapter extends BaseAdapter implements Filterable {

    private final Activity context;
    public ArrayList<StateListObject> stateListObjects;
    CustomFilter filter;
    ArrayList<StateListObject> filterList;
    private String sharedLanguage;

    public StateListAdapter(Activity context, ArrayList<StateListObject> stateListObjects, String sharedLanguage) {

        this.sharedLanguage = sharedLanguage;
        this.context = context;
        this.stateListObjects = stateListObjects;
        this.filterList = stateListObjects;

    }

    @Override
    public int getCount() {
        return stateListObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return stateListObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return stateListObjects.indexOf(getItem(position));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_list_view, null);
        }

        TextView txtTitle = convertView.findViewById(R.id.bluesnap_customlist_list_view_text);
        ImageView bluesnap_customlist_list_view_icon = convertView.findViewById(R.id.bluesnap_customlist_list_view_icon);

        String stateFullName = stateListObjects.get(position).getStateFullName();
        String stateInitial = stateListObjects.get(position).getStateCode();

        txtTitle.setText(stateFullName);
        if (sharedLanguage.equals(stateListObjects.get(position).getStateFullName())) {
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

                ArrayList<StateListObject> filters = new ArrayList<>();

                //get specific items
                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getStateFullName().toUpperCase().contains(constraint)) {
                        StateListObject p = new StateListObject(filterList.get(i).getStateFullName(), filterList.get(i).getStateCode());

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

            stateListObjects = (ArrayList<StateListObject>) results.values;
            notifyDataSetChanged();
        }

    }

}
