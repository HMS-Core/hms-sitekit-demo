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
import com.huawei.hms.site.widget.SearchFragment;
import com.huawei.hms.site.widget.SiteSelectionListener;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchFragmentActivity extends AppCompatActivity {
    private static final String TAG = "SearchFragmentActivity";

    private TextView textView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_fragment);
        textView = findViewById(R.id.demo_fragment_textView_result);
        SearchFilter searchFilter = buildSearchFilter();
        if (null == searchFilter) {
            Log.e(TAG, "searchFilter is null.");
            return;
        }
        initFragment(searchFilter);
    }

    private void initFragment(SearchFilter searchFilter) {
        SearchFragment searchFragment =
            (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.demo_fragment);
        searchFragment.setSearchFilter(searchFilter);
        searchFragment.setHint("Click to start the query.");
        String apiKey = Utils.getApiKey();
        searchFragment.setApiKey(apiKey);

        if (searchFragment != null) {
            searchFragment.setOnSiteSelectedListener(new SiteSelectionListener() {
                @Override
                public void onSiteSelected(Site data) {
                    Site site = data;

                    StringBuilder response = new StringBuilder("\n");
                    AddressDetail addressDetail = site.getAddress();
                    Coordinate location = site.getLocation();
                    Poi poi = site.getPoi();
                    CoordinateBounds viewport = site.getViewport();
                    response.append(String.format(
                        "siteId: '%s', Name: %s, FormatAddress: %s, Country: %s, CountryCode: %s, Location: %s, PoiTypes: %s, Viewport: %s, streetNumber: %s, postalCode: %s , tertiaryAdminArea: %s \n\n",
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
                        response.append(String.format("childrenNode: %s,", jsonString));
                    }
                    textView.setText(response.toString());
                }

                @Override
                public void onError(SearchStatus status) {
                    textView.setText(" Error code from fragment: " + status.getErrorCode()
                        + "\n Error message from fragment: " + status.getErrorMessage());
                }
            });
        }
    }

    private SearchFilter buildSearchFilter() {
        SearchFilter searchFilter = null;
        if (null != getIntent()) {
            Intent intent = getIntent();

            String query = intent.getStringExtra("query");
            String language = intent.getStringExtra("language");
            String countryCode = intent.getStringExtra("countryCode");
            String radius = intent.getStringExtra("radius");
            CoordinateBounds bounds = intent.getParcelableExtra("bounds");
            Coordinate location = intent.getParcelableExtra("location");
            List<LocationType> poiTypes = (List<LocationType>) intent.getSerializableExtra("poiTypes");
            boolean children = intent.getBooleanExtra("children", false);
            boolean strictBounds = intent.getBooleanExtra("strictBounds", false);
            List<String> countryList = (List<String>) intent.getSerializableExtra("countries");

            searchFilter = new SearchFilter();

            if (!TextUtils.isEmpty(language)) {
                searchFilter.setLanguage(language);
            }
            if (!TextUtils.isEmpty(query)) {
                searchFilter.setQuery(query);
            }
            if (!TextUtils.isEmpty(countryCode)) {
                searchFilter.setCountryCode(countryCode);
            }
            if (null != countryList && countryList.size() > 0) {
                searchFilter.setCountries(countryList);
            }

            Integer radiusValue;
            if ((radiusValue = Utils.parseInt(radius)) != null) {
                searchFilter.setRadius(radiusValue);
            }

            if (null != bounds) {
                searchFilter.setBounds(bounds);
            }

            if (null != location) {
                searchFilter.setLocation(location);
            }

            if (null != poiTypes) {
                searchFilter.setPoiType(poiTypes);
            }
            searchFilter.setChildren(children);
            searchFilter.setStrictBounds(strictBounds);
        }
        return searchFilter;
    }
}
