package rak.pixellwp.cycling.jsonLoading

import android.os.AsyncTask
import android.util.Log
import rak.pixellwp.cycling.jsonModels.ImageInfo
import java.net.URL


class JsonDownloader(private val image: ImageInfo, private val listener: JsonDownloadListener) : AsyncTask<String, Void, String>() {
    private val baseUrl = "http://www.effectgames.com/demos/canvascycle/image.php?file="
    private val logTag = "JsonDownloader"
    override fun doInBackground(vararg params: String?): String {
        return downloadImage()
    }

    private fun downloadImage() : String {
        var json = ""
        try {
            val inputStream = URL(baseUrl + image.id).openStream()

            val s = java.util.Scanner(inputStream).useDelimiter("\\A")
            json = if (s.hasNext()) s.next() else ""

            inputStream.close()
            return json
        } catch (e: Exception){
            Log.e(logTag, "Unable to download image from ${baseUrl + image.id}")
            e.printStackTrace()
        }
        return json
    }

    override fun onPostExecute(result: String?) {
        if (result != null) {
            val json = cleanJson(result)
            Log.d(logTag, "downloaded json from ${baseUrl + image.id} to ${image.fileName}: ${json.substring(0, 100)} ... ${json.substring(json.length - 100)}")
            listener.downloadComplete(image, json)
        }
        super.onPostExecute(result)
    }

    private fun cleanJson(json: String) : String{
        return json.substring(25, json.length-4)
    }

}