package com.example.gallery

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class ImagePreviewActivity : AppCompatActivity() {

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


        // Obsługa przycisków
        backButton.setOnClickListener {
            finish() // Zamknięcie aktywności podglądu
        }

        deleteButton.setOnClickListener {
            // Implementacja usuwania zdjęcia
            // Możesz użyć imagePath do zlokalizowania pliku i go usunąć
        }

        addToPrivateButton.setOnClickListener {
            // Implementacja dodawania do prywatnych
        }
    }
}