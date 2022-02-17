package rak.pixellwp.cycling.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DropDownPreference
import com.fasterxml.jackson.module.kotlin.readValue
import rak.pixellwp.cycling.jsonModels.ImageInfo
import rak.pixellwp.mapper

class ImagePreference(context: Context, attributeSet: AttributeSet) : DropDownPreference(context, attributeSet) {

    init {
        val images = loadImages()
        entries = images.map { image -> image.name }.toTypedArray()
        entryValues = images.map { image -> image.id }.toTypedArray()
    }

    private fun loadImages(): List<ImageInfo> {
        val json = context.assets.open("Images.json")
        return mapper.readValue(json)
    }
}