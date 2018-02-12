package rak.pixellwp.cycling

import rak.pixellwp.cycling.jsonModels.ImageInfo

interface JsonDownloadListener {

    fun downloadComplete(image: ImageInfo)
}