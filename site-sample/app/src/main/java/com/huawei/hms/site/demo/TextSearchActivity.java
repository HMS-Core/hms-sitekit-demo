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

package com.huawei.hms.site.demo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

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

public class TextSearchActivity extends AppCompatActivity {
    private static final String TAG = "TextSearchActivity";

    EditText queryInput;

    EditText latInput;

    EditText lngInput;

    EditText radiusInput;

    Spinner poiTypeSpinner;

    EditText countryInput;

    EditText languageInput;

    EditText politicalViewInput;

    EditText pageIndexInput;

    EditText pageSizeInput;

    TextView resultTextView;

    private SearchService searchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_search);

        searchService = SearchServiceFactory.create(this);

        queryInput = findViewById(R.id.edit_text_text_search_query);
        latInput = findViewById(R.id.edit_text_text_search_location_lat);
        lngInput = findViewById(R.id.edit_text_text_search_location_lng);
        radiusInput = findViewById(R.id.edit_text_text_search_radius);
        poiTypeSpinner = findViewById(R.id.spinner_text_search_poitype);
        poiTypeSpinner.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(LocationType.values())));

        Switch usePOITypeSwitch = findViewById(R.id.switch_text_search_poitype);
        usePOITypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                poiTypeSpinner.setEnabled(isChecked);
            }
        });
        countryInput = findViewById(R.id.edit_text_text_search_country);
        languageInput = findViewById(R.id.edit_text_text_search_language);
        politicalViewInput = findViewById(R.id.edit_text_text_search_politicalview);
        pageIndexInput = findViewById(R.id.edit_text_text_search_pageindex);
        pageSizeInput = findViewById(R.id.edit_text_text_search_pagesize);
        resultTextView = findViewById(R.id.response_text_search);

        findViewById(R.id.button_text_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextSearchActivity.this.execTextSearch();
            }
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

        String politicalView = politicalViewInput.getText().toString();
        if (!TextUtils.isEmpty(politicalView)) {
            request.setPoliticalView(politicalView);
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

        SearchResultListener<TextSearchResponse> resultListener = new SearchResultListener<TextSearchResponse>() {
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
                            "[%s] siteId: '%s', name: %s, formatAddress: %s, country: %s, countryCode: %s, location: %s, poiTypes: %s, viewport is %s \r\n",
                            "" + (count++), site.getSiteId(), site.getName(), site.getFormatAddress(),
                            (addressDetail == null ? "" : addressDetail.getCountry()),
                            (addressDetail == null ? "" : addressDetail.getCountryCode()),
                            (location == null ? "" : (location.getLat() + "," + location.getLng())),
                            (poi == null ? "" : Arrays.toString(poi.getPoiTypes())),
                            (viewport == null ? "" : viewport.getNortheast() + "," + viewport.getSouthwest())));
                }
                resultTextView.setText(response.toString());
                Log.d(TAG, "onTextSearchResult: " + response.toString());
            }

            @Override
            public void onSearchError(SearchStatus status) {
                resultTextView.setText("Error : " + status.getErrorCode() + " " + status.getErrorMessage());
            }
        };

        searchService.textSearch(request, resultListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchService = null;
    }
}
