package com.websarva.wings.android.ocrsample

import android.os.Build
import android.provider.MediaStore

class readtest {
    val targetUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val projection: Array<String> = arrayOf(
        MediaStore.Images.Media._ID
    )

    context.applicationContext.contentResolver.query(
    targetUri,
    projection,
    null,
    null,
    null
    )?.use {
        val items = ArrayList<String>(it.count)
        val idIndex = it.getColumnIndex(MediaStore.Images.Media._ID)

        it.moveToFirst()
        while (it.isAfterLast.not()) {
            val imageUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                it.getLong(idIndex)
            )
            items.add(imageUri.toString())
            it.moveToNext()
        }
        return items
    }
}