package rak.pixellwp.cycling.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DropDownPreference
import com.fasterxml.jackson.module.kotlin.readValue
import rak.pixellwp.cycling.jsonModels.ImageCollection
import rak.pixellwp.cycling.jsonModels.ImageInfo
import rak.pixellwp.mapper

class ImageCollectionPreference(context: Context, attributeSet: AttributeSet) : DropDownPreference(context, attributeSet) {

    init {
        val images = loadImages().sortedBy { it.name }
        val names = images.map { image -> image.name }.toTypedArray()
        entries = names
        entryValues = names
    }

    private fun loadImages(): List<ImageCollection> {
        val json = context.assets.open("ImageCollections.json")
        return mapper.readValue(json)
    }
}