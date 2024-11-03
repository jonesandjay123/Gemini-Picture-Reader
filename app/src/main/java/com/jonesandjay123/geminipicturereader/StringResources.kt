package com.jonesandjay123.geminipicturereader

import android.content.Context

class StringResources(private val context: Context) {
    fun getString(resourceId: Int, language: String): String {
        return when (language) {
            "EN" -> context.getString(resourceId)
            "中文" -> getChineseString(resourceId)
            else -> context.getString(resourceId)
        }
    }

    private fun getChineseString(resourceId: Int): String {
        return when (resourceId) {
            R.string.title -> "圖片識別"
            R.string.image_selection_placeholder -> "選擇圖片"
            R.string.image_remove_button -> "移除"
            R.string.select_category -> "選擇類別"
            else -> context.getString(resourceId)
        }
    }
}
