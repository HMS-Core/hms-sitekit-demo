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

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.huawei.hms.site.api.model.*
import com.huawei.hms.site.sample.Utils.apiKey
import com.huawei.hms.site.sample.Utils.parseInt
import com.huawei.hms.site.widget.SearchFilter
import com.huawei.hms.site.widget.SearchFragment
import com.huawei.hms.site.widget.SiteSelectionListener
import java.util.*

class SearchFragmentActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "SearchFragmentActivity"
    }

    private lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_fragment)
        textView = findViewById(R.id.demo_fragment_textView_result)
        val searchFilter = buildSearchFilter()
        if (null == searchFilter) {
            Log.e(TAG, "searchFilter is null.")
            return
        }
        initFragment(searchFilter)
    }

    private fun initFragment(searchFilter: SearchFilter) {
        val searchFragment = supportFragmentManager.findFragmentById(R.id.demo_fragment) as SearchFragment?
        searchFragment?.setSearchFilter(searchFilter)
        searchFragment?.setHint("Click to start the query.")
        val apiKey = apiKey
        searchFragment?.setApiKey(apiKey)
        searchFragment?.setOnSiteSelectedListener(object : SiteSelectionListener {
            override fun onSiteSelected(data: Site) {
                val response = StringBuilder("\n")
                val addressDetail = data.getAddress()
                val location = data.getLocation()
                val poi = data.getPoi()
                val viewport = data.getViewport()
                response.append(String.format(
                        "siteId: '%s', Name: %s, FormatAddress: %s, Country: %s, CountryCode: %s, Location: %s, PoiTypes: %s, Viewport: %s, streetNumber: %s, postalCode: %s , tertiaryAdminArea: %s \n\n",
                        data.getSiteId(), data.getName(), data.getFormatAddress(),
                        if (addressDetail == null) "" else addressDetail.getCountry(),
                        if (addressDetail == null) "" else addressDetail.getCountryCode(),
                        if (location == null) "" else location.getLat().toString() + "," + location.getLng(),
                        if (poi == null) "" else Arrays.toString(poi.getPoiTypes()),
                        if (viewport == null) "" else viewport.getNortheast().toString() + "," + viewport.getSouthwest(),
                        if (addressDetail == null) "" else addressDetail.getStreetNumber(),
                        if (addressDetail == null) "" else addressDetail.getPostalCode(),
                        if (addressDetail == null) "" else addressDetail.getTertiaryAdminArea()))
                if (poi != null) {
                    val g = Gson()
                    val jsonString = g.toJson(poi.getChildrenNodes())
                    response.append(String.format("childrenNode: %s,", jsonString))
                }
                textView.text = response.toString()
            }

            override fun onError(status: SearchStatus) {
                textView.text = "Error code from fragment: ${status.getErrorCode()}  Error message from fragment: ${status.getErrorMessage()}"
            }
        })
    }

    private fun buildSearchFilter(): SearchFilter? {
        var searchFilter: SearchFilter? = null
        if (null != intent) {
            val intent = intent
            val query: String? = intent.getStringExtra("query")
            val language: String? = intent.getStringExtra("language")
            val countryCode: String? = intent.getStringExtra("countryCode")
            val radius: String? = intent.getStringExtra("radius")
            val bounds: CoordinateBounds? = intent.getParcelableExtra("bounds")
            val location: Coordinate? = intent.getParcelableExtra("location")
            var poiTypes: List<LocationType>? = null
            if (null != intent.getSerializableExtra("poiTypes")) {
                poiTypes = intent.getSerializableExtra("poiTypes") as List<LocationType>
            }
            val children: Boolean? = intent.getBooleanExtra("children", false)
            val strictBounds: Boolean? = intent.getBooleanExtra("strictBounds", false)
            searchFilter = SearchFilter()
            if (!language.isNullOrEmpty()) {
                searchFilter.language = language
            }
            if (!query.isNullOrEmpty()) {
                searchFilter.query = query
            }
            if (!countryCode.isNullOrEmpty()) {
                searchFilter.countryCode = countryCode
            }
            if (!radius.isNullOrEmpty()) {
                val radiusValue: Int? = parseInt(radius!!)
                searchFilter.radius = radiusValue
            }
            if (null != bounds) {
                searchFilter.bounds = bounds
            }
            if (null != location) {
                searchFilter.location = location
            }
            if (null != poiTypes) {
                searchFilter.poiType = poiTypes
            }
            searchFilter.isChildren = children!!
            searchFilter.strictBounds = strictBounds
        }
        return searchFilter
    }
}