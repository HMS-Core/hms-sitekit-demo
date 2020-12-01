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
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.api.model.TextSearchRequest;
import com.huawei.hms.site.api.model.TextSearchResponse;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Keyword Search Example Code
 */
public class TextSearchActivity extends AppCompatActivity {
    private static final String TAG = "TextSearchActivity";

    private EditText queryInput;

    private EditText latInput;

    private EditText lngInput;

    private EditText radiusInput;

    private Spinner poiTypeSpinner;

    private EditText countryInput;

    private EditText languageInput;

    private EditText pageIndexInput;

    private EditText pageSizeInput;

    private TextView resultTextView;

    // Declare a SearchService object.
    private SearchService searchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_search);

        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(this, Utils.getApiKey());

        queryInput = findViewById(R.id.edit_text_text_search_query);
        latInput = findViewById(R.id.edit_text_text_search_location_lat);
        lngInput = findViewById(R.id.edit_text_text_search_location_lng);
        radiusInput = findViewById(R.id.edit_text_text_search_radius);
        poiTypeSpinner = findViewById(R.id.spinner_text_search_poitype);
        poiTypeSpinner.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(LocationType.values())));

        Switch usePOITypeSwitch = findViewById(R.id.switch_text_search_poitype);
        usePOITypeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            poiTypeSpinner.setEnabled(isChecked);
        });
        countryInput = findViewById(R.id.edit_text_text_search_country);
        languageInput = findViewById(R.id.edit_text_text_search_language);
        pageIndexInput = findViewById(R.id.edit_text_text_search_pageindex);
        pageSizeInput = findViewById(R.id.edit_text_text_search_pagesize);
        resultTextView = findViewById(R.id.response_text_search);

        findViewById(R.id.button_text_search).setOnClickListener(view -> {
            execTextSearch();
        });

        poiTypeSpinner.setEnabled(false);
    }

    private void execTextSearch() {
        Log.d(TAG, "execTextSearch: " + this);
        Log.d(TAG, "execTextSearch: " + getApplicationContext());
        Log.d(TAG, "execTextSearch: "
                + (getApplicationContext() == null ? "" : getApplicationContext().getApplicationContext()));
        String query = queryInput.getText().toString();
        if (TextUtils.isEmpty(query)) {
            resultTextView.setText("Error : Query is empty!");
            return;
        }
        // Create a request body.
        TextSearchRequest request = new TextSearchRequest();
        request.setQuery(query);

        Double lat;
        Double lng;
        String latStr = latInput.getText().toString();
        String lngStr = lngInput.getText().toString();
        if (!TextUtils.isEmpty(latStr) || !TextUtils.isEmpty(lngStr)) {
            if ((lat = Utils.parseDouble(latStr)) == null || (lng = Utils.parseDouble(lngStr)) == null) {
                resultTextView.setText("Error : Location is invalid!");
                return;
            }
            request.setLocation(new Coordinate(lat, lng));
        }

        String radius = radiusInput.getText().toString();
        Integer radiusInt;
        if ((radiusInt = Utils.parseInt(radius)) == null || radiusInt <= 0) {
            resultTextView.setText("Error : Radius Must be greater than 0 !");
            return;
        }
        request.setRadius(radiusInt);

        LocationType poiType = poiTypeSpinner.isEnabled() ? (LocationType) poiTypeSpinner.getSelectedItem() : null;
        if (poiType != null) {
            request.setPoiType(poiType);
        }

        String countryCode = countryInput.getText().toString();
        if (!TextUtils.isEmpty(countryCode)) {
            request.setCountryCode(countryCode);
        }

        String language = languageInput.getText().toString();
        if (!TextUtils.isEmpty(language)) {
            request.setLanguage(language);
        }

        String pageIndex = pageIndexInput.getText().toString();
        Integer pageIndexInt;
        if ((pageIndexInt = Utils.parseInt(pageIndex)) == null || pageIndexInt < 1 || pageIndexInt > 60) {
            resultTextView.setText("Error : PageIndex Must be between 1 and 60!");
            return;
        }
        request.setPageIndex(pageIndexInt);

        String pageSize = pageSizeInput.getText().toString();
        Integer pageSizeInt;
        if ((pageSizeInt = Utils.parseInt(pageSize)) == null || pageSizeInt < 1 || pageSizeInt > 20) {
            resultTextView.setText("Error : PageSize Must be between 1 and 20!");
            return;
        }
        request.setPageSize(pageSizeInt);
        request.setChildren(((Switch) findViewById(R.id.switch_text_search_children)).isChecked());
        // Create a search result listener.
        SearchResultListener<TextSearchResponse> resultListener = new SearchResultListener<TextSearchResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(TextSearchResponse results) {
                List<Site> siteList;
                if (results == null || results.getTotalCount() <= 0 || (siteList = results.getSites()) == null
                        || siteList.size() <= 0) {
                    resultTextView.setText("Result is Empty!");
                    return;
                }

                StringBuilder response = new StringBuilder("\n");
                response.append("success\n");
                int count = 1;
                AddressDetail addressDetail;
                Coordinate location;
                Poi poi;
                CoordinateBounds viewport;
                for (Site site : siteList) {
                    addressDetail = site.getAddress();
                    location = site.getLocation();
                    poi = site.getPoi();
                    viewport = site.getViewport();
                    response.append(String.format(
                            "[%s] siteId: '%s', name: %s, formatAddress: %s, country: %s, countryCode: %s, location: %s, poiTypes: %s, viewport: %s, ",
                            "" + (count++), site.getSiteId(), site.getName(), site.getFormatAddress(),
                            (addressDetail == null ? "" : addressDetail.getCountry()),
                            (addressDetail == null ? "" : addressDetail.getCountryCode()),
                            (location == null ? "" : (location.getLat() + "," + location.getLng())),
                            (poi == null ? "" : Arrays.toString(poi.getPoiTypes())),
                            (viewport == null ? "" : "northeast{lat=" + viewport.getNortheast().getLat() + ", lng=" + viewport.getNortheast().getLng() + "},"
                                    + "southwest{lat=" + viewport.getSouthwest().getLat() + ", lng=" + viewport.getSouthwest().getLng() + "}")));
                    if ((poi != null)){
                        Gson g = new Gson();
                        String jsonString = g.toJson(poi.getChildrenNodes());
                        response.append(String.format("childrenNode: %s \n\n", jsonString));
                    }
                }
                resultTextView.setText(response.toString());
                Log.d(TAG, "onTextSearchResult: " + response.toString());
            }
            // Return the result code and description upon a search exception.
            @Override
            public void onSearchError(SearchStatus status) {
                resultTextView.setText("Error : " + status.getErrorCode() + " " + status.getErrorMessage());
            }
        };
        // Call the place search API.
        searchService.textSearch(request, resultListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchService = null;
    }
}
