package rak.pixellwp.cycling.jsonLoading

import android.os.AsyncTask
import android.util.Log
import kotlinx.coroutines.*
import rak.pixellwp.cycling.jsonModels.ImageInfo
import java.net.URL


class JsonDownloader(private val image: ImageInfo, private val listener: (ImageInfo, String) -> Unit) {
    private val imageUrl = "http://www.effectgames.com/demos/canvascycle/image.php?file="
    private val timelineImageUrl = "http://www.effectgames.com/demos/worlds/scene.php?file="
    private val logTag = "JsonDownloader"
    fun download() {
        val json = runBlocking {
            withContext(Dispatchers.Default) {
                downloadImage()
            }
        }
        completeDownload(json)
    }

    private fun downloadImage(): String {
        var json = ""
        try {
            val inputStream = URL(getFullUrl(image)).openStream()

            val s = java.util.Scanner(inputStream).useDelimiter("\\A")
            json = if (s.hasNext()) s.next() else ""

            inputStream.close()
            return json
        } catch (e: Exception) {
            Log.e(logTag, "Unable to download image from ${getFullUrl(image)}")
            e.printStackTrace()
        }
        return json
    }

    private fun completeDownload(result: String?) {
        val json = cleanJson(result)
        val jsonSample = if (json.length > 100) "${json.substring(0, 100)} ... ${json.substring(json.length - 100)}" else json
        Log.d(logTag, "downloaded json for ${image.name} from ${getFullUrl(image)} to ${image.getFileName()}: $jsonSample")
        listener(image, json)
    }

    private fun cleanJson(json: String?): String {
        return if (image.isTimeline) {
            cleanTimelineImageJson(json)
        } else {
            cleanImageJson(json)
        }
    }

    private fun cleanImageJson(json: String?): String {
        if (json == null) return ""
        if (json.length > 25) {
            val start = json.indexOf("{filename")
            return json.substring(start, json.length - 4)
        }
        return json
    }
    private fun cleanTimelineImageJson(json: String?): String {
        if (json == null) return ""
        if (json.length > 25) {
            val start = json.indexOf("{base")
            return json.substring(start, json.length - 3)
        }
        return json
    }

    private fun getFullUrl(image: ImageInfo) : String {
        return if (image.isTimeline){
            timelineImageUrl + image.getJustId() + "&month=" + image.month + "&script=" + image.script
        } else {
            imageUrl + image.id
        }
    }

}