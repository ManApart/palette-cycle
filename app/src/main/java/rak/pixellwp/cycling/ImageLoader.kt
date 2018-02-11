package rak.pixellwp.cycling

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import rak.pixellwp.cycling.jsonModels.JsonDownloader
import rak.pixellwp.cycling.jsonModels.ImageCollection
import rak.pixellwp.cycling.jsonModels.ImageInfo
import rak.pixellwp.cycling.jsonModels.ImgJson
import java.io.*
import java.util.*


class ImageLoader(private val context: Context) {

    val collection: List<ImageCollection> = loadCollection()

    private fun loadCollection(): List<ImageCollection> {
        val json = context.assets.open("Images.json")
        return jacksonObjectMapper().readValue(json)
    }

    fun getBitmap(name: String) : ColorCyclingImage {
        Log.d("Image Loader", "grabbing image for $name")
        val image = getImageInfo(name)

        val fileName = image.name + ".json"
//        if (!File(context.filesDir, fileName).exists()){
        if (!context.assets.list("").contains(fileName)){
            Log.d("Image Loader", "Unable to find $fileName locally, downloading from ${image.url}")
            downloadImage(fileName, image.url)
        }
        return loadImage(fileName)
    }

    private fun getImageInfo(name: String): ImageInfo {
        val imageCollection = collection.first { it.name == name }
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        Log.d("Image Loader", "grabbing image for $hour")
        return imageCollection.images
                .filter { hour > it.startHour }
                .sortedBy { it.startHour }
                .firstOrNull()
                ?: imageCollection.images
                        .sortedBy { it.startHour }
                        .last()
    }

    private fun downloadImage(fileName: String, url: String) {
        JsonDownloader(fileName, url, context).execute()

//        val url = URL(url)
//        val urlConnection = url.openConnection() as HttpURLConnection
//        try {
//            val input = BufferedInputStream(urlConnection.inputStream)
//            val bytes = input.read()
//            val fileout = context.openFileOutput(fileName, MODE_PRIVATE)
//            val outputWriter = OutputStreamWriter(fileout). use {
//                it.write(input)
//            }
//            outputWriter.close()
//        } finally {
//            urlConnection.disconnect()
//        }
//        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        val uri = Uri.parse(url)
//        val request = DownloadManager.Request(uri)
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
////        request.setde(this@Video_detail_Activity, Environment.DIRECTORY_DOWNLOADS, videoName)
//        val reference = downloadManager.enqueue(request)

    }

    private fun loadImage(fileName: String): ColorCyclingImage {
//        val json: InputStream = if (File(context.filesDir, fileName).exists()) {
//            FileInputStream(File(context.filesDir, fileName))
//        } else {
            Log.e("ImageLoader", "Couldn't load $fileName. Falling back to seascape")
            val json = context.assets.open("Seascape.json")
//        }

        val img: ImgJson = jacksonObjectMapper().readValue(json)
        return ColorCyclingImage(img)
    }
}