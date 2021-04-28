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
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.*
import com.huawei.hms.site.sample.Utils.apiKey
import com.huawei.hms.site.sample.Utils.parseDouble
import com.huawei.hms.site.sample.Utils.parseInt
import java.util.*

/**
 * Keyword Search Example Code
 */
class TextSearchActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "TextSearchActivity"
    }

    private lateinit var queryInput: EditText
    private lateinit var latInput: EditText
    private lateinit var lngInput: EditText
    private lateinit var radiusInput: EditText
    private lateinit var poiTypeSpinner: Spinner
    private lateinit var countryCodeInput: EditText
    private lateinit var languageInput: EditText
    private lateinit var pageIndexInput: EditText
    private lateinit var pageSizeInput: EditText
    private lateinit var resultTextView: TextView
    private var countryListSpinner: CheckboxCountriesSpinner? = null

    // Declare a SearchService object.
    private var searchService: SearchService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_search)

        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(this, apiKey)
        queryInput = findViewById(R.id.edit_text_text_search_query)
        latInput = findViewById(R.id.edit_text_text_search_location_lat)
        lngInput = findViewById(R.id.edit_text_text_search_location_lng)
        radiusInput = findViewById(R.id.edit_text_text_search_radius)
        poiTypeSpinner = findViewById(R.id.spinner_text_search_poitype)
        poiTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf(*LocationType.values()))
        val usePOITypeSwitch = findViewById<Switch>(R.id.switch_text_search_poitype)
        usePOITypeSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean -> poiTypeSpinner.isEnabled = isChecked }
        countryCodeInput = findViewById(R.id.edit_text_text_search_country_code)
        languageInput = findViewById(R.id.edit_text_text_search_language)
        pageIndexInput = findViewById(R.id.edit_text_text_search_pageindex)
        pageSizeInput = findViewById(R.id.edit_text_text_search_pagesize)
        resultTextView = findViewById(R.id.response_text_search)
        findViewById<View>(R.id.button_text_search).setOnClickListener { execTextSearch() }
        poiTypeSpinner.isEnabled = false

        val countryList: MutableList<String> = ArrayList()
        countryList.add("en")
        countryList.add("fr")
        countryList.add("cn")
        countryList.add("de")
        countryList.add("ko")

        val countryListInput = findViewById<TextView>(R.id.text_search_country_list_input)
        countryListInput.isEnabled = false

        countryListSpinner = CheckboxCountriesSpinner(findViewById(R.id.switch_text_search_countrylist), countryListInput, countryList)
    }

    private fun execTextSearch() {
        Log.d(TAG, "execTextSearch: $this")
        Log.d(TAG, "execTextSearch: $applicationContext")
        Log.d(TAG, "execTextSearch: "
                + if (applicationContext == null) "" else applicationContext.applicationContext)
        val query = queryInput.text.toString()
        if (TextUtils.isEmpty(query)) {
            resultTextView.text = "Error : Query is empty!"
            return
        }
        // Create a request body.
        val request = TextSearchRequest()
        request.setQuery(query)
        val lat: Double?
        val lng: Double?
        val latStr = latInput.text.toString()
        val lngStr = lngInput.text.toString()
        if (latStr.isNotEmpty() || lngStr.isNotEmpty()) {
            lat = parseDouble(latStr)
            lng = parseDouble(lngStr)
            if (lat == null || lng == null) {
                resultTextView.text = "Error : Location is invalid!"
                return
            }
            request.setLocation(Coordinate(lat, lng))
        }

        val radiusInt: Int? = parseInt(radiusInput.text.toString())
        if (radiusInt == null || radiusInt <= 0) {
            resultTextView.text = "Error : Radius Must be greater than 0 !"
            return
        }
        request.setRadius(radiusInt)
        val poiType = if (poiTypeSpinner.isEnabled) poiTypeSpinner.selectedItem as LocationType else null
        if (poiType != null) {
            request.setPoiType(poiType)
        }
        val countryCode = countryCodeInput.text.toString()
        if (!TextUtils.isEmpty(countryCode)) {
            request.setCountryCode(countryCode)
        }
        val countryList: List<String> = getCountryList()
        if (countryList.isNotEmpty()) {
            request.countries = countryList
        }
        val language = languageInput.text.toString()
        if (!TextUtils.isEmpty(language)) {
            request.setLanguage(language)
        }

        val pageIndexInt: Int? = parseInt(pageIndexInput.text.toString())
        if (pageIndexInt == null || pageIndexInt < 1 || pageIndexInt > 60) {
            resultTextView.text = "Error : PageIndex Must be between 1 and 60!"
            return
        }
        request.setPageIndex(pageIndexInt)

        val pageSizeInt: Int? = parseInt(pageSizeInput.text.toString())
        if (pageSizeInt == null || pageSizeInt < 1 || pageSizeInt > 20) {
            resultTextView.text = "Error : PageSize Must be between 1 and 20!"
            return
        }
        request.setPageSize(pageSizeInt)
        request.isChildren = findViewById<Switch>(R.id.switch_text_search_children).isChecked
        // Create a search result listener.
        val resultListener: SearchResultListener<TextSearchResponse> = object : SearchResultListener<TextSearchResponse> {
            // Return search results upon a successful search.
            override fun onSearchResult(results: TextSearchResponse?) {
                val siteList: List<Site>? = results?.getSites()
                if (results == null || results.getTotalCount() <= 0 || siteList.isNullOrEmpty()) {
                    resultTextView.text = "Result is Empty!"
                    return
                }
                val response = StringBuilder("\n")
                response.append("success\n")
                var count = 1
                var addressDetail: AddressDetail
                var location: Coordinate
                var poi: Poi
                var viewport: CoordinateBounds
                for (site in siteList) {
                    addressDetail = site.getAddress()
                    location = site.getLocation()
                    poi = site.getPoi()
                    viewport = site.getViewport()
                    response.append(String.format(
                            "[%s] siteId: '%s', name: %s, formatAddress: %s, country: %s, countryCode: %s, location: %s, poiTypes: %s, viewport: %s, ",
                            "" + count++, site.getSiteId(), site.getName(), site.getFormatAddress(),
                            if (addressDetail == null) "" else addressDetail.getCountry(),
                            if (addressDetail == null) "" else addressDetail.getCountryCode(),
                            if (location == null) "" else location.getLat().toString() + "," + location.getLng(),
                            if (poi == null) "" else Arrays.toString(poi.getPoiTypes()),
                            if (viewport == null) "" else "northeast{lat=" + viewport.getNortheast().getLat().toString() + ", lng=" + viewport.getNortheast().getLng().toString() + "},"
                                    + "southwest{lat=" + viewport.getSouthwest().getLat().toString() + ", lng=" + viewport.getSouthwest().getLng().toString() + "}"))
                    if (poi != null) {
                        val g = Gson()
                        val jsonString = g.toJson(poi.getChildrenNodes())
                        response.append(String.format("childrenNode: %s \n\n", jsonString))
                    }
                }
                resultTextView.text = response.toString()
                Log.d(TAG, "onTextSearchResult: $response")
            }

            // Return the result code and description upon a search exception.
            override fun onSearchError(status: SearchStatus) {
                resultTextView.text = "Error : ${status.getErrorCode()}  ${status.getErrorMessage()}"
            }
        }
        // Call the place search API.
        searchService?.textSearch(request, resultListener)
    }

    private fun getCountryList(): List<String> {
        return if (findViewById<Switch>(R.id.switch_text_search_countrylist).isChecked) {
            countryListSpinner!!.selectedCountryList
        } else {
            ArrayList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchService = null
    }
}