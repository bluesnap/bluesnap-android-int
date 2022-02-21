package com.bluesnap.androidapi.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.StateListObject;
import com.bluesnap.androidapi.services.BlueSnapValidator;
import com.bluesnap.androidapi.views.adapters.StateListAdapter;

import java.util.*;

public class StateActivity extends Activity {
    private static final String TAG = StateActivity.class.getSimpleName();

    ListView listView;
    String[] state_values_array;
    String[] state_key_array;
    EditText inputSearch;
    String localeState;
    StateListAdapter adapter;
    Map<String, Integer> mapIndex;
    private final TextWatcher searchLineTextWatcher = new SearchLineTextWatcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.bluesnap_state_selector);

        final ImageButton backButton = findViewById(R.id.back_button);
        inputSearch = findViewById(R.id.searchView);
        listView = findViewById(R.id.state_list_view);

        savedInstanceState = getIntent().getExtras();
        if (savedInstanceState != null) {
            String countryString = savedInstanceState.getString(getString(R.string.COUNTRY_STRING));
            countryString = countryString == null ? "" : countryString.toUpperCase();
            String stateString = savedInstanceState.getString(getString(R.string.STATE_STRING));
            stateString = stateString == null ? "" : stateString.toUpperCase();

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

            Integer index = !"".equals(stateString) ? Arrays.asList(state_key_array).indexOf(stateString) : -1;
            localeState = index != -1 ? state_values_array[index] : "";
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

        getIndexList(state_values_array);
        displayIndex();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        inputSearch.addTextChangedListener(searchLineTextWatcher);
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

    private class SearchLineTextWatcher implements TextWatcher {
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
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy() was called");
        super.onDestroy();
        inputSearch.removeTextChangedListener(searchLineTextWatcher);
    }
}
