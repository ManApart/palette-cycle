package rak.pixellwp.cycling.jsonLoading

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import rak.pixellwp.cycling.jsonModels.ImageInfo
import java.io.OutputStreamWriter
import java.net.URL


class JsonDownloader(private val image: ImageInfo, private val context: Context, private val listener: JsonDownloadListener) : AsyncTask<String, Void, String>() {
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
            saveImage(cleanJson(result))
        }
        super.onPostExecute(result)
    }

    private fun cleanJson(json: String) : String{
        return json.substring(25, json.length-4)
    }

    private fun saveImage(json: String){
        try {
            val stream = OutputStreamWriter(context.openFileOutput(image.fileName, Context.MODE_PRIVATE))
            stream.write(json)
            stream.close()
        } catch (e: Exception){
            Log.e(logTag, "Unable to save image")
            e.printStackTrace()
        }
        Log.d(logTag, "saved json from ${baseUrl + image.id} to ${image.fileName}: ${json.substring(0, 100)} ... ${json.substring(json.length - 100)}")
        listener.downloadComplete(image)
    }
}