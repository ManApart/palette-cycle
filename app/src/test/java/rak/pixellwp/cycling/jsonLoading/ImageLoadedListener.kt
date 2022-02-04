package rak.pixellwp.cycling.jsonLoading

import rak.pixellwp.cycling.jsonModels.ImageInfo

interface ImageLoadedListener {

    fun imageLoadComplete(image: ImageInfo)
}