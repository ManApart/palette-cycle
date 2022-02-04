package rak.pixellwp.cycling.jsonModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class ImageCollection(val name: String, val images: List<ImageInfo>) {
    override fun toString(): String {
        return "Collection: $name" + images.map{it.toString()}
    }
}