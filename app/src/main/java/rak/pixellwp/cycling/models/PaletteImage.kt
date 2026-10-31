package rak.pixellwp.cycling.models

import android.graphics.Bitmap

interface PaletteImage {
    fun advance(timePassed: Long)
    fun getBitmap() : Bitmap
    fun getImageWidth() : Int
    fun getImageHeight() : Int
}
