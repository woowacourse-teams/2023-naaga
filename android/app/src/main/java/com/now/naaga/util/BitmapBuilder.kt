package com.now.naaga.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface

class BitmapBuilder(
    private val imageUri: Uri,
    private val contentResolver: ContentResolver,
) {
    private var sampleSize: Int = 1
    private var isProperRotate: Boolean = false

    fun addScaling(resize: Int): BitmapBuilder {
        val options = BitmapFactory.Options()
        BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri), null, options)

        var width = options.outWidth
        var height = options.outHeight
        var sampleSize = 1
        while (true) {
            if (width / 2 < resize || height / 2 < resize) break
            width /= 2
            height /= 2
            sampleSize *= 2
        }

        this.sampleSize = sampleSize

        return this
    }

    fun setProperRotate(): BitmapBuilder {
        isProperRotate = true
        return this
    }

    fun build(): Bitmap {
        val bitmap = getBitmapFromUri(Options().apply { inSampleSize = sampleSize })
        if (isProperRotate) {
            return getRotatedBitmap(bitmap)
        }
        return bitmap
    }

    private fun getBitmapFromUri(option: Options?): Bitmap {
        return BitmapFactory.decodeStream(
            contentResolver.openInputStream(imageUri),
            null,
            option,
        ) ?: throw IllegalStateException("비트맵 생성에 실패했습니다.")
    }

    private fun getRotatedBitmap(bitmap: Bitmap): Bitmap {
        val orientation = getImageOrientation()
        if (orientation == 0) return bitmap

        val matrix = Matrix()
        matrix.setRotate(orientation.toFloat(), (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun getImageOrientation(): Int {
        val inputStream = requireNotNull(contentResolver.openInputStream(imageUri)) { "Uri로 InputStream을 여는데 실패했습니다." }
        val exif = ExifInterface(inputStream)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }
}
