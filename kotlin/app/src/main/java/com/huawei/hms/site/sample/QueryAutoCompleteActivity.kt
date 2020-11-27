package com.huawei.hms.site.sample

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.Coordinate
import com.huawei.hms.site.api.model.QueryAutocompleteRequest
import com.huawei.hms.site.api.model.QueryAutocompleteResponse
import com.huawei.hms.site.api.model.SearchStatus
import com.huawei.hms.site.sample.Utils.apiKey
import com.huawei.hms.site.sample.Utils.parseDouble
import com.huawei.hms.site.sample.Utils.parseInt
import java.util.*

/**
 * Query AutoComplete Example Code
 */
class QueryAutoCompleteActivity : AppCompatActivity(), View.OnClickListener {

    // Declare a SearchService object.
    private var searchService: SearchService? = null
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query_auto_complete)

        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(this, apiKey)
        findViewById<View>(R.id.search_query_auto_complete_button).setOnClickListener(this)
        resultText = findViewById(R.id.response_text_search)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.search_query_auto_complete_button) {
            queryAutoComplete()
        }
    }

    private fun queryAutoComplete() {
        // Create a request body.
        val request = QueryAutocompleteRequest()
        val query = findViewById<TextView>(R.id.query_suggestion_query_input).text.toString()
        val radius = findViewById<TextView>(R.id.query_suggestion_radius_input).text.toString()
        val language = findViewById<TextView>(R.id.query_suggestion_language_input).text.toString()
        val locationLatitude = findViewById<TextView>(R.id.query_suggestion_location_lat_input).text.toString()
        val locationLongitude = findViewById<TextView>(R.id.query_suggestion_location_lng_input).text.toString()
        if (!TextUtils.isEmpty(language)) {
            request.setLanguage(language)
        }
        if (!TextUtils.isEmpty(query)) {
            request.setQuery(query)
        }
        val radiusInt: Int? = parseInt(radius)
        if (radiusInt == null || radiusInt <= 0) {
            resultText.text = "Error : Radius Must be greater than 0 !"
            return
        }
        request.setRadius(radiusInt)
        val lat = parseDouble(locationLatitude)
        val lng = parseDouble(locationLongitude)
        if (lat != null && lng != null) {
            request.setLocation(Coordinate(lat, lng))
        }
        request.isChildren = (findViewById<View>(R.id.switch_query_auto_complete_children) as Switch).isChecked

        // Create a search result listener.
        val resultListener: SearchResultListener<QueryAutocompleteResponse?> = object : SearchResultListener<QueryAutocompleteResponse?> {
            // Return search results upon a successful search.
            override fun onSearchResult(results: QueryAutocompleteResponse?) {
                val stringBuilder = StringBuilder()
                if (results != null) {
                    val predictions = results.getPredictions()
                    if (predictions != null && predictions.isNotEmpty()) {
                        stringBuilder.append("AutoCompletePrediction[ ]:\n")
                        var count = 1
                        for (mPrediction in predictions) {
                            stringBuilder.append(String.format("[%s] Prediction,description = %s ,", "" + count++, mPrediction.getDescription()))

                            val matchedKeywords = mPrediction.getMatchedKeywords()
                            for (matchedKeyword in matchedKeywords) {
                                stringBuilder.append("matchedKeywords: $matchedKeyword")
                            }

                            val matchedWords = mPrediction.getMatchedWords()
                            for (matchedWord in matchedWords) {
                                stringBuilder.append(",matchedWords: $matchedWord")
                            }

                            stringBuilder.append("\n")
                        }
                    } else {
                        stringBuilder.append("Predictions 0 results")
                    }

                    stringBuilder.append("\n\nSite[ ]:\n")

                    val sites = results.getSites()
                    if (sites != null && sites.isNotEmpty()) {
                        var count = 1
                        for (site in sites) {
                            val addressDetail = site.getAddress()
                            val location = site.getLocation()
                            val poi = site.getPoi()
                            val viewport = site.getViewport()
                            stringBuilder.append(String.format(
                                    "[%s] siteId: '%s', name: %s, formatAddress: %s, utcOffset: %s, country: %s, countryCode: %s, location: %s, distance: %s, poiTypes: %s, viewport is %s, streetNumber: %s, postalCode: %s , tertiaryAdminArea: %s, ",
                                    "" + count++, site.getSiteId(), site.getName(), site.getFormatAddress(), site.getUtcOffset(),
                                    if (addressDetail == null) "" else addressDetail.getCountry(),
                                    if (addressDetail == null) "" else addressDetail.getCountryCode(),
                                    if (location == null) "" else location.getLat().toString() + "," + location.getLng(), site.getDistance(),
                                    if (poi == null) "" else Arrays.toString(poi.getPoiTypes()),
                                    if (viewport == null) "" else "northeast{lat=" + viewport.getNortheast().getLat().toString() + ", lng=" + viewport.getNortheast().getLng().toString() + "},"
                                            + "southwest{lat=" + viewport.getSouthwest().getLat().toString() + ", lng=" + viewport.getSouthwest().getLng().toString() + "}",
                                    if (addressDetail == null) "" else addressDetail.getStreetNumber(),
                                    if (addressDetail == null) "" else addressDetail.getPostalCode(),
                                    if (addressDetail == null) "" else addressDetail.getTertiaryAdminArea()))
                            if (poi != null) {
                                val g = Gson()
                                val jsonString = g.toJson(poi.getChildrenNodes())
                                stringBuilder.append(String.format("childrenNode: %s \n\n", jsonString))
                            }
                        }
                    } else {
                        stringBuilder.append("sites 0 results\n")
                    }
                }
                resultText.text = stringBuilder
            }

            // Return the result code and description upon a search exception.
            override fun onSearchError(status: SearchStatus) {
                resultText.text = "onSearchError errorCode: ${status.getErrorCode()}  ${status.getErrorMessage()}"
            }
        }
        // Call the nearby place search API.
        searchService?.queryAutocomplete(request, resultListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchService = null
    }
}