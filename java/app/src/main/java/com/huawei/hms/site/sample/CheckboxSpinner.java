/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
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

import com.huawei.hms.site.api.model.LocationType;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class CheckboxSpinner {

    @ColorInt
    public static final int WHITE = 0xD0FFFFFF;

    private final List<LocationTypeItem> locationTypeItemList;

    private final TextView poiTypeTextView;

    public CheckboxSpinner(Switch usePOITypeSwitch, TextView poiTypeTextView, List<LocationType> poiTypeList) {
        locationTypeItemList = new ArrayList<>();
        for (LocationType poiType : poiTypeList) {
            locationTypeItemList.add(new LocationTypeItem(poiType));
        }

        poiTypeTextView.setOnClickListener(v -> {
            if (v.isEnabled()) {
                showDialog(poiTypeTextView.getContext());
            }
        });

        usePOITypeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            poiTypeTextView.setEnabled(isChecked);
            if (!isChecked) {
                poiTypeTextView.setText("");
                for (LocationTypeItem placeField : locationTypeItemList) {
                    placeField.checked = false;
                }
                return;
            }
            showDialog(buttonView.getContext());
        });

        this.poiTypeTextView = poiTypeTextView;
    }

    public void showDialog(Context context) {
        ListView listView = new ListView(context);
        LocationTypeItemArrayAdapter adapter = new LocationTypeItemArrayAdapter(context, locationTypeItemList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);

        AlertDialog alertDialog =
            new AlertDialog.Builder(context).setTitle("POI Types").setPositiveButton("OK", (dialog, which) -> {
                poiTypeTextView.setText(getSelectedString());
            }).setView(listView).show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(R.drawable.button_style);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(WHITE);
    }

    public List<LocationType> getSelectedLocationTypes() {
        List<LocationType> selectedList = new ArrayList<>();
        for (LocationTypeItem placeField : locationTypeItemList) {
            if (placeField.checked) {
                selectedList.add(placeField.item);
            }
        }

        return selectedList;
    }

    public String getSelectedString() {
        StringBuilder builder = new StringBuilder();
        for (LocationType field : getSelectedLocationTypes()) {
            builder.append(field).append("\n");
        }

        return builder.toString();
    }

    private static class LocationTypeItem {
        final LocationType item;

        boolean checked;

        public LocationTypeItem(LocationType item) {
            this.item = item;
        }
    }

    private static class LocationTypeItemArrayAdapter extends ArrayAdapter<LocationTypeItem>
        implements AdapterView.OnItemClickListener {

        public LocationTypeItemArrayAdapter(Context context, List<LocationTypeItem> placeFields) {
            super(context, android.R.layout.simple_list_item_multiple_choice, placeFields);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            LocationTypeItem locationTypeItem = getItem(position);
            updateView(view, locationTypeItem);

            return view;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LocationTypeItem placeField = getItem(position);
            placeField.checked = !placeField.checked;
            updateView(view, placeField);
        }

        private void updateView(View view, LocationTypeItem locationTypeItem) {
            if (view instanceof CheckedTextView) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                checkedTextView.setText(locationTypeItem.item.toString());
                checkedTextView.setChecked(locationTypeItem.checked);
            }
        }
    }

}
