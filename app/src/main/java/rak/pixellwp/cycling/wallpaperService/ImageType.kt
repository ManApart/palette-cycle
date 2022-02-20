package rak.pixellwp.cycling.wallpaperService

import rak.pixellwp.cycling.IMAGE_COLLECTION
import rak.pixellwp.cycling.TIMELINE_IMAGE

enum class ImageType(val stringValue: String) {
    TIMELINE(TIMELINE_IMAGE), COLLECTION(IMAGE_COLLECTION)
}

fun String?.toImageType(): ImageType {
    return ImageType.values().firstOrNull { it.stringValue == this } ?: ImageType.TIMELINE
}