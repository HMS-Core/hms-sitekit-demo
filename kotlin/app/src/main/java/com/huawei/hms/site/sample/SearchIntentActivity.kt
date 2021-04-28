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
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.huawei.hms.site.api.model.Coordinate
import com.huawei.hms.site.api.model.CoordinateBounds
import com.huawei.hms.site.api.model.LocationType
import com.huawei.hms.site.sample.SearchFragmentActivity
import com.huawei.hms.site.sample.Utils.apiKey
import com.huawei.hms.site.sample.Utils.parseDouble
import com.huawei.hms.site.sample.Utils.parseInt
import com.huawei.hms.site.widget.SearchFilter
import com.huawei.hms.site.widget.SearchIntent
import java.io.Serializable
import java.util.*

class SearchIntentActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var childrenSwitch: Switch
    private lateinit var strictBoundsSwitch: Switch

    private var searchIntent: SearchIntent? = null
    private var locationTypeSpinner: CheckboxSpinner? = null

    private var countryListSpinner: CheckboxCountriesSpinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_intent)
        searchIntent = SearchIntent()
        textView = findViewById(R.id.demo_fragment_textView_result)
        childrenSwitch = findViewById(R.id.childrenswitch)
        strictBoundsSwitch = findViewById(R.id.switch_strict_bounds)

        val poiTypes: MutableList<LocationType> = ArrayList()
        poiTypes.add(LocationType.GEOCODE)
        poiTypes.add(LocationType.ADDRESS)
        poiTypes.add(LocationType.ESTABLISHMENT)
        poiTypes.add(LocationType.REGIONS)
        poiTypes.add(LocationType.CITIES)
        val poiTypesInput = findViewById<TextView>(R.id.query_suggestion_poi_type_input)
        poiTypesInput.isEnabled = false

        locationTypeSpinner = CheckboxSpinner(findViewById(R.id.switch_query_suggestion_poitype), poiTypesInput, poiTypes)

        val countryList: MutableList<String> = ArrayList()
        countryList.add("en")
        countryList.add("fr")
        countryList.add("cn")
        countryList.add("de")
        countryList.add("ko")

        val countryListInput = findViewById<TextView>(R.id.search_fragment_country_list_input)
        countryListInput.isEnabled = false

        countryListSpinner = CheckboxCountriesSpinner(findViewById(R.id.switch_search_fragment_countrylist), countryListInput, countryList)
    }

    fun openSearchActivity(view: View?) {
        val query = findViewById<EditText>(R.id.query_suggestion_query_input).text.toString()
        val radius = findViewById<EditText>(R.id.query_suggestion_radius_input).text.toString()
        val language = findViewById<EditText>(R.id.query_suggestion_language_input).text.toString()
        val locationLatitude = findViewById<EditText>(R.id.query_suggestion_location_lat_input).text.toString()
        val locationLongitude = findViewById<EditText>(R.id.query_suggestion_location_lng_input).text.toString()
        val countryCode = findViewById<EditText>(R.id.query_suggestion_country_code_input).text.toString()
        val northeastLatText = findViewById<EditText>(R.id.query_suggestion_bounds_northeast_lat_input).text.toString()
        val northeastLngText = findViewById<EditText>(R.id.query_suggestion_bounds_northeast_lng_input).text.toString()
        val southwestLatText = findViewById<EditText>(R.id.query_suggestion_bounds_southwest_lat_input).text.toString()
        val southwestLngText = findViewById<EditText>(R.id.query_suggestion_bounds_southwest_lng_input).text.toString()

        val searchFilter = SearchFilter()
        if (language.isNotEmpty()) {
            searchFilter.language = language
        }
        if (query.isNotEmpty()) {
            searchFilter.query = query
        }
        if (countryCode.isNotEmpty()) {
            searchFilter.countryCode = countryCode
        }
        val countryList: List<String> = getCountryList()
        if (countryList.isNotEmpty()) {
            searchFilter.countries = countryList
        }
        if (radius.isNotEmpty()) {
            val radiusValue: Int? = parseInt(radius)
            searchFilter.radius = radiusValue
        }

        val northeastLat = parseDouble(northeastLatText)
        val northeastLng = parseDouble(northeastLngText)
        val southwestLat = parseDouble(southwestLatText)
        val southwestLng = parseDouble(southwestLngText)
        if (northeastLat != null && northeastLng != null && southwestLat != null && southwestLng != null) {
            val northeast = Coordinate(northeastLat, northeastLng)
            val southwest = Coordinate(southwestLat, southwestLng)
            val bounds = CoordinateBounds(northeast, southwest)
            searchFilter.bounds = bounds
        }
        val lat = parseDouble(locationLatitude)
        val lng = parseDouble(locationLongitude)
        if (lat != null && lng != null) {
            searchFilter.location = Coordinate(lat, lng)
        }
        val locationTypes = locationTypes
        searchFilter.poiType = locationTypes
        searchFilter.isChildren = childrenSwitch.isChecked
        searchFilter.strictBounds = strictBoundsSwitch.isChecked
        val apiKey = apiKey
        searchIntent?.setApiKey(apiKey)
        searchIntent?.setSearchFilter(searchFilter)
        val intent = searchIntent?.getIntent(this)
        startActivityForResult(intent, SearchIntent.SEARCH_REQUEST_CODE)
    }

    fun openSearchFragment(view: View?) {
        val query = findViewById<EditText>(R.id.query_suggestion_query_input).text.toString()
        val radius = findViewById<EditText>(R.id.query_suggestion_radius_input).text.toString()
        val language = findViewById<EditText>(R.id.query_suggestion_language_input).text.toString()
        val locationLatitude = findViewById<EditText>(R.id.query_suggestion_location_lat_input).text.toString()
        val locationLongitude = findViewById<EditText>(R.id.query_suggestion_location_lng_input).text.toString()
        val countryCode = findViewById<EditText>(R.id.query_suggestion_country_code_input).text.toString()
        val northeastLatText = findViewById<EditText>(R.id.query_suggestion_bounds_northeast_lat_input).text.toString()
        val northeastLngText = findViewById<EditText>(R.id.query_suggestion_bounds_northeast_lng_input).text.toString()
        val southwestLatText = findViewById<EditText>(R.id.query_suggestion_bounds_southwest_lat_input).text.toString()
        val southwestLngText = findViewById<EditText>(R.id.query_suggestion_bounds_southwest_lng_input).text.toString()

        val intent = Intent(this, SearchFragmentActivity::class.java)
        if (language.isNotEmpty()) {
            intent.putExtra("language", language)
        }
        if (query.isNotEmpty()) {
            intent.putExtra("query", query)
        }
        if (countryCode.isNotEmpty()) {
            intent.putExtra("countryCode", countryCode)
        }
        val countryList = getCountryList()
        if (countryList.isNotEmpty()) {
            intent.putExtra("countries", countryList as Serializable?)
        }
        if (radius.isNotEmpty()) {
            intent.putExtra("radius", radius)
        }
        val northeastLat = parseDouble(northeastLatText)
        val northeastLng = parseDouble(northeastLngText)
        val southwestLat = parseDouble(southwestLatText)
        val southwestLng = parseDouble(southwestLngText)
        if (northeastLat != null && northeastLng != null && southwestLat != null && southwestLng != null) {
            val northeast = Coordinate(northeastLat, northeastLng)
            val southwest = Coordinate(southwestLat, southwestLng)
            val bounds = CoordinateBounds(northeast, southwest)
            intent.putExtra("bounds", bounds)
        }
        val lat = parseDouble(locationLatitude)
        val lng = parseDouble(locationLongitude)
        if (lat != null && lng != null) {
            intent.putExtra("location", Coordinate(lat, lng))
        }
        val locationTypes = locationTypes
        intent.putExtra("poiTypes", locationTypes as Serializable?)
        intent.putExtra("children", childrenSwitch.isChecked)
        intent.putExtra("strictBounds", strictBoundsSwitch.isChecked)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (SearchIntent.SEARCH_REQUEST_CODE == requestCode) {
            if (SearchIntent.isSuccess(resultCode)) {
                val site = searchIntent?.getSiteFromIntent(data) ?: return
                val response = StringBuilder("\n")
                val addressDetail = site.getAddress()
                val location = site.getLocation()
                val poi = site.getPoi()
                val viewport = site.getViewport()
                response.append(String.format(
                        "siteId: '%s', Name: %s, FormatAddress: %s, Country: %s, CountryCode: %s, Location: %s, PoiTypes: %s, Viewport: %s, streetNumber: %s, postalCode: %s ,tertiaryAdminArea: %s \n\n",
                        site.getSiteId(), site.getName(), site.getFormatAddress(),
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
                    if (jsonString != "null") {
                        response.append(String.format("childrenNode: %s,", jsonString))
                    }
                }
                textView.text = response.toString()
            } else {
                val searchStatus = searchIntent?.getStatusFromIntent(data)
                textView.text = "Error code: ${searchStatus?.getErrorCode()}  Error message: ${searchStatus?.getErrorMessage()}"
            }
        }
    }

    private val locationTypes: List<LocationType>?
        get() = if ((findViewById<View>(R.id.switch_query_suggestion_poitype) as Switch).isChecked) {
            locationTypeSpinner?.selectedLocationTypes
        } else {
            null
        }

    private fun getCountryList(): List<String> {
        return if (findViewById<Switch>(R.id.switch_search_fragment_countrylist).isChecked) {
            countryListSpinner!!.selectedCountryList
        } else {
            ArrayList()
        }
    }
}