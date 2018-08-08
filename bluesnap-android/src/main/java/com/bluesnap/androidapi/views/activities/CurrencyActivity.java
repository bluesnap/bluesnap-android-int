package com.bluesnap.androidapi.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import com.bluesnap.androidapi.R;
import com.bluesnap.androidapi.models.CustomListObject;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.views.adapters.CustomListAdapter;

import java.util.*;

public class CurrencyActivity extends Activity {
    private static final String TAG = CurrencyActivity.class.getSimpleName();

    ListView listView;
    String[] currency_value_array;
    String[] currency_key_array;
    EditText inputSearch;
    String localeCurrencyFull;
    CustomListAdapter adapter;
    Map<String, Integer> mapIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluesnap_currency_selector);

        final ImageButton backButton = findViewById(R.id.back_button);
        inputSearch = findViewById(R.id.searchView);
        listView = findViewById(R.id.currency_list_view);
        currency_value_array = getResources().getStringArray(R.array.currency_value_array);
        currency_key_array = getResources().getStringArray(R.array.currency_key_array);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            localeCurrencyFull = currency_value_array[Arrays.asList(currency_key_array).indexOf(extras.getString(getString(R.string.CURRENCY_STRING)))];
        }

        adapter = new CustomListAdapter(this, CustomListObject.getCustomListObject(currency_value_array), localeCurrencyFull);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currencyPick = currency_key_array[Arrays.asList(currency_value_array).indexOf(adapter.customListObjects.get(position).getName())];
                BlueSnapService.getInstance().onCurrencyChange(currencyPick, CurrencyActivity.this);
                finish();
            }
        });

        getIndexList(currency_value_array);
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
