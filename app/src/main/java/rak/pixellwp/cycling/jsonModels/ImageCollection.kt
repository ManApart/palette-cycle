package rak.pixellwp.cycling.jsonModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class ImageCollection(val name: String, val images: List<ImageInfo>) {
    constructor(id: String, name: String, month: String, script: String) : this(name, listOf(ImageInfo(name, id, month = month, script = script)))

    override fun toString(): String {
        return "Collection: $name" + images.map { it.toString() }
    }
}