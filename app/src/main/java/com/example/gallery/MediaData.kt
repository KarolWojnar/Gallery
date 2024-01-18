package com.example.gallery

import java.lang.reflect.Constructor

open class MediaData(
    val path: String,
    val date: Long,
    var isPrivate: Boolean
) {
}
