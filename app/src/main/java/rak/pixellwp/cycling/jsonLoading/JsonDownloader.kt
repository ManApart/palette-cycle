package rak.pixellwp.cycling.jsonLoading

import android.util.Log

import rak.pixellwp.cycling.jsonModels.ImageInfo

import java.net.URL
import java.util.zip.GZIPInputStream



class JsonDownloader(
    private val image: ImageInfo,
    private val downloading: MutableSet<ImageInfo>,
    private val listener: (ImageInfo, String) -> Unit
) {
    private val imageUrl = "http://www.effectgames.com/demos/canvascycle/image.php?file="
    private val timelineImageUrl = "http://www.effectgames.com/demos/worlds/scene.php?file="
    private val logTag = "JsonDownloader"

    fun download() {
        completeDownload(downloadImage())
    }

    private fun downloadImage(): String? {
        try {
            val urlConnection = URL(getFullUrl(image)).openConnection()
            urlConnection.setRequestProperty("Accept-Encoding", "gzip")
            val inputStream = GZIPInputStream(urlConnection.inputStream)

            val s = java.util.Scanner(inputStream).useDelimiter("\\A")
            val json = if (s.hasNext()) s.next() else ""

            inputStream.close()
            return json
        } catch (e: Exception) {
            Log.e(logTag, "Unable to download image from ${getFullUrl(image)}")
            e.printStackTrace()
            downloading.remove(image)
        }
        return null
    }

    private fun completeDownload(result: String?) {
        if (result != null) {
            try {
                val json = cleanJson(result)
                val jsonSample = json.getSample()
                Log.d(logTag, "downloaded json for ${image.name} from ${getFullUrl(image)} to ${image.getFileName()}: $jsonSample")
                listener(image, json)
            } catch (e: Exception) {
                Log.e(logTag, "Unable to download image from ${getFullUrl(image)}")
                e.printStackTrace()
                downloading.remove(image)
            }
        }
    }

    private fun cleanJson(json: String): String {
        return if (image.isTimeline) {
            cleanTimelineImageJson(json)
        } else {
            cleanImageJson(json)
        }
    }

    private fun cleanImageJson(json: String): String {
        if (json.length > 25) {
            val start = json.indexOf("{filename")
            return json.substring(start, json.length - 4)
        }
        return json
    }

    private fun cleanTimelineImageJson(json: String): String {
        if (json.length > 25) {
            val start = json.indexOf("{base")
            return json.substring(start, json.length - 3)
        }
        return json
    }

    private fun getFullUrl(image: ImageInfo): String {
        return if (image.isTimeline) {
            timelineImageUrl + image.getJustId() + "&month=" + image.month + "&script=" + image.script
        } else {
            imageUrl + image.id
        }
    }

    private fun String.getSample(): String {
        return if (length > 100) "${substring(0, 100)} ... ${substring(length - 100)}" else this
    }

}