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

import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.site.api.model.LocationType;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class
 */
public class Utils {

    private static final String TAG = "Utils";

    public static boolean isNumber(String string) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(string);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static Double parseDouble(String string) {
        Double doubleValue = null;
        try {
            doubleValue = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            doubleValue = null;
        }

        return doubleValue;
    }

    public static Integer parseInt(String string) {
        Integer intValue = null;
        if (TextUtils.isEmpty(string)) {
            return intValue;
        }

        try {
            intValue = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            intValue = null;
        }

        return intValue;
    }

    public static LocationType parseLocationType(String originValue) {
        LocationType locationType = null;
        try {
            locationType = LocationType.valueOf(originValue);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            locationType = null;
        }

        return locationType;
    }

    /**
     * It is recommended to save the apiKey to the server to avoid being obtained by hackers.
     * Please get the api_key from the app you created in appgallery
     * Need to encode api_key before use
     */
    public static String getApiKey() {
        // get apiKey from AppGallery Connect
        String apiKey = "your api key";

        // need encodeURI the apiKey
        try {
            String encodedApiKey = URLEncoder.encode(apiKey, "utf-8");
            return encodedApiKey;
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encode apikey error");
            return null;
        }
    }
}