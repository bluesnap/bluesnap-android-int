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
import com.bluesnap.androidapi.models.StateListObject;
import com.bluesnap.androidapi.services.BlueSnapValidator;
import com.bluesnap.androidapi.views.adapters.StateListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StateActivity extends Activity {

    ListView listView;
    String[] state_values_array;
    String[] state_key_array;
    EditText inputSearch;
    String localeState;
    StateListAdapter adapter;
    Map<String, Integer> mapIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluesnap_state_selector);

        final ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
        inputSearch = (EditText) findViewById(R.id.searchView);
        listView = (ListView) findViewById(R.id.state_list_view);

        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null) {
            String countryString = savedInstanceState.getString(getString(R.string.COUNTRY_STRING)).toUpperCase();
            String stateString = savedInstanceState.getString(getString(R.string.STATE_STRING)).toUpperCase();

            // check if US, BlueSnapValidator.STATE_NEEDED_COUNTRIES[0] = US
            if (countryString.equals(BlueSnapValidator.STATE_NEEDED_COUNTRIES[0])) {
                state_values_array = getResources().getStringArray(R.array.state_us_value_array);
                state_key_array = getResources().getStringArray(R.array.state_us_key_array);
            } else if (countryString.equals(BlueSnapValidator.STATE_NEEDED_COUNTRIES[1])) {
                // check if BR, BlueSnapValidator.STATE_NEEDED_COUNTRIES[1] = BR
                state_values_array = getResources().getStringArray(R.array.state_br_value_array);
                state_key_array = getResources().getStringArray(R.array.state_br_key_array);
            } else {
                // check if CA, BlueSnapValidator.STATE_NEEDED_COUNTRIES[2] = CA
                state_values_array = getResources().getStringArray(R.array.state_ca_value_array);
                state_key_array = getResources().getStringArray(R.array.state_ca_key_array);
            }
            if (!"".equals(stateString))
                localeState = state_values_array[Arrays.asList(state_key_array).indexOf(stateString)];
            else
                localeState = "";
        }

        adapter = new StateListAdapter(this, StateListObject.getStateListObject(state_values_array, state_key_array), localeState);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String statePick = adapter.stateListObjects.get(position).getStateCode();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", statePick);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        Arrays.asList(state_values_array);
        getIndexList(state_values_array);
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