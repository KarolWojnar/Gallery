package com.example.gallery

import ImageAdapter
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {

    private val IMAGE_PROJECTION = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DATE_TAKEN
    )

    private val VIDEO_PROJECTION = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.DATE_TAKEN
    )
    private val CHANNEL_ID = "MyNotificationChannel"
    private val NOTIFICATION_ID = 1


    override fun onResume() {
        super.onResume()
        val recyclerView = findViewById<RecyclerView>(R.id.dateRecyclerView)
        val imagesList: List<ImageData> = getAllImagesData()
        val videosList: List<VideoData> = getAllVideosData()

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val imageSizePercentage = 0.2
        val imageWidth = (screenWidth * imageSizePercentage).toInt()

        val margin = (displayMetrics.density * 4).toInt()


        val imageAdapter = ImageAdapter(imagesList, videosList, imageWidth, margin)
        recyclerView.adapter = imageAdapter
        imageAdapter.setOnItemClickListener(object : ImageAdapter.OnItemClickListener {
            override fun onItemClick(mediaData: MediaData) {
                val intent = Intent(this@MainActivity, ImagePreviewActivity::class.java)

                if (mediaData is ImageData) {
                    intent.putExtra("imagePath", mediaData.imagePath)
                } else if (mediaData is VideoData) {
                    intent.putExtra("videoPath", mediaData.videoPath)
                }
                startActivity(intent)
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        sendNotification()

        val aparatButton = findViewById<Button>(R.id.aparat_id)
        aparatButton.setOnClickListener {
            openCamera()
        }

        val privateButton = findViewById<Button>(R.id.private_button_id)
        privateButton.setOnClickListener {
            val intent = Intent(this, SecurityActivity::class.java)
            startActivity(intent)
        }


        val recyclerView = findViewById<RecyclerView>(R.id.dateRecyclerView)
        val imagesList: List<ImageData> = getAllImagesData()
        val videosList: List<VideoData> = getAllVideosData()

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val imageSizePercentage = 0.2
        val imageWidth = (screenWidth * imageSizePercentage).toInt()
        val margin = (displayMetrics.density * 4).toInt()

        val imageAdapter = ImageAdapter(imagesList, videosList, imageWidth, margin)
        recyclerView.adapter = imageAdapter

        imageAdapter.setOnItemClickListener(object : ImageAdapter.OnItemClickListener {
            override fun onItemClick(mediaData: MediaData) {
                val intent = Intent(this@MainActivity, ImagePreviewActivity::class.java)

                if (mediaData is ImageData) {
                    intent.putExtra("imagePath", mediaData.imagePath)
                    intent.putExtra("imageDate", mediaData.date)
                } else if (mediaData is VideoData) {
                    intent.putExtra("videoPath", mediaData.videoPath)
                    intent.putExtra("videoDate", mediaData.date)
                }
                startActivity(intent)
            }
        })
    }

    private fun sendNotification() {
        createNotificationChannel()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Cześć")
            .setContentText("Witaj w aplikacji Galerii!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Notification Channel"
            val descriptionText = "Channel for app notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivity(cameraIntent)
        } else {
            Toast.makeText(this, "Brak aplikacji aparatu", Toast.LENGTH_SHORT).show()
        }
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
                    fileList.add(ImageData(imagePath, imageDate, false))
                }
            }
        }

        cursor?.close()

        return fileList.filter { !it.imageIsPrivate }
    }
    private fun getAllVideosData(): List<VideoData> {
        val fileList: ArrayList<VideoData> = ArrayList()

        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            VIDEO_PROJECTION,
            null,
            null,
            "${MediaStore.Video.Media.DATE_TAKEN} DESC"
        )

        cursor?.use {
            val columnIndexData = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val columnIndexDate = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)

            while (it.moveToNext()) {
                val videoPath = it.getStringOrNull(columnIndexData)
                val videoDate = it.getLong(columnIndexDate)

                if (videoPath != null) {
                    fileList.add(VideoData(videoPath, videoDate, false))
                }
            }
        }
        cursor?.close()
        return fileList.filter { !it.videoIsPrivate }
    }
}
