package com.bluesnap.androidapi.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.views.CustomListObject;
import com.bluesnap.androidapi.views.adapters.CustomListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CountryActivity extends Activity {

    ListView listView;
    String[] country_values_array;
    String[] country_key_array;
    EditText inputSearch;
    String localeCountry;
    CustomListAdapter adapter;
    Map<String, Integer> mapIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluesnap_country_selector);

        final ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        inputSearch = (EditText) findViewById(R.id.searchView);
        listView = (ListView) findViewById(R.id.country_list_view);
        //country_map = getHashMapResource(this.getApplicationContext(), R.xml.countries_hash_map);
        country_values_array = getResources().getStringArray(R.array.country_value_array);
        country_key_array = getResources().getStringArray(R.array.country_key_array);

        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null) {
            localeCountry = country_values_array[Arrays.asList(country_key_array).indexOf(savedInstanceState.getString(getString(R.string.COUNTRY_STRING)).toUpperCase())];
        }

        adapter = new CustomListAdapter(this, CustomListObject.getCustomListObject(country_values_array), localeCountry);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String countryPick = country_key_array[Arrays.asList(country_values_array).indexOf(adapter.customListObjects.get(position).getName())];
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", countryPick);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        Arrays.asList(country_values_array);
        getIndexList(country_values_array);
        displayIndex();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                adapter.getFilter().filter(cs);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getIndexList(String[] lists) {
        mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < lists.length; i++) {
            String list = lists[i];
            String index = list.substring(0, 1);

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private void displayIndex() {
        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.side_index);

        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView selectedIndex = (TextView) view;
                    listView.setSelection(mapIndex.get(selectedIndex.getText()));
                }
            });
            indexLayout.addView(textView);
        }
    }
}
