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
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.*
import com.huawei.hms.site.sample.Utils.apiKey
import com.huawei.hms.site.sample.Utils.parseDouble
import com.huawei.hms.site.sample.Utils.parseInt
import java.util.*

/**
 * Nearby Place Search Example Code
 */
class NearbySearchActivity : AppCompatActivity(), View.OnClickListener {

    // Declare a SearchService object.
    private var searchService: SearchService? = null
    private lateinit var poiTypeSpinner: Spinner
    private lateinit var pageIndexInput: EditText
    private lateinit var pageSizeInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_search)

        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(this, apiKey)
        findViewById<View>(R.id.search_nearby_button).setOnClickListener(this)
        poiTypeSpinner = findViewById(R.id.spinner_nearby_search_poitype)
        poiTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf(*LocationType.values()))
        val usePOITypeSwitch = findViewById<Switch>(R.id.switch_nearby_search_poitype)
        usePOITypeSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean -> poiTypeSpinner.isEnabled = isChecked }
        pageIndexInput = findViewById(R.id.edit_text_nearby_search_pageindex)
        pageSizeInput = findViewById(R.id.edit_text_nearby_search_pagesize)
        poiTypeSpinner.isEnabled = false
    }

    // Create a search result listener.
    private val searchResultListener: SearchResultListener<NearbySearchResponse?> = object : SearchResultListener<NearbySearchResponse?> {
        // Return search results upon a successful search.
        override fun onSearchResult(results: NearbySearchResponse?) {
            val stringBuilder = StringBuilder()
            if (results != null) {
                val sites = results.getSites()
                if (sites != null && sites.size > 0) {
                    var count = 1
                    for (site in sites) {
                        val addressDetail = site.getAddress()
                        val location = site.getLocation()
                        val poi = site.getPoi()
                        val viewport = site.getViewport()
                        stringBuilder.append(String.format(
                                "[%s] siteId: '%s', name: %s, formatAddress: %s, country: %s, countryCode: %s, location: %s, poiTypes: %s, viewport is %s \n\n",
                                "" + count++, site.getSiteId(), site.getName(), site.getFormatAddress(),
                                if (addressDetail == null) "" else addressDetail.getCountry(),
                                if (addressDetail == null) "" else addressDetail.getCountryCode(),
                                if (location == null) "" else location.getLat().toString() + "," + location.getLng(),
                                if (poi == null) "" else Arrays.toString(poi.getPoiTypes()),
                                if (viewport == null) "" else "northeast{lat=" + viewport.getNortheast().getLat().toString() + ", lng=" + viewport.getNortheast().getLng().toString() + "},"
                                        + "southwest{lat=" + viewport.getSouthwest().getLat().toString() + ", lng=" + viewport.getSouthwest().getLng().toString() + "}"))
                    }
                } else {
                    stringBuilder.append("0 results")
                }
            }
            showSuccessResult(stringBuilder.toString())
        }

        // Return the result code and description upon a search exception.
        override fun onSearchError(status: SearchStatus) {
            showFailResult("", status.getErrorCode(), status.getErrorMessage())
        }
    }

    override fun onClick(view: View) {
        if (view.id == R.id.search_nearby_button) {
            nearbySearch()
        }
    }

    private fun nearbySearch() {
        // Create a request body.
        val request = NearbySearchRequest()
        val locationLatitude = (findViewById<View>(R.id.nearby_search_location_lat_input) as TextView).text.toString()
        val locationLongitude = (findViewById<View>(R.id.nearby_search_location_lng_input) as TextView).text.toString()
        val lat: Double?
        val lng: Double?
        if (locationLatitude.isNotEmpty() || locationLongitude.isNotEmpty()) {
            lat = parseDouble(locationLatitude)
            lng = parseDouble(locationLongitude)
            if (lat == null || lng == null) {
                showFailResult("Location is invalid!", "", "")
                return
            }
            request.setLocation(Coordinate(lat, lng))
        }
        val query = (findViewById<View>(R.id.nearby_search_query_input) as TextView).text.toString()
        if (!TextUtils.isEmpty(query)) {
            request.setQuery(query)
        }
        val radius = (findViewById<View>(R.id.nearby_search_radius_input) as TextView).text.toString()
        var radiusValue: Int?
        if (parseInt(radius).also { radiusValue = it } != null) {
            request.setRadius(radiusValue)
        }
        val poiType = if (poiTypeSpinner.isEnabled) poiTypeSpinner.selectedItem as LocationType else null
        if (poiType != null) {
            request.setPoiType(poiType)
        }
        val language = (findViewById<View>(R.id.nearby_search_language_input) as TextView).text.toString()
        if (!TextUtils.isEmpty(language)) {
            request.setLanguage(language)
        }
        val pageIndexInt: Int? = parseInt(pageIndexInput.text.toString())
        if (pageIndexInt == null || pageIndexInt < 1 || pageIndexInt > 60) {
            showFailResult("PageIndex Must be between 1 and 60!", "", "")
            return
        }
        request.setPageIndex(pageIndexInt)

        val pageSizeInt: Int? = parseInt(pageSizeInput.text.toString())
        if (pageSizeInt == null || pageSizeInt < 1 || pageSizeInt > 20) {
            showFailResult("PageSize Must be between 1 and 20!", "", "")
            return
        }
        request.setPageSize(pageSizeInt)

        // Call the nearby place search API.
        searchService?.nearbySearch(request, searchResultListener)
    }

    private fun showFailResult(result: String, errorCode: String, errorMessage: String) {
        (findViewById<View>(R.id.nearby_search_result_status) as TextView).text = "failed $errorCode $errorMessage"
        (findViewById<View>(R.id.nearby_search_result_text) as TextView).text = result
    }

    private fun showSuccessResult(result: String) {
        (findViewById<View>(R.id.nearby_search_result_status) as TextView).text = "success"
        (findViewById<View>(R.id.nearby_search_result_text) as TextView).text = result
    }

    override fun onDestroy() {
        super.onDestroy()
        searchService = null
    }
}