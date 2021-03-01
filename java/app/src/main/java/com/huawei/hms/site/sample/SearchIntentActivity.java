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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.huawei.hms.site.api.model.AddressDetail;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.CoordinateBounds;
import com.huawei.hms.site.api.model.LocationType;
import com.huawei.hms.site.api.model.Poi;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.widget.SearchFilter;
import com.huawei.hms.site.widget.SearchIntent;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SearchIntentActivity extends AppCompatActivity {
    private TextView textView;

    private SearchIntent searchIntent;

    private CheckboxSpinner locationTypeSpinner;

    private Switch childrenSwitch;

    private Switch strictBoundsSwitch;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_intent);

        searchIntent = new SearchIntent();
        textView = findViewById(R.id.demo_fragment_textView_result);
        childrenSwitch = findViewById(R.id.childrenswitch);
        strictBoundsSwitch = findViewById(R.id.switch_strict_bounds);

        List<LocationType> poiTypes = new ArrayList<>();
        poiTypes.add(LocationType.GEOCODE);
        poiTypes.add(LocationType.ADDRESS);
        poiTypes.add(LocationType.ESTABLISHMENT);
        poiTypes.add(LocationType.REGIONS);
        poiTypes.add(LocationType.CITIES);

        TextView poiTypesInput = findViewById(R.id.query_suggestion_poi_type_input);
        poiTypesInput.setEnabled(false);

        locationTypeSpinner =
            new CheckboxSpinner(findViewById(R.id.switch_query_suggestion_poitype), poiTypesInput, poiTypes);
    }

    public void openSearchActivity(View view) {
        String query = ((TextView) findViewById(R.id.query_suggestion_query_input)).getText().toString();
        String radius = ((TextView) findViewById(R.id.query_suggestion_radius_input)).getText().toString();
        String language = ((TextView) findViewById(R.id.query_suggestion_language_input)).getText().toString();
        String locationLatitude =
            ((TextView) findViewById(R.id.query_suggestion_location_lat_input)).getText().toString();
        String locationLongitude =
            ((TextView) findViewById(R.id.query_suggestion_location_lng_input)).getText().toString();
        String countryCode = ((TextView) findViewById(R.id.query_suggestion_country_code_input)).getText().toString();
        String northeastLatText =
            ((TextView) findViewById(R.id.query_suggestion_bounds_northeast_lat_input)).getText().toString();
        String northeastLngText =
            ((TextView) findViewById(R.id.query_suggestion_bounds_northeast_lng_input)).getText().toString();
        String southwestLatText =
            ((TextView) findViewById(R.id.query_suggestion_bounds_southwest_lat_input)).getText().toString();
        String southwestLngText =
            ((TextView) findViewById(R.id.query_suggestion_bounds_southwest_lng_input)).getText().toString();

        SearchFilter searchFilter = new SearchFilter();

        if (!TextUtils.isEmpty(language)) {
            searchFilter.setLanguage(language);
        }
        if (!TextUtils.isEmpty(query)) {
            searchFilter.setQuery(query);
        }
        if (!TextUtils.isEmpty(countryCode)) {
            searchFilter.setCountryCode(countryCode);
        }
        Integer radiusValue;
        if ((radiusValue = Utils.parseInt(radius)) != null) {
            searchFilter.setRadius(radiusValue);
        }

        Double northeastLat = Utils.parseDouble(northeastLatText);
        Double northeastLng = Utils.parseDouble(northeastLngText);
        Double southwestLat = Utils.parseDouble(southwestLatText);
        Double southwestLng = Utils.parseDouble(southwestLngText);

        if (northeastLat != null && northeastLng != null && southwestLat != null && southwestLng != null) {
            Coordinate northeast = new Coordinate(northeastLat, northeastLng);
            Coordinate southwest = new Coordinate(southwestLat, southwestLng);
            CoordinateBounds bounds = new CoordinateBounds(northeast, southwest);
            searchFilter.setBounds(bounds);
        }

        Double lat = Utils.parseDouble(locationLatitude);
        Double lng = Utils.parseDouble(locationLongitude);
        if (lat != null && lng != null) {
            searchFilter.setLocation(new Coordinate(lat, lng));
        }

        List<LocationType> locationTypes = getLocationTypes();
        searchFilter.setPoiType(locationTypes);
        searchFilter.setChildren(childrenSwitch.isChecked());
        searchFilter.setStrictBounds(strictBoundsSwitch.isChecked());

        String apiKey = Utils.getApiKey();
        searchIntent.setApiKey(apiKey);

        searchIntent.setSearchFilter(searchFilter);

        Intent intent = searchIntent.getIntent(this);
        startActivityForResult(intent, SearchIntent.SEARCH_REQUEST_CODE);
    }

    public void openSearchFragment(View view) {
        String query = ((TextView) findViewById(R.id.query_suggestion_query_input)).getText().toString();
        String radius = ((TextView) findViewById(R.id.query_suggestion_radius_input)).getText().toString();
        String language = ((TextView) findViewById(R.id.query_suggestion_language_input)).getText().toString();
        String locationLatitude =
            ((TextView) findViewById(R.id.query_suggestion_location_lat_input)).getText().toString();
        String locationLongitude =
            ((TextView) findViewById(R.id.query_suggestion_location_lng_input)).getText().toString();
        String countryCode = ((TextView) findViewById(R.id.query_suggestion_country_code_input)).getText().toString();
        String northeastLatText =
            ((TextView) findViewById(R.id.query_suggestion_bounds_northeast_lat_input)).getText().toString();
        String northeastLngText =
            ((TextView) findViewById(R.id.query_suggestion_bounds_northeast_lng_input)).getText().toString();
        String southwestLatText =
            ((TextView) findViewById(R.id.query_suggestion_bounds_southwest_lat_input)).getText().toString();
        String southwestLngText =
            ((TextView) findViewById(R.id.query_suggestion_bounds_southwest_lng_input)).getText().toString();

        Intent intent = new Intent(this, SearchFragmentActivity.class);

        if (!TextUtils.isEmpty(language)) {
            intent.putExtra("language", language);
        }
        if (!TextUtils.isEmpty(query)) {
            intent.putExtra("query", query);
        }
        if (!TextUtils.isEmpty(countryCode)) {
            intent.putExtra("countryCode", countryCode);
        }

        if (!TextUtils.isEmpty(radius)) {
            intent.putExtra("radius", radius);
        }

        Double northeastLat = Utils.parseDouble(northeastLatText);
        Double northeastLng = Utils.parseDouble(northeastLngText);
        Double southwestLat = Utils.parseDouble(southwestLatText);
        Double southwestLng = Utils.parseDouble(southwestLngText);

        if (northeastLat != null && northeastLng != null && southwestLat != null && southwestLng != null) {
            Coordinate northeast = new Coordinate(northeastLat, northeastLng);
            Coordinate southwest = new Coordinate(southwestLat, southwestLng);
            CoordinateBounds bounds = new CoordinateBounds(northeast, southwest);
            intent.putExtra("bounds", bounds);
        }

        Double lat = Utils.parseDouble(locationLatitude);
        Double lng = Utils.parseDouble(locationLongitude);
        if (lat != null && lng != null) {
            intent.putExtra("location", new Coordinate(lat, lng));
        }

        List<LocationType> locationTypes = getLocationTypes();
        intent.putExtra("poiTypes", (Serializable) locationTypes);
        intent.putExtra("children", childrenSwitch.isChecked());
        intent.putExtra("strictBounds", strictBoundsSwitch.isChecked());

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (SearchIntent.SEARCH_REQUEST_CODE == requestCode) {
            if (SearchIntent.isSuccess(resultCode)) {
                Site site = searchIntent.getSiteFromIntent(data);

                StringBuilder response = new StringBuilder("\n");
                AddressDetail addressDetail = site.getAddress();
                Coordinate location = site.getLocation();
                Poi poi = site.getPoi();
                CoordinateBounds viewport = site.getViewport();
                response.append(String.format(
                    "siteId: '%s', Name: %s, FormatAddress: %s, Country: %s, CountryCode: %s, Location: %s, PoiTypes: %s, Viewport: %s, streetNumber: %s, postalCode: %s ,tertiaryAdminArea: %s \n\n",
                    site.getSiteId(), site.getName(), site.getFormatAddress(),
                    (addressDetail == null ? "" : addressDetail.getCountry()),
                    (addressDetail == null ? "" : addressDetail.getCountryCode()),
                    (location == null ? "" : (location.getLat() + "," + location.getLng())),
                    (poi == null ? "" : Arrays.toString(poi.getPoiTypes())),
                    (viewport == null ? "" : viewport.getNortheast() + "," + viewport.getSouthwest()),
                    (addressDetail == null ? "" : addressDetail.getStreetNumber()),
                    (addressDetail == null ? "" : addressDetail.getPostalCode()),
                    (addressDetail == null ? "" : addressDetail.getTertiaryAdminArea())));
                if ((poi != null)) {
                    Gson g = new Gson();
                    String jsonString = g.toJson(poi.getChildrenNodes());
                    if (!jsonString.equals("null")) {
                        response.append(String.format("childrenNode: %s,", jsonString));
                    }
                }
                textView.setText(response.toString());
            } else {
                SearchStatus searchStatus = searchIntent.getStatusFromIntent(data);
                textView.setText(" Error code: " + searchStatus.getErrorCode() + "\n Error message: "
                    + searchStatus.getErrorMessage());
            }
        }
    }

    private List<LocationType> getLocationTypes() {
        if (((Switch) findViewById(R.id.switch_query_suggestion_poitype)).isChecked()) {
            return locationTypeSpinner.getSelectedLocationTypes();
        } else {
            return null;
        }
    }
}
