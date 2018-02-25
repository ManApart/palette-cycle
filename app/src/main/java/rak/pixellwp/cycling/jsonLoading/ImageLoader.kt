package rak.pixellwp.cycling.jsonLoading

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import rak.pixellwp.cycling.ColorCyclingImage
import rak.pixellwp.cycling.jsonModels.*
import java.io.*
import java.util.*

class ImageLoader(private val context: Context) : JsonDownloadListener {
    private val logTag = "ImageLoader"
    private val collection: List<ImageCollection> = loadCollection()
    private val images: List<ImageInfo> = loadImages()
    private val defaultImage = ImageInfo("DefaultImage", "none", 0)
    private val downloading: MutableList<ImageInfo> = mutableListOf()
    private val loadListeners: MutableList<ImageLoadedListener> = mutableListOf()

    private fun loadCollection(): List<ImageCollection> {
        val json = context.assets.open("ImageCollections.json")
        return jacksonObjectMapper().readValue(json)
    }

    private fun loadImages(): List<ImageInfo> {
        val json = context.assets.open("Images.json")
        return jacksonObjectMapper().readValue(json)
    }

    override fun downloadComplete(image: ImageInfo, json: String) {
        saveImage(image, json)
        for (loadListener in loadListeners){
            loadListener.imageLoadComplete(image)
        }
    }

    fun addLoadListener(loadListener: ImageLoadedListener){
        loadListeners.add(loadListener)
    }

    fun getImageInfoForImage(imageId: String): ImageInfo {
        Log.d(logTag, "grabbing image info for $imageId")
        return images.first { it.id == imageId }
    }

    fun getImageInfoForCollection(collectionName: String): ImageInfo {
        val imageCollection = collection.first { it.name == collectionName }
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        Log.d(logTag, "grabbing image info for $collectionName at hour $hour")
        val info = imageCollection.images
                .filter { it.startHour > hour }
                .sortedBy { it.startHour }
                .firstOrNull()
                ?: imageCollection.images
                        .sortedBy { it.startHour }
                        .last()

        Log.d(logTag, "grabbed ${info.name} with hour ${info.startHour}")
        return info
    }

    fun loadImage(image: ImageInfo, fallback: ImageInfo = defaultImage): ColorCyclingImage {
        startDownloadingMissingFile(image)
        val json: String = readJson(loadInputStream(image.fileName, fallback.fileName))
        Log.d(logTag, "load json: ${json.substring(0, 100)} ... ${json.substring(json.length - 100)}")
        return ColorCyclingImage(parseJson(json))
    }

    private fun startDownloadingMissingFile(image: ImageInfo) {
        if (!context.getFileStreamPath(image.fileName).exists()) {
            if (downloading.contains(image)){
                Log.d(logTag, "Still attempting to download ${image.fileName}")
            } else {
                Log.d(logTag, "Unable to find ${image.fileName} locally, downloading using id ${image.id}")
                downloading.add(image)
                JsonDownloader(image, this).execute()
                Toast.makeText(context, "Unable to find ${image.fileName} locally. I'll change the image as soon as it's downloaded", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveImage(image: ImageInfo, json: String){
        try {
            val stream = OutputStreamWriter(context.openFileOutput(image.fileName, Context.MODE_PRIVATE))
            stream.write(json)
            stream.close()
        } catch (e: Exception){
            Log.e(logTag, "Unable to save image")
            e.printStackTrace()
        }
        Log.d(logTag, "saved ${image.fileName}")
        downloading.remove(image)
    }

    private fun loadInputStream(fileName: String, fallback: String): InputStream {
        return if (context.getFileStreamPath(fileName).exists()) {
            FileInputStream(context.getFileStreamPath(fileName))
        } else {
            Log.e(logTag, "Couldn't load $fileName.")
            FileInputStream(context.getFileStreamPath(fallback))
//            context.assets.open("DefaultImage.json")
        }
    }

    private fun readJson(inputStream: InputStream): String {
        val s = Scanner(inputStream).useDelimiter("\\A")
        val json = if (s.hasNext()) s.next() else ""
        inputStream.close()
        return json
    }

    private fun parseJson(json: String): ImgJson {
        val mapper = jacksonObjectMapper()
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        return mapper.readValue(json)
    }

}