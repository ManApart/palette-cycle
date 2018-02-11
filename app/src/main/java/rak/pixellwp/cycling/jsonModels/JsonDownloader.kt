package rak.pixellwp.cycling.jsonModels

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import java.io.OutputStreamWriter
import java.net.URL


class JsonDownloader(private val fileName: String, private val url: String, private val context: Context) : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg params: String?): String {
        return downloadImage()
    }

    private fun downloadImage() : String {
        Log.d("Image Loader", "loading json for $url")
        var json = ""
        try {
            val inputStream = URL(url).openStream()

            val s = java.util.Scanner(inputStream).useDelimiter("\\A")
            json = if (s.hasNext()) s.next() else ""

            inputStream.close()
            return json
        } catch (e: Exception){
            Log.e("Image Loader", "Unable to download image")
            e.printStackTrace()
        }
        Log.d("Image Loader", "downloaded json for $url")
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
        Log.d("Image Loader", "save json from $url: ${json.substring(0, 100)} ... ${json.substring(json.length - 100)}")

        try {
            val stream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            OutputStreamWriter(stream).write(json)
            stream.close()
        } catch (e: Exception){
            Log.e("Image Loader", "Unable to save image")
            e.printStackTrace()
        }
        Log.d("Image Loader", "saved json from $url to $fileName")
        Log.d("json", json)
    }
}