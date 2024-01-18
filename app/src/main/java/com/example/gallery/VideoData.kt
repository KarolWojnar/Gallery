package com.example.gallery

import android.content.Context

data class VideoData(
    val videoPath: String,
    val videoDate: Long,
    var videoIsPrivate: Boolean
): MediaData(videoPath, videoDate, videoIsPrivate) {
    companion object {
        // Funkcje do zarządzania danymi w SharedPreferences dla filmów
        private const val PREFS_NAME = "VideoPrefs"
        private const val PRIVATE_KEY_PREFIX = "private_"
        private val videoDataMap = mutableMapOf<String, VideoData>()

        fun saveVideoPrivacy(context: Context, videoPath: String, isPrivate: Boolean) {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(PRIVATE_KEY_PREFIX + videoPath, isPrivate).apply()
        }

        fun loadVideoPrivacy(context: Context, videoPath: String): Boolean {
            val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(PRIVATE_KEY_PREFIX + videoPath, false)
        }

        fun getVideoDataByPath(videoPath: String): VideoData? {
            return videoDataMap[videoPath]
        }

    }
    init {
        // Dodajemy obiekt ImageData do mapy przy tworzeniu nowej instancji
        videoDataMap[videoPath] = this
    }
}
