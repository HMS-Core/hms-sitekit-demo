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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.DetailSearchRequest
import com.huawei.hms.site.api.model.DetailSearchResponse
import com.huawei.hms.site.api.model.SearchStatus
import com.huawei.hms.site.api.model.Site
import com.huawei.hms.site.sample.Utils.apiKey
import okhttp3.*
import java.io.IOException
import java.util.*

/**
 * Place Detail Search Example Code
 */
class DetailSearchActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "DetailSearchActivity"
    }

    private lateinit var siteIdInput: EditText
    private lateinit var languageInput: EditText
    private lateinit var resultTextView: TextView
    private lateinit var sitePhotoView: ImageView
    private var bitmap: Bitmap? = null

    // Declare a SearchService object.
    private var searchService: SearchService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_search)

        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(this, apiKey)
        siteIdInput = findViewById(R.id.edit_text_detail_search_site_id)
        languageInput = findViewById(R.id.edit_text_detail_search_language)
        findViewById<View>(R.id.button_detail_search).setOnClickListener(this)
        resultTextView = findViewById(R.id.response_detail_search)
        sitePhotoView = findViewById(R.id.search_detail_site_photo_image)
        sitePhotoView.visibility = View.INVISIBLE
    }

    override fun onClick(view: View) {
        if (view.id == R.id.button_detail_search) {
            execDetailSearch()
        }
    }

    private fun execDetailSearch() {
        val siteId = siteIdInput.text.toString()
        if (TextUtils.isEmpty(siteId) || siteId.toByteArray().size > 256) {
            resultTextView.text = "Error : Site Id is empty!"
            return
        }

        // Create a request body.
        val request = DetailSearchRequest()
        request.setSiteId(siteId)
        val language = languageInput.text.toString()
        if (!TextUtils.isEmpty(language)) {
            request.setLanguage(language)
        }
        request.isChildren = findViewById<Switch>(R.id.switch_detail_search_children).isChecked

        // Create a search result listener.
        val resultListener: SearchResultListener<DetailSearchResponse> = object : SearchResultListener<DetailSearchResponse> {
            // Return search results upon a successful search.
            override fun onSearchResult(results: DetailSearchResponse?) {
                var site: Site? = null
                if (results == null || results.getSite().also { site = it } == null) {
                    resultTextView.text = "Result is Empty!"
                    return
                }
                val addressDetail = site?.getAddress()
                val location = site?.getLocation()
                val poi = site?.getPoi()
                val viewport = site?.getViewport()
                val response = StringBuilder("\n")
                response.append("success\n")
                response.append(String.format(
                        "siteId: '%s', Name: %s, FormatAddress: %s, Country: %s, CountryCode: %s, Location: %s, PoiTypes: %s, Viewport: %s, ",
                        site?.getSiteId(), site?.getName(), site?.getFormatAddress(),
                        if (addressDetail == null) "" else addressDetail.getCountry(),
                        if (addressDetail == null) "" else addressDetail.getCountryCode(),
                        if (location == null) "" else location.getLat().toString() + "," + location.getLng(),
                        if (poi == null) "" else Arrays.toString(poi.getPoiTypes()),
                        if (viewport == null) "" else "northeast{lat=" + viewport.getNortheast().getLat().toString() + ", lng=" + viewport.getNortheast().getLng().toString() + "},"
                                + "southwest{lat=" + viewport.getSouthwest().getLat().toString() + ", lng=" + viewport.getSouthwest().getLng().toString() + "}"))

                if (poi != null) {
                    val g = Gson()
                    val jsonString = g.toJson(poi.getChildrenNodes())
                    response.append(String.format("children: %s\n\n", jsonString))
                }
                val displaySwitch = findViewById<Switch>(R.id.detail_search_photo_switch)
                val photoUrls = poi?.getPhotoUrls()
                val photoUrl = if (photoUrls != null && photoUrls.isNotEmpty()) photoUrls[0] else ""
                if (displaySwitch.isChecked && !TextUtils.isEmpty(photoUrl)) {
                    try {
                        displayPhoto(photoUrl)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        resultTextView.text = response.append("**********************")
                                .append("\r\n")
                                .append("But Get Photo Error : " + e.message)
                                .toString()
                        return
                    }
                } else {
                    sitePhotoView.setImageBitmap(null)
                }
                resultTextView.text = response.toString()
                Log.d(TAG, "onDetailSearchResult: $response")
            }

            // Return the result code and description upon a search exception.
            override fun onSearchError(status: SearchStatus) {
                resultTextView.text = "Error : ${status.getErrorCode()}  ${status.getErrorMessage()}"
            }
        }
        // Call the place details search API.
        searchService?.detailSearch(request, resultListener)
    }

    val uiHandler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            0 -> {
                sitePhotoView.setImageBitmap(bitmap)
                sitePhotoView.visibility = View.VISIBLE
            }
            1 -> {
                sitePhotoView.setImageBitmap(null)
                sitePhotoView.visibility = View.INVISIBLE
            }
            else -> {
            }
        }
        false
    })

    private fun displayPhoto(photoUrl: String) {
        val request = Request.Builder().url(photoUrl).get().build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.code() == 200) {
                    val inputStream = response.body()?.byteStream()
                    bitmap = BitmapFactory.decodeStream(inputStream)
                    uiHandler.sendEmptyMessage(0)
                } else {
                    uiHandler.sendEmptyMessage(1)
                    Log.e(TAG, "get photo from server failed. httpCode=${response.code()}")
                }
            }

            override fun onFailure(call: Call, ioe: IOException) {
                uiHandler.sendEmptyMessage(1)
                Log.e(TAG, "get photo from server failed.IOException")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        searchService = null
    }
}