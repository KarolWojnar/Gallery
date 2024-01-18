package com.example.gallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ImagePreviewActivity : AppCompatActivity() {

    private var isVideoPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        val imagePath = intent.getStringExtra("imagePath")
        val videoPath = intent.getStringExtra("videoPath")

        val imageView = findViewById<ImageView>(R.id.imagePreview)
        val videoView = findViewById<VideoView>(R.id.videoView)

        val backButton = findViewById<Button>(R.id.backBtn_id)
        val deleteButton = findViewById<Button>(R.id.deleteBtn_id)
        val addToPrivateButton = findViewById<Button>(R.id.toPrivateBtn_id)
        addToPrivateButton.text = intent.getStringExtra("buttonName")

        if (imagePath != null) {
            videoView.visibility = View.GONE
            imageView.visibility = View.VISIBLE

            imageView.setImageURI(Uri.parse(imagePath))
        } else {
            imageView.visibility = View.GONE
            videoView.visibility = View.VISIBLE

            videoView.setVideoURI(Uri.parse(videoPath))
            videoView.start()
        }

        videoView.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
                isVideoPaused = true
            } else {
                videoView.start()
                isVideoPaused = false
            }
        }

        // Obsługa przycisków
        backButton.setOnClickListener {
            finish() // Zamknięcie aktywności podglądu
        }

        deleteButton.setOnClickListener {
            val mediaPath = imagePath ?: videoPath
            if (mediaPath != null) {
                val mediaFile = File(mediaPath)
                if (mediaFile.exists()) {
                    val deleted = mediaFile.delete()
                    if (deleted) {
                        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mediaFile)))
                        if (addToPrivateButton.text.equals("Usuń z prywatnych")) {
                            startActivity(Intent(this, PrivateActivity::class.java))
                        } else
                            startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "Błąd podczas usuwania pliku", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Plik nie istnieje", Toast.LENGTH_SHORT).show()
                }
            }
        }

        addToPrivateButton.setOnClickListener {
            if (videoPath != null) {
                val videoMedia = VideoData.getVideoDataByPath(videoPath)
                if (videoMedia != null) {
                    if (addToPrivateButton.text == "Usuń z prywatnych") {
                        videoMedia.videoIsPrivate = false
                        VideoData.saveVideoPrivacy(this, videoPath, false)
                        Toast.makeText(this, "Film został usunięty z prywatnych!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, PrivateActivity::class.java))
                    } else {
                        videoMedia.videoIsPrivate = true
                        VideoData.saveVideoPrivacy(this, videoPath, true)
                        Toast.makeText(this, "Film został dodany do prywatnych!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }
            } else if (imagePath != null) {
                val imageMedia = ImageData.getImageDataByPath(imagePath)
                if (imageMedia != null) {
                    if (addToPrivateButton.text == "Usuń z prywatnych") {
                        imageMedia.imageIsPrivate = false
                        ImageData.saveImagePrivacy(this, imagePath, false)
                        Toast.makeText(this, "Zdjęcie zostało usunięte z prywatnych!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, PrivateActivity::class.java))
                    } else {
                        imageMedia.imageIsPrivate = true
                        ImageData.saveImagePrivacy(this, imagePath, true)
                        Toast.makeText(this, "Zdjęcie zostało dodane do prywatnych!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }
            }
        }
    }
}