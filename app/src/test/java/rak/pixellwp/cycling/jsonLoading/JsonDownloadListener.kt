package rak.pixellwp.cycling.jsonLoading

import rak.pixellwp.cycling.jsonModels.ImageInfo

interface JsonDownloadListener {

    fun downloadComplete(image: ImageInfo, json: String)
}