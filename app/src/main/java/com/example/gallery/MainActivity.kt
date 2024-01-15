package com.example.gallery

import ImageAdapter
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private val IMAGE_PROJECTION = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DATE_TAKEN
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        val dateRecyclerView = findViewById<RecyclerView>(R.id.dateRecyclerView)
        val imageRecyclerView = findViewById<RecyclerView>(R.id.imageRecyclerView)
        val imagesList: List<ImageData> = getAllImagesData()

        // Konwertuj listę ImageData na mapę, gdzie kluczem jest data w formacie "dd.MM.yyyy"
        val imagesMap = imagesList.groupBy {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(it.imageDate))
        }

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val imageSizePercentage = 0.2
        val imageWidth = (screenWidth * imageSizePercentage).toInt()

        // Dodaj marginesy w pikselach
        val margin = (displayMetrics.density * 4).toInt()

        // Przekazujesz teraz imagesMap do ImageAdapter
        imageRecyclerView.adapter = ImageAdapter(imagesMap, imageWidth, margin)
    }

    private fun getAllImagesData(): List<ImageData> {
        val fileList: ArrayList<ImageData> = ArrayList()

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            IMAGE_PROJECTION,
            null,
            null,
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )

        cursor?.use {
            val columnIndexData = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val columnIndexDate = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

            while (it.moveToNext()) {
                val imagePath = it.getStringOrNull(columnIndexData)
                val imageDate = it.getLong(columnIndexDate)

                if (imagePath != null) {
                    fileList.add(ImageData(imagePath, imageDate))
                }
            }
        }

        cursor?.close()

        return fileList
    }
}
