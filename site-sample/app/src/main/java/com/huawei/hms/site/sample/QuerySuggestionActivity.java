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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.AddressDetail;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.CoordinateBounds;
import com.huawei.hms.site.api.model.LocationType;
import com.huawei.hms.site.api.model.Poi;
import com.huawei.hms.site.api.model.QuerySuggestionRequest;
import com.huawei.hms.site.api.model.QuerySuggestionResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Place Search Suggestion Example Code
 */
public class QuerySuggestionActivity extends AppCompatActivity implements View.OnClickListener {
    // Declare a SearchService object.
    private SearchService searchService;

    private CheckboxSpinner locationTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_suggestion);

        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(this, Utils.getApiKey());

        findViewById(R.id.search_query_suggestion_button).setOnClickListener(this);

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

    // Create a search result listener.
    SearchResultListener searchResultListener = new SearchResultListener<QuerySuggestionResponse>() {
        // Return search results upon a successful search.
        @Override
        public void onSearchResult(QuerySuggestionResponse results) {
            ((TextView) findViewById(R.id.query_suggestion_result_status)).setText("success");
            TextView resultView = findViewById(R.id.query_suggestion_result_text);
            StringBuilder stringBuilder = new StringBuilder();
            if (results != null) {
                List<Site> sites = results.getSites();
                if (sites != null && sites.size() > 0) {
                    int count = 1;
                    for (Site site : sites) {
                        AddressDetail addressDetail = site.getAddress();
                        Coordinate location = site.getLocation();
                        Poi poi = site.getPoi();
                        CoordinateBounds viewport = site.getViewport();
                        stringBuilder.append(String.format(
                            "[%s] siteId: '%s', name: %s, formatAddress: %s, country: %s, countryCode: %s, location: %s, distance: %s, poiTypes: %s, viewport: %s, ",
                            "" + (count++), site.getSiteId(), site.getName(), site.getFormatAddress(),
                            (addressDetail == null ? "" : addressDetail.getCountry()),
                            (addressDetail == null ? "" : addressDetail.getCountryCode()),
                            (location == null ? "" : (location.getLat() + "," + location.getLng())), site.getDistance(),
                            (poi == null ? "" : Arrays.toString(poi.getPoiTypes())),
                            (viewport == null ? "" : "northeast{lat=" + viewport.getNortheast().getLat() + ", lng=" + viewport.getNortheast().getLng() + "},"
                                    + "southwest{lat=" + viewport.getSouthwest().getLat() + ", lng=" + viewport.getSouthwest().getLng() + "}")));
                        if ((poi != null)) {
                            Gson g = new Gson();
                            String jsonString = g.toJson(poi.getChildrenNodes());
                            stringBuilder.append(String.format("childrenNode: %s \n\n", jsonString));
                        }
                    }
                } else {
                    stringBuilder.append("0 results");
                }
            }
            resultView.setText(stringBuilder);
        }

        // Return the result code and description upon a search exception.
        @Override
        public void onSearchError(SearchStatus status) {
            ((TextView) findViewById(R.id.query_suggestion_result_text)).setText("");
            ((TextView) findViewById(R.id.query_suggestion_result_status))
                .setText("failed " + status.getErrorCode() + " " + status.getErrorMessage());
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.search_query_suggestion_button) {
            querySuggestion();
        }
    }

    private void querySuggestion() {
        // Create a request body.
        QuerySuggestionRequest request = new QuerySuggestionRequest();
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

        if (!TextUtils.isEmpty(language)) {
            request.setLanguage(language);
        }
        if (!TextUtils.isEmpty(query)) {
            request.setQuery(query);
        }
        if (!TextUtils.isEmpty(countryCode)) {
            request.setCountryCode(countryCode);
        }
        Integer radiusValue;
        if ((radiusValue = Utils.parseInt(radius)) != null) {
            request.setRadius(radiusValue);
        }

        Double northeastLat = Utils.parseDouble(northeastLatText);
        Double northeastLng = Utils.parseDouble(northeastLngText);
        Double southwestLat = Utils.parseDouble(southwestLatText);
        Double southwestLng = Utils.parseDouble(southwestLngText);

        if (northeastLat != null && northeastLng != null && southwestLat != null && southwestLng != null) {
            Coordinate northeast = new Coordinate(northeastLat, northeastLng);
            Coordinate southwest = new Coordinate(southwestLat, southwestLng);
            CoordinateBounds bounds = new CoordinateBounds(northeast, southwest);
            request.setBounds(bounds);
        }

        Double lat = Utils.parseDouble(locationLatitude);
        Double lng = Utils.parseDouble(locationLongitude);
        if (lat != null && lng != null) {
            request.setLocation(new Coordinate(lat, lng));
        }

        List<LocationType> locationTypes = getLocationTypes();
        request.setPoiTypes(locationTypes);
        request.setChildren(((Switch) findViewById(R.id.switch_query_suggestion_children)).isChecked());
        request.setStrictBounds(((Switch) findViewById(R.id.switch_query_suggestion_strict_bounds)).isChecked());

        // Call the place search suggestion API.
        searchService.querySuggestion(request, searchResultListener);
    }

    private List<LocationType> getLocationTypes() {
        if (((Switch) findViewById(R.id.switch_query_suggestion_poitype)).isChecked()) {
            return locationTypeSpinner.getSelectedLocationTypes();
        } else {
            return new ArrayList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchService = null;
    }
}
