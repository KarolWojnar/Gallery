package com.example.gallery

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {

    private val galleryImageView: ImageView by lazy { findViewById<ImageView>(R.id.image_id) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        val allImagesPaths = getAllImages()

        if (allImagesPaths.isNotEmpty()) {
            galleryImageView.setImageURI(Uri.parse(allImagesPaths[1]))
        }
    }
    private fun getAllImages(): List<String> {
        val images = mutableListOf<String>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val data = cursor.getString(dataColumn)
                images.add(data)
            }
        }
        return images
    }

}
