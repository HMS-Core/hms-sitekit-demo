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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.text_search_button).setOnClickListener(this);
        findViewById(R.id.detail_search_button).setOnClickListener(this);
        findViewById(R.id.nearby_search_button).setOnClickListener(this);
        findViewById(R.id.query_suggestion_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_search_button:
                startActivity(new Intent(this, TextSearchActivity.class));
                break;
            case R.id.detail_search_button:
                startActivity(new Intent(this, DetailSearchActivity.class));
                break;
            case R.id.nearby_search_button:
                startActivity(new Intent(this, NearbySearchActivity.class));
                break;
            case R.id.query_suggestion_button:
                startActivity(new Intent(this, QuerySuggestionActivity.class));
                break;
            default:
                break;
        }
    }

}
