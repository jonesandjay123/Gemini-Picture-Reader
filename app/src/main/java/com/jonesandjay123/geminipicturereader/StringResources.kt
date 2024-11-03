package com.jonesandjay123.geminipicturereader

import android.content.Context

class StringResources(private val context: Context) {
    fun getString(resourceId: Int, language: String): String {
        return when (language) {
            "EN" -> context.resources.getString(resourceId)
            "中文" -> getChineseString(resourceId)
            else -> context.resources.getString(resourceId)
        }
    }

    private fun getChineseString(resourceId: Int): String {
        return when (resourceId) {
            R.string.title -> "圖片識別畫面"
            R.string.language_label -> "語言"
            R.string.image_selection_placeholder -> "圖片選擇佔位符"
            R.string.input_field_placeholder -> "輸入欄位佔位符"
            R.string.button_placeholder -> "按鈕佔位符"
            R.string.result_placeholder -> "結果佔位符"
            R.string.en -> "EN"
            R.string.zh -> "中文"
            else -> context.resources.getString(resourceId)
        }
    }
}
