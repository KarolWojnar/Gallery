package com.example.gallery

import android.content.Context

data class ImageData(
    val imagePath: String,
    val imageDate: Long,
    var imageIsPrivate: Boolean
) : MediaData(imagePath, imageDate, imageIsPrivate)
{



    companion object {
        // Funkcje do zarządzania danymi w SharedPreferences dla zdjęć
        private const val PREFS_NAME = "ImagePrefs"
        private const val PRIVATE_KEY_PREFIX = "private_"
        private val imageDataMap = mutableMapOf<String, ImageData>()

        fun saveImagePrivacy(context: Context, imagePath: String, isPrivate: Boolean) {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(PRIVATE_KEY_PREFIX + imagePath, isPrivate).apply()
        }

        fun loadImagePrivacy(context: Context, imagePath: String): Boolean {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(PRIVATE_KEY_PREFIX + imagePath, false)
        }
        fun getImageDataByPath(imagePath: String): ImageData? {
            return imageDataMap[imagePath]
        }
    }
    init {
        // Dodajemy obiekt ImageData do mapy przy tworzeniu nowej instancji
        imageDataMap[imagePath] = this
    }
}