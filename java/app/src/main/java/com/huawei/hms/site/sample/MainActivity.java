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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/***
 * Mian activity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.text_search_button).setOnClickListener(this);
        findViewById(R.id.detail_search_button).setOnClickListener(this);
        findViewById(R.id.nearby_search_button).setOnClickListener(this);
        findViewById(R.id.query_suggestion_button).setOnClickListener(this);
        findViewById(R.id.query_auto_complete_button).setOnClickListener(this);
        findViewById(R.id.search_widget_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Jump to keyword search activity.
            case R.id.text_search_button:
                startActivity(new Intent(this, TextSearchActivity.class));
                break;
            // Jump to place detail search activity.
            case R.id.detail_search_button:
                startActivity(new Intent(this, DetailSearchActivity.class));
                break;
            // Jump to nearby place search activity.
            case R.id.nearby_search_button:
                startActivity(new Intent(this, NearbySearchActivity.class));
                break;
            // Jump to keyword search activity.
            case R.id.query_suggestion_button:
                startActivity(new Intent(this, QuerySuggestionActivity.class));
                break;
            // Jump to auto complete activity.
            case R.id.query_auto_complete_button:
                startActivity(new Intent(this, QueryAutoCompleteActivity.class));
                break;
            // Jump to auto search intent activity.
            case R.id.search_widget_button:
                startActivity(new Intent(this, SearchIntentActivity.class));
                break;
            default:
                break;
        }
    }

}
