package com.example.gallery

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import android.Manifest
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {

            val image_id = findViewById<ImageView>(R.id.image_id)
            val bPrev_id = findViewById<Button>(R.id.bPrev_id)
            val bNext_id = findViewById<Button>(R.id.bNext_id)

            val allImagesPaths = getAllImages()

            var currImgIndex = 0

            image_id.setImageURI(Uri.parse(allImagesPaths.get(currImgIndex)))

            bPrev_id.setOnClickListener {
                image_id.setImageURI(Uri.parse(allImagesPaths.get(currImgIndex - 1)))
            }
            bNext_id.setOnClickListener {
                image_id.setImageURI(Uri.parse(allImagesPaths.get(currImgIndex + 1)))
            }
        }
    }

    fun getAllImages():ArrayList<String>{
        val fileList: ArrayList<String> = ArrayList()

        val path: String = Environment.getExternalStorageDirectory().absolutePath

        File(path).walk().forEach {
            if (it.toString().endsWith(".jpg"))
                fileList.add(it.toString())
        }
        return fileList
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val allImagesPaths = getAllImages()
                // ...
            } else {
                // Uprawnienie zostało odrzucone, możesz obsłużyć to zdarzenie odpowiednio
            }
        }
    }
}
