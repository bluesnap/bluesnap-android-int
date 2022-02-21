package com.bluesnap.androidapi.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CountryListObject;
import com.bluesnap.androidapi.views.adapters.CountryListAdapter;

import java.util.*;

public class CountryActivity extends Activity {

    ListView listView;
    String[] country_values_array;
    String[] country_key_array;
    EditText inputSearch;
    String localeCountry;
    CountryListAdapter adapter;
    Map<String, Integer> mapIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.bluesnap_country_selector);

        final ImageButton backButton = findViewById(R.id.back_button);
        inputSearch = findViewById(R.id.searchView);
        listView = findViewById(R.id.country_list_view);
        //country_map = getHashMapResource(this.getApplicationContext(), R.xml.countries_hash_map);
        country_values_array = getResources().getStringArray(R.array.country_value_array);
        country_key_array = getResources().getStringArray(R.array.country_key_array);

        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null) {
            String country = savedInstanceState.getString(getString(R.string.COUNTRY_STRING));
            country = country == null ? "" : country.toUpperCase();
            localeCountry = country_values_array[Arrays.asList(country_key_array).indexOf(country)];
        }

        int[] country_drawable_array = new int[country_key_array.length];
        for (int i = 0; i < country_key_array.length; i++) {
            int countryId = getResources().getIdentifier(country_key_array[i].toLowerCase(), "drawable", getPackageName());
            // int The associated resource identifier.  Returns 0 if no such resource was found.  (0 is not a valid resource ID.)
            if (countryId > 0)
                country_drawable_array[i] = countryId;
            else
                country_drawable_array[i] = R.drawable.unknown;
        }
        adapter = new CountryListAdapter(this, CountryListObject.getCountryListObject(country_values_array, country_key_array, country_drawable_array), localeCountry);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String countryPick = adapter.countryListObjects.get(position).getCountryInitial();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", countryPick);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

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
        mapIndex = new LinkedHashMap<>();
        for (int i = 0; i < lists.length; i++) {
            String list = lists[i];
            String index = list.substring(0, 1);

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private void displayIndex() {
        LinearLayout indexLayout = findViewById(R.id.side_index);

        TextView textView;
        List<String> indexList = new ArrayList<>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView selectedIndex = (TextView) view;
                    listView.setSelection(mapIndex.get(selectedIndex.getText().toString()));
                }
            });
            indexLayout.addView(textView);
        }
    }
}
