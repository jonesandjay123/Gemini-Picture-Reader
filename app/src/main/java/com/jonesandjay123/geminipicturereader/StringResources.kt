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
            R.string.title -> "圖片識別"
            R.string.image_selection_placeholder -> "選擇圖片"
            R.string.image_remove_button -> "移除"
            R.string.recognition_button -> "AI識別"
            R.string.en -> "EN"
            R.string.zh -> "中文"
            else -> context.resources.getString(resourceId)
        }
    }
}
