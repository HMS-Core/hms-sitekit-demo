/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.hms.site.sample;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class CheckboxCountriesSpinner {

    @ColorInt
    public static final int WHITE = 0xD0FFFFFF;

    private final List<CountryListItem> countryListItemList;

    private final TextView countryListTextView;

    public CheckboxCountriesSpinner(Switch useCountryListSwitch, TextView countryListTextView, List<String> countryList) {
        countryListItemList = new ArrayList<>();
        for (String country : countryList) {
            countryListItemList.add(new CountryListItem(country));
        }

        countryListTextView.setOnClickListener(v -> {
            if (v.isEnabled()) {
                showDialog(countryListTextView.getContext());
            }
        });

        useCountryListSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            countryListTextView.setEnabled(isChecked);
            if (!isChecked) {
                countryListTextView.setText("");
                for (CountryListItem placeField : countryListItemList) {
                    placeField.checked = false;
                }
                return;
            }
            showDialog(buttonView.getContext());
        });

        this.countryListTextView = countryListTextView;
    }

    public void showDialog(Context context) {
        ListView listView = new ListView(context);
        LocationTypeItemArrayAdapter adapter = new LocationTypeItemArrayAdapter(context, countryListItemList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);

        AlertDialog alertDialog =
            new AlertDialog.Builder(context).setTitle("Country List").setPositiveButton("OK", (dialog, which) -> {
                countryListTextView.setText(getSelectedString());
            }).setView(listView).show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(R.drawable.button_style);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(WHITE);
    }

    public List<String> getSelectedCountryList() {
        List<String> selectedList = new ArrayList<>();
        for (CountryListItem countryListItem : countryListItemList) {
            if (countryListItem.checked) {
                selectedList.add(countryListItem.item);
            }
        }

        return selectedList;
    }

    public String getSelectedString() {
        StringBuilder builder = new StringBuilder();
        for (String field : getSelectedCountryList()) {
            builder.append(field).append("\n");
        }

        return builder.toString();
    }

    private static class CountryListItem {
        final String item;

        boolean checked;

        public CountryListItem(String item) {
            this.item = item;
        }
    }

    private static class LocationTypeItemArrayAdapter extends ArrayAdapter<CountryListItem>
        implements AdapterView.OnItemClickListener {

        public LocationTypeItemArrayAdapter(Context context, List<CountryListItem> placeFields) {
            super(context, android.R.layout.simple_list_item_multiple_choice, placeFields);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            CountryListItem countryListItem = getItem(position);
            updateView(view, countryListItem);

            return view;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CountryListItem placeField = getItem(position);
            placeField.checked = !placeField.checked;
            updateView(view, placeField);
        }

        private void updateView(View view, CountryListItem countryListItem) {
            if (view instanceof CheckedTextView) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                checkedTextView.setText(countryListItem.item.toString());
                checkedTextView.setChecked(countryListItem.checked);
            }
        }
    }

}
