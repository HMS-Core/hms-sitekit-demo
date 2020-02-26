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
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.Poi;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class NearbySearchActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner poiTypeSpinner;
    EditText pageIndexInput;
    EditText pageSizeInput;
    SearchResultListener searchResultListener = new SearchResultListener<NearbySearchResponse>() {
        @Override
        public void onSearchResult(NearbySearchResponse results) {
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
                                "[%s] siteId: '%s', name: %s, formatAddress: %s, country: %s, countryCode: %s, location: %s, poiTypes: %s, viewport is %s \n\n",
                                "" + (count++), site.getSiteId(), site.getName(), site.getFormatAddress(),
                                (addressDetail == null ? "" : addressDetail.getCountry()),
                                (addressDetail == null ? "" : addressDetail.getCountryCode()),
                                (location == null ? "" : (location.getLat() + "," + location.getLng())),
                                (poi == null ? "" : Arrays.toString(poi.getPoiTypes())),
                                (viewport == null ? "" : viewport.getNortheast() + "," + viewport.getSouthwest())));
                    }
                } else {
                    stringBuilder.append("0 results");
                }
            }
            showSuccessResult(stringBuilder.toString());
        }

        @Override
        public void onSearchError(SearchStatus status) {
            showFailResult("", status.getErrorCode(), status.getErrorMessage());
        }
    };
    private SearchService searchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_search);

        searchService = SearchServiceFactory.create(this);

        findViewById(R.id.search_nearby_button).setOnClickListener(this);

        poiTypeSpinner = findViewById(R.id.spinner_nearby_search_poitype);
        poiTypeSpinner.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList(LocationType.values())));

        Switch usePOITypeSwitch = findViewById(R.id.switch_nearby_search_poitype);
        usePOITypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                poiTypeSpinner.setEnabled(isChecked);
            }
        });

        pageIndexInput = findViewById(R.id.edit_text_nearby_search_pageindex);
        pageSizeInput = findViewById(R.id.edit_text_nearby_search_pagesize);

        poiTypeSpinner.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.search_nearby_button) {
            nearbySearch();
        }
    }

    private void nearbySearch() {
        NearbySearchRequest request = new NearbySearchRequest();

        String locationLatitude = ((TextView) findViewById(R.id.nearby_search_location_lat_input)).getText().toString();
        String locationLongitude =
                ((TextView) findViewById(R.id.nearby_search_location_lng_input)).getText().toString();
        Double lat;
        Double lng;
        if (!TextUtils.isEmpty(locationLatitude) || !TextUtils.isEmpty(locationLongitude)) {
            if ((lat = Utils.parseDouble(locationLatitude)) == null
                    || (lng = Utils.parseDouble(locationLongitude)) == null) {
                showFailResult("Location is invalid!", "", "");
                return;
            }
            request.setLocation(new Coordinate(lat, lng));
        }

        String query = ((TextView) findViewById(R.id.nearby_search_query_input)).getText().toString();
        if (!TextUtils.isEmpty(query)) {
            request.setQuery(query);
        }

        String radius = ((TextView) findViewById(R.id.nearby_search_radius_input)).getText().toString();
        Integer radiusValue;
        if ((radiusValue = Utils.parseInt(radius)) != null) {
            request.setRadius(radiusValue);
        }

        LocationType poiType = poiTypeSpinner.isEnabled() ? (LocationType) poiTypeSpinner.getSelectedItem() : null;
        if (poiType != null) {
            request.setPoiType(poiType);
        }

        String language = ((TextView) findViewById(R.id.nearby_search_language_input)).getText().toString();
        if (!TextUtils.isEmpty(language)) {
            request.setLanguage(language);
        }

        String politicalView = ((TextView) findViewById(R.id.nearby_search_politicalview_input)).getText().toString();
        if (!TextUtils.isEmpty(politicalView)) {
            request.setPoliticalView(politicalView);
        }

        String pageIndex = pageIndexInput.getText().toString();
        if (!TextUtils.isEmpty(pageIndex)) {
            Integer pageIndexInt;
            if ((pageIndexInt = Utils.parseInt(pageIndex)) == null || pageIndexInt < 1 || pageIndexInt > 60) {
                showFailResult("PageIndex Must be between 1 and 60!", "", "");
                return;
            }
            request.setPageIndex(pageIndexInt);
        }

        String pageSize = pageSizeInput.getText().toString();
        if (!TextUtils.isEmpty(pageSize)) {
            Integer pageSizeInt;
            if ((pageSizeInt = Utils.parseInt(pageSize)) == null || pageSizeInt < 1 || pageSizeInt > 20) {
                showFailResult("PageSize Must be between 1 and 20!", "", "");
                return;
            }
            request.setPageSize(pageSizeInt);
        }

        searchService.nearbySearch(request, searchResultListener);
    }

    private void showFailResult(String result, String errorCode, String errorMessage) {
        ((TextView) findViewById(R.id.nearby_search_result_status)).setText("failed " + errorCode + " " + errorMessage);
        ((TextView) findViewById(R.id.nearby_search_result_text)).setText(result);
    }

    private void showSuccessResult(String result) {
        ((TextView) findViewById(R.id.nearby_search_result_status)).setText("success");
        ((TextView) findViewById(R.id.nearby_search_result_text)).setText(result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchService = null;
    }
}
