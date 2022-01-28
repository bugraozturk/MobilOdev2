package com.bugraozturk.exchangeapp;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    TextView convertFromDropdownTextView, convertToDropdownTextView, conversionRateText;
    EditText amountToConvert;
    ArrayList<String> arrayList;
    Dialog fromDialog;
    Dialog toDialog;
    Button convertButton;
    String convertFromValue, convertToValue, conversionValue;
    String[] country = {"TRY", "USD", "EUR"}; //TODO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        convertFromDropdownTextView = findViewById(R.id.convert_from_dropdown_menu);
        convertToDropdownTextView = findViewById(R.id.convert_to_dropdown_menu);
        convertButton = findViewById(R.id.conversionButton);
        conversionRateText = findViewById(R.id.conversionRateText);
        amountToConvert = findViewById(R.id.amount_to_convert_value_edittext);

        arrayList = new ArrayList<>();
        Collections.addAll(arrayList, country);

        convertFromDropdownTextView.setOnClickListener(v -> {
            fromDialog = new Dialog(MainActivity.this);
            fromDialog.setContentView(R.layout.from_spinner);
            fromDialog.getWindow().setLayout(650,800);
            fromDialog.show();

            EditText editText = fromDialog.findViewById(R.id.edit_text);
            ListView listView = fromDialog.findViewById(R.id.list_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,arrayList);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            listView.setOnItemClickListener((parent, view, position, id) -> {
                convertFromDropdownTextView.setText(adapter.getItem(position));
                fromDialog.dismiss();
                convertFromValue = adapter.getItem(position);

            });
        });

        convertToDropdownTextView.setOnClickListener(v -> {
            toDialog = new Dialog(MainActivity.this);
            toDialog.setContentView(R.layout.to_spinner);
            toDialog.getWindow().setLayout(650,800);
            toDialog.show();

            EditText editText = toDialog.findViewById(R.id.edit_text);
            ListView listView = toDialog.findViewById(R.id.list_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,arrayList);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            listView.setOnItemClickListener((parent, view, position, id) -> {
                convertToDropdownTextView.setText(adapter.getItem(position));
                toDialog.dismiss();
                convertToValue = adapter.getItem(position);


            });
        });

        convertButton.setOnClickListener(v -> {
            try {
                double amountToConvert = Double.parseDouble(MainActivity.this.amountToConvert.getText().toString());
                getConversionRate(convertFromValue,convertToValue);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getConversionRate(String convertFrom, String convertTo) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://free.currconv.com/api/v7/convert?q=" + convertFrom + "_" + convertTo + "&compact=ultra&apiKey=2749029b56824550e123";
        double amount = Double.valueOf(amountToConvert.getText().toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    Double conversionRateValue = round(((double) jsonObject.get(convertFrom + "_" + convertTo)), 2);
                    conversionValue = "" + round((conversionRateValue * amount), 2);
                    conversionRateText.setText(conversionValue);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value); bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();

    }
}