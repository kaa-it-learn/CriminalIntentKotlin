package ru.tvhelp.akruglov.criminalintent

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point

fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
    var options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    val srcWidth = options.outWidth
    val srcHeight = options.outHeight

    var inSampleSize: Long = 1

    if (srcWidth > destWidth || srcHeight > destHeight) {
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth
        val scale = if (heightScale > widthScale) heightScale else widthScale
        inSampleSize = Math.round(scale.toDouble())
    }

    options = BitmapFactory.Options()
    options.inSampleSize = inSampleSize.toInt()

    return BitmapFactory.decodeFile(path, options)
}

fun getScaledBitmap(path: String, activity: Activity): Bitmap {
    val size = Point()
    activity.windowManager.defaultDisplay.getSize(size)

    return getScaledBitmap(path, size.x, size.y)
}