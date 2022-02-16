package rak.pixellwp.cycling.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DropDownPreference
import androidx.preference.ListPreference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import rak.pixellwp.cycling.jsonModels.ImageInfo

class ImagePreference(context: Context, attributeSet: AttributeSet) : DropDownPreference(context, attributeSet) {

    init {
        val images = loadImages()
        entries = images.map { image -> image.name }.toTypedArray()
        entryValues = images.map { image -> image.id }.toTypedArray()
    }

    private fun loadImages(): List<ImageInfo> {
        val json = context.assets.open("Images.json")
        return jacksonObjectMapper().readValue(json)
    }
}