package com.example.gallery

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ImagePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        val imagePath = intent.getStringExtra("imagePath")
        val imageView = findViewById<ImageView>(R.id.imagePreview)
        val backButton = findViewById<Button>(R.id.backBtn_id)
        val deleteButton = findViewById<Button>(R.id.deleteBtn_id)
        val addToPrivateButton = findViewById<Button>(R.id.toPrivateBtn_id)

        // Ustawienie obrazu
        // Tutaj możesz użyć biblioteki do ładowania obrazów, np. Picasso lub Glide
        // W tym przykładzie używam tylko ImageView do celów demonstracyjnych
        imageView.setImageURI(Uri.parse(imagePath))

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