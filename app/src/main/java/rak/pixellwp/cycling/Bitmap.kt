package rak.pixellwp.cycling

import android.graphics.Bitmap as BitMap
import android.os.Parcel
import android.os.Parcelable
import rak.pixellwp.cycling.jsonModels.ImgJson

class Bitmap(img: ImgJson) : Parcelable {
    val width = img.width
    val height = img.height
    private val palette = Palette(img.getParsedColors(), img.cycles)
    private val pixels = img.pixels
    private val bitmap: BitMap = BitMap.createBitmap(width,height, BitMap.Config.ARGB_8888)

    constructor(parcel: Parcel) : this(TODO("img")) {
    }

    override fun toString(): String {
        return "image with wth dimensions $width x $height = ${width*height}, ${palette.colors.size} colors, ${palette.cycles.size} cycles and ${pixels.size} pixels."
    }

    fun render() : BitMap{
        var j = 0
        for (y in 0 until height){
            for (x in 0 until width) {
                val color = palette.colors[pixels[j]]
                bitmap.setPixel(x, y, color)
                j++
            }
        }


        return bitmap
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Bitmap> {
        override fun createFromParcel(parcel: Parcel): Bitmap {
            return Bitmap(parcel)
        }

        override fun newArray(size: Int): Array<Bitmap?> {
            return arrayOfNulls(size)
        }
    }
}