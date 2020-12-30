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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.AddressDetail;
import com.huawei.hms.site.api.model.AutoCompletePrediction;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.CoordinateBounds;
import com.huawei.hms.site.api.model.Poi;
import com.huawei.hms.site.api.model.QueryAutocompleteRequest;
import com.huawei.hms.site.api.model.QueryAutocompleteResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.api.model.Word;

import java.util.Arrays;

/**
 * Query AutoComplete Example Code
 */
public class QueryAutoCompleteActivity extends AppCompatActivity implements View.OnClickListener {
    // Declare a SearchService object.
    private SearchService searchService;

    private TextView resultText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_auto_complete);

        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(this, Utils.getApiKey());
        findViewById(R.id.search_query_auto_complete_button).setOnClickListener(this);
        resultText = findViewById(R.id.response_text_search);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_query_auto_complete_button) {
            queryAutoComplete();
        }
    }

    private void queryAutoComplete() {
        // Create a request body.
        QueryAutocompleteRequest request = new QueryAutocompleteRequest();
        String query = ((TextView) findViewById(R.id.query_suggestion_query_input)).getText().toString();
        String radius = ((TextView) findViewById(R.id.query_suggestion_radius_input)).getText().toString();
        String language = ((TextView) findViewById(R.id.query_suggestion_language_input)).getText().toString();
        String locationLatitude =
                ((TextView) findViewById(R.id.query_suggestion_location_lat_input)).getText().toString();
        String locationLongitude =
                ((TextView) findViewById(R.id.query_suggestion_location_lng_input)).getText().toString();

        if (!TextUtils.isEmpty(language)) {
            request.setLanguage(language);
        }

        if (!TextUtils.isEmpty(query)) {
            request.setQuery(query);
        }

        Integer radiusValue;
        if ((radiusValue = Utils.parseInt(radius)) != null) {
            request.setRadius(radiusValue);
        }

        Double lat = Utils.parseDouble(locationLatitude);
        Double lng = Utils.parseDouble(locationLongitude);
        if (lat != null && lng != null) {
            request.setLocation(new Coordinate(lat, lng));
        }
        request.setChildren(((Switch) findViewById(R.id.switch_query_auto_complete_children)).isChecked());
        // Create a search result listener.
        SearchResultListener<QueryAutocompleteResponse> resultListener = new SearchResultListener<QueryAutocompleteResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(QueryAutocompleteResponse results) {
                StringBuilder stringBuilder = new StringBuilder();
                if (results != null) {
                    AutoCompletePrediction[] predictions = results.getPredictions();
                    if (predictions != null && predictions.length > 0) {
                        stringBuilder.append("AutoCompletePrediction[ ]:\n");
                        int count = 1;
                        for (AutoCompletePrediction mPrediction : predictions) {
                            stringBuilder.append(String.format("[%s] Prediction,description = %s ,", "" + (count++), mPrediction.getDescription()));

                            Word[] matchedKeywords = mPrediction.getMatchedKeywords();
                            for (Word matchedKeyword : matchedKeywords) {
                                stringBuilder.append("matchedKeywords: " + matchedKeyword.toString());
                            }

                            Word[] matchedWords = mPrediction.getMatchedWords();
                            for (Word matchedWord : matchedWords) {
                                stringBuilder.append(",matchedWords: " + matchedWord.toString());
                            }

                            stringBuilder.append("\n");
                        }
                    } else {
                        stringBuilder.append("Predictions 0 results");
                    }

                    stringBuilder.append("\n\nSite[ ]:\n");

                    Site[] sites = results.getSites();
                    if (sites != null && sites.length > 0) {
                        int count = 1;
                        for (Site site : sites) {
                            AddressDetail addressDetail = site.getAddress();
                            Coordinate location = site.getLocation();
                            Poi poi = site.getPoi();
                            CoordinateBounds viewport = site.getViewport();

                            stringBuilder.append(String.format(
                                    "[%s] siteId: '%s', name: %s, formatAddress: %s, utcOffset: %s, country: %s, countryCode: %s, location: %s, distance: %s, poiTypes: %s, viewport is %s, streetNumber: %s, postalCode: %s , tertiaryAdminArea: %s, ",
                                    "" + (count++), site.getSiteId(), site.getName(), site.getFormatAddress(), site.getUtcOffset(),
                                    (addressDetail == null ? "" : addressDetail.getCountry()),
                                    (addressDetail == null ? "" : addressDetail.getCountryCode()),
                                    (location == null ? "" : (location.getLat() + "," + location.getLng())), site.getDistance(),
                                    (poi == null ? "" : Arrays.toString(poi.getPoiTypes())),
                                    (viewport == null ? "" : "northeast{lat=" + viewport.getNortheast().getLat() + ", lng=" + viewport.getNortheast().getLng() + "},"
                                            + "southwest{lat=" + viewport.getSouthwest().getLat() + ", lng=" + viewport.getSouthwest().getLng() + "}"),
                                    (addressDetail == null ? "" : addressDetail.getStreetNumber()),
                                    (addressDetail == null ? "" : addressDetail.getPostalCode()),
                                    (addressDetail == null ? "" : addressDetail.getTertiaryAdminArea())));
                            if ((poi != null)) {
                                Gson g = new Gson();
                                String jsonString = g.toJson(poi.getChildrenNodes());
                                stringBuilder.append(String.format("childrenNode: %s \n\n", jsonString));
                            }
                        }
                    } else {
                        stringBuilder.append("sites 0 results\n");
                    }
                }
                resultText.setText(stringBuilder);
            }

            // Return the result code and description upon a search exception.
            @Override
            public void onSearchError(SearchStatus status) {
                resultText.setText("onSearchError errorCode = " + status.getErrorCode());
            }
        };
        // Call the nearby place search API.
        searchService.queryAutocomplete(request, resultListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchService = null;
    }
}
