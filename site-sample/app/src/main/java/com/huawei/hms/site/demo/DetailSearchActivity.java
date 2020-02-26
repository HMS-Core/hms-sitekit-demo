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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.AddressDetail;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.CoordinateBounds;
import com.huawei.hms.site.api.model.DetailSearchRequest;
import com.huawei.hms.site.api.model.DetailSearchResponse;
import com.huawei.hms.site.api.model.Poi;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailSearchActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DetailSearchActivity";

    EditText siteIdInput;

    EditText languageInput;

    EditText politicalViewInput;

    TextView resultTextView;

    ImageView sitePhotoView;

    Bitmap bitmap = null;
    public Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    sitePhotoView.setImageBitmap(bitmap);
                    sitePhotoView.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    sitePhotoView.setImageBitmap(null);
                    sitePhotoView.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    private SearchService searchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_search);

        searchService = SearchServiceFactory.create(this);

        siteIdInput = findViewById(R.id.edit_text_detail_search_site_id);

        languageInput = findViewById(R.id.edit_text_detail_search_language);

        politicalViewInput = findViewById(R.id.edit_text_detail_search_politicalview);

        findViewById(R.id.button_detail_search).setOnClickListener(this);
        resultTextView = findViewById(R.id.response_detail_search);
        sitePhotoView = findViewById(R.id.search_detail_site_photo_image);
        sitePhotoView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_detail_search) {
            execDetailSearch();
        }
    }

    private void execDetailSearch() {
        String siteId = siteIdInput.getText().toString();
        if (TextUtils.isEmpty(siteId) || siteId.getBytes().length > 256) {
            resultTextView.setText("Error : Site Id is empty!");
            return;
        }

        DetailSearchRequest request = new DetailSearchRequest();
        request.setSiteId(siteId);

        String language = languageInput.getText().toString();
        if (!TextUtils.isEmpty(language)) {
            request.setLanguage(language);
        }

        String politicalView = politicalViewInput.getText().toString();
        if (!TextUtils.isEmpty(politicalView)) {
            request.setPoliticalView(politicalView);
        }

        SearchResultListener<DetailSearchResponse> resultListener = new SearchResultListener<DetailSearchResponse>() {
            @Override
            public void onSearchResult(DetailSearchResponse results) {
                Site site;
                if (results == null || (site = results.getSite()) == null) {
                    resultTextView.setText("Result is Empty!");
                    return;
                }
                AddressDetail addressDetail = site.getAddress();
                Coordinate location = site.getLocation();
                Poi poi = site.getPoi();
                CoordinateBounds viewport = site.getViewport();
                StringBuilder response = new StringBuilder("\n");
                response.append("success\n");
                response.append(String.format(
                        "siteId: '%s', Name: %s, FormatAddress: %s, Country: %s, CountryCode: %s, Location: %s, PoiTypes: %s, Viewport: %s \r\n",
                        site.getSiteId(), site.getName(), site.getFormatAddress(),
                        (addressDetail == null ? "" : addressDetail.getCountry()),
                        (addressDetail == null ? "" : addressDetail.getCountryCode()),
                        (location == null ? "" : (location.getLat() + "," + location.getLng())),
                        (poi == null ? "" : Arrays.toString(poi.getPoiTypes())),
                        (viewport == null ? "" : viewport.getNortheast() + "," + viewport.getSouthwest())));

                Switch displaySwitch = findViewById(R.id.detail_search_photo_switch);
                String[] photoUrls = poi != null ? poi.getPhotoUrls() : null;
                String photoUrl = photoUrls != null && photoUrls.length > 0 ? photoUrls[0] : "";
                if (displaySwitch.isChecked() && !TextUtils.isEmpty(photoUrl)) {
                    try {
                        displayPhoto(photoUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                        resultTextView.setText(response.append("**********************")
                                .append("\r\n")
                                .append("But Get Photo Error : " + e.getMessage())
                                .toString());
                        return;
                    }
                } else {
                    sitePhotoView.setImageBitmap(null);
                }

                resultTextView.setText(response.toString());
                Log.d(TAG, "onDetailSearchResult: " + response.toString());
            }

            @Override
            public void onSearchError(SearchStatus status) {
                resultTextView.setText("Error : " + status.getErrorCode() + " " + status.getErrorMessage());
            }
        };

        searchService.detailSearch(request, resultListener);
    }

    private void displayPhoto(String photoUrl) {
        Request request = new Request.Builder().url(photoUrl).get().build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    InputStream inputStream = response.body().byteStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    uiHandler.sendEmptyMessage(0);
                } else {
                    uiHandler.sendEmptyMessage(1);
                    Log.e(TAG, "get photo from server failed. httpCode=" + response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException ioe) {
                uiHandler.sendEmptyMessage(1);
                Log.e(TAG, "get photo from server failed.IOException");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchService = null;
    }
}
