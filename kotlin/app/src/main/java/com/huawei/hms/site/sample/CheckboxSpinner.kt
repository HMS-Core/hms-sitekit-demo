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

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import com.huawei.hms.site.api.model.LocationType
import java.util.*

class CheckboxSpinner(usePOITypeSwitch: Switch, poiTypeTextView: TextView, poiTypeList: List<LocationType>) {
    private val locationTypeItemList: MutableList<LocationTypeItem>
    private val poiTypeTextView: TextView

    private fun showDialog(context: Context?) {
        val listView = ListView(context)
        val adapter = LocationTypeItemArrayAdapter(context, locationTypeItemList)
        listView.adapter = adapter
        listView.onItemClickListener = adapter
        val alertDialog = AlertDialog.Builder(context!!).setTitle("POI Types").setPositiveButton("OK")
        { _: DialogInterface?, _: Int -> poiTypeTextView.text = selectedString }
                .setView(listView)
                .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(R.drawable.button_style)
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(WHITE)
    }

    val selectedLocationTypes: List<LocationType>
        get() {
            val selectedList: MutableList<LocationType> = ArrayList()
            for (placeField in locationTypeItemList) {
                if (placeField.checked) {
                    selectedList.add(placeField.item)
                }
            }
            return selectedList
        }

    private val selectedString: String
        get() {
            val builder = StringBuilder()
            for (field in selectedLocationTypes) {
                builder.append(field).append("\n")
            }
            return builder.toString()
        }

    private class LocationTypeItem(val item: LocationType) {
        var checked = false

    }

    private class LocationTypeItemArrayAdapter(context: Context?, placeFields: List<LocationTypeItem>?) : ArrayAdapter<LocationTypeItem?>(context!!, android.R.layout.simple_list_item_multiple_choice, placeFields!!), OnItemClickListener {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val locationTypeItem = getItem(position)
            updateView(view, locationTypeItem)
            return view
        }

        override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            val placeField = getItem(position)
            placeField!!.checked = !placeField.checked
            updateView(view, placeField)
        }

        private fun updateView(view: View, locationTypeItem: LocationTypeItem?) {
            if (view is CheckedTextView) {
                view.text = locationTypeItem!!.item.toString()
                view.isChecked = locationTypeItem.checked
            }
        }
    }

    companion object {
        @ColorInt
        val WHITE = -0x2f000001
    }

    init {
        locationTypeItemList = ArrayList()
        for (poiType in poiTypeList) {
            locationTypeItemList.add(LocationTypeItem(poiType))
        }
        poiTypeTextView.setOnClickListener { v: View ->
            if (v.isEnabled) {
                showDialog(poiTypeTextView.context)
            }
        }
        usePOITypeSwitch.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            poiTypeTextView.isEnabled = isChecked
            if (!isChecked) {
                poiTypeTextView.text = ""
                for (placeField in locationTypeItemList) {
                    placeField.checked = false
                }
                return@setOnCheckedChangeListener
            }
            showDialog(buttonView.context)
        }
        this.poiTypeTextView = poiTypeTextView
    }
}
