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
package com.huawei.hms.site.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/***
 * Mian activity
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.text_search_button).setOnClickListener(this)
        findViewById<View>(R.id.detail_search_button).setOnClickListener(this)
        findViewById<View>(R.id.nearby_search_button).setOnClickListener(this)
        findViewById<View>(R.id.query_suggestion_button).setOnClickListener(this)
        findViewById<View>(R.id.query_auto_complete_button).setOnClickListener(this)
        findViewById<View>(R.id.search_widget_button).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            // Jump to keyword search activity.
            R.id.text_search_button -> startActivity(Intent(this, TextSearchActivity::class.java))
            // Jump to place detail search activity.
            R.id.detail_search_button -> startActivity(Intent(this, DetailSearchActivity::class.java))
            // Jump to nearby place search activity.
            R.id.nearby_search_button -> startActivity(Intent(this, NearbySearchActivity::class.java))
            // Jump to keyword search activity.
            R.id.query_suggestion_button -> startActivity(Intent(this, QuerySuggestionActivity::class.java))
            // Jump to auto complete activity.
            R.id.query_auto_complete_button -> startActivity(Intent(this, QueryAutoCompleteActivity::class.java))
            // Jump to auto search intent activity.
            R.id.search_widget_button -> startActivity(Intent(this, SearchIntentActivity::class.java))
            else -> {
            }
        }
    }
}