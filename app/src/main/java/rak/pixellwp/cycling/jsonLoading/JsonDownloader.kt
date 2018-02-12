package rak.pixellwp.cycling.jsonLoading

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import rak.pixellwp.cycling.jsonModels.ImageInfo
import java.io.OutputStreamWriter
import java.net.URL


class JsonDownloader(private val image: ImageInfo, private val context: Context, private val listener: JsonDownloadListener) : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg params: String?): String {
        return downloadImage()
    }

    private fun downloadImage() : String {
        var json = ""
        try {
            val inputStream = URL(image.url).openStream()

            val s = java.util.Scanner(inputStream).useDelimiter("\\A")
            json = if (s.hasNext()) s.next() else ""

            inputStream.close()
            return json
        } catch (e: Exception){
            Log.e("Image Loader", "Unable to download image from ${image.url}")
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
            Log.e("Image Loader", "Unable to save image")
            e.printStackTrace()
        }
        Log.d("Image Loader", "saved json from ${image.url} to ${image.fileName}: ${json.substring(0, 100)} ... ${json.substring(json.length - 100)}")
        listener.downloadComplete(image)
    }
}