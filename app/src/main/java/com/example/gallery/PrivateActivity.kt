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

class PrivateActivity : ComponentActivity() {
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
                val intent = Intent(this@PrivateActivity, ImagePreviewActivity::class.java)

                intent.putExtra("buttonName", "Usuń z prywatnych")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private)

        val aparatButton = findViewById<Button>(R.id.aparat_id)
        aparatButton.setOnClickListener {
            openCamera()
        }

        val mainButton = findViewById<Button>(R.id.photos_button_id)
        mainButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.dateRecyclerView)
        val imagesList: List<ImageData> = getAllImagesData()
        val videosList: List<VideoData> = getAllVideosData()

        sendNotification(imagesList, videosList)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val imageSizePercentage = 0.2
        val imageWidth = (screenWidth * imageSizePercentage).toInt()
        val margin = (displayMetrics.density * 4).toInt()

        val imageAdapter = ImageAdapter(imagesList, videosList, imageWidth, margin)
        recyclerView.adapter = imageAdapter

        imageAdapter.setOnItemClickListener(object : ImageAdapter.OnItemClickListener {
            override fun onItemClick(mediaData: MediaData) {
                val intent = Intent(this@PrivateActivity, ImagePreviewActivity::class.java)
                intent.putExtra("buttonName", "Usuń z prywatnych")
                if (mediaData is ImageData) {
                    intent.putExtra("imagePath", mediaData.imagePath)
                    intent.putExtra("imageDate", mediaData.imageDate)
                } else if (mediaData is VideoData) {
                    intent.putExtra("videoPath", mediaData.videoPath)
                    intent.putExtra("videoDate", mediaData.videoDate)
                }
                startActivity(intent)
            }
        })
    }

    private fun sendNotification(imagesList: List<ImageData>, videosList: List<VideoData>) {
        createNotificationChannel()

        val totalImages = imagesList.size
        val totalVideos = videosList.size
        val totalMedia = totalImages + totalVideos

        val notificationText = "Liczba zdjęć: $totalImages, Liczba wideo: $totalVideos, Razem: $totalMedia"

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Cześć")
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
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
                    val isPrivate = ImageData.loadImagePrivacy(this, imagePath)
                    if (isPrivate) {
                        fileList.add(ImageData(imagePath, imageDate, true))
                    }
                }
            }
        }

        cursor?.close()

        return fileList
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
                    val isPrivate = VideoData.loadVideoPrivacy(this, videoPath)
                    if (isPrivate) {
                        fileList.add(VideoData(videoPath, videoDate, true))
                    }
                }
            }
        }
        cursor?.close()
        return fileList
    }
}
