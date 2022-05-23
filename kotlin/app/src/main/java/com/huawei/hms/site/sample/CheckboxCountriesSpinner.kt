/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
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
import java.util.*

class CheckboxCountriesSpinner(useCountryListSwitch: Switch, countryListTextView: TextView, countryList: List<String>) {
    companion object {
        @ColorInt
        val WHITE = -0x2f000001
    }

    private val countryListItemList: MutableList<CountryListItem>
    private val countryListTextView: TextView

    init {
        countryListItemList = ArrayList()
        for (country in countryList) {
            countryListItemList.add(CountryListItem(country))
        }
        countryListTextView.setOnClickListener { v: View ->
            if (v.isEnabled) {
                showDialog(countryListTextView.context)
            }
        }
        useCountryListSwitch.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            countryListTextView.isEnabled = isChecked
            if (!isChecked) {
                countryListTextView.text = ""
                for (placeField in countryListItemList) {
                    placeField.checked = false
                }
                return@setOnCheckedChangeListener
            }
            showDialog(buttonView.context)
        }
        this.countryListTextView = countryListTextView
    }

    fun showDialog(context: Context?) {
        val listView = ListView(context)
        val adapter = LocationTypeItemArrayAdapter(context, countryListItemList)
        listView.adapter = adapter
        listView.onItemClickListener = adapter
        val alertDialog = AlertDialog.Builder(context!!).setTitle("Country List").setPositiveButton("OK") { dialog: DialogInterface?, which: Int -> countryListTextView.text = selectedString }.setView(listView).show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(R.drawable.button_style)
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(WHITE)
    }

    val selectedCountryList: List<String>
        get() {
            val selectedList: MutableList<String> = ArrayList()
            for (countryListItem in countryListItemList) {
                if (countryListItem.checked) {
                    selectedList.add(countryListItem.item)
                }
            }
            return selectedList
        }

    val selectedString: String
        get() {
            val builder = StringBuilder()
            for (field in selectedCountryList) {
                builder.append(field).append("\n")
            }
            return builder.toString()
        }

    private class CountryListItem(val item: String) {
        var checked = false

    }

    private class LocationTypeItemArrayAdapter(context: Context?, placeFields: List<CountryListItem>?) : ArrayAdapter<CountryListItem?>(context!!, android.R.layout.simple_list_item_multiple_choice, placeFields!!), OnItemClickListener {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val countryListItem = getItem(position)
            updateView(view, countryListItem)
            return view
        }

        override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            val placeField = getItem(position)
            placeField!!.checked = !placeField.checked
            updateView(view, placeField)
        }

        private fun updateView(view: View, countryListItem: CountryListItem?) {
            if (view is CheckedTextView) {
                val checkedTextView = view
                checkedTextView.text = countryListItem!!.item
                checkedTextView.isChecked = countryListItem.checked
            }
        }
    }
}
