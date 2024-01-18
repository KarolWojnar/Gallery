package com.example.gallery

class ImageData(
    val imagePath: String,
    val imageDate: Long,
    val imageIsPrivate: Boolean = false,
) : MediaData(imagePath, imageDate, imageIsPrivate)