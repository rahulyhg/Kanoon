package com.rvsoftlab.kanoon.helper

import android.graphics.*
import android.graphics.Paint.FILTER_BITMAP_FLAG
import java.io.ByteArrayOutputStream


fun Bitmap.convertToByteArray():ByteArray{
    val maxSize = 500
    var width = this.width
    var height = this.height
    val bitmapRatio = width.toFloat() / height.toFloat()
    if (bitmapRatio > 1) {
        width = maxSize
        height = ((width / bitmapRatio).toInt())
    } else {
        height = maxSize
        width = ((height * bitmapRatio).toInt())
    }

    val bmp:Bitmap = Bitmap.createScaledBitmap(this, width, height, true)

    val out = ByteArrayOutputStream()
    bmp.compress(Bitmap.CompressFormat.PNG,100,out)
    return out.toByteArray()

}

fun calculateInSampleSize(options:BitmapFactory.Options , reqWidth:Int, reqHeight:Int):Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val heightRatio = Math.round(height.toFloat() / reqHeight as Float)
        val widthRatio = Math.round(width.toFloat() / reqWidth as Float)
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = (width * height).toFloat()
    val totalReqPixelsCap = reqWidth * reqHeight * 2

    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++
    }

    return inSampleSize
}
