package rak.pixellwp.cycling.jsonLoading

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import rak.pixellwp.cycling.models.ColorCyclingImage
import rak.pixellwp.cycling.jsonModels.*
import rak.pixellwp.cycling.models.TimelineImage
import java.io.*
import java.util.*

class ImageLoader(private val context: Context) {
    private val logTag = "ImageLoader"
    private val images: List<ImageInfo> = loadImages()
    private val timelineImages: List<ImageInfo> = loadTimelineImages()
    private val collection: List<ImageCollection> = loadCollection()
    private val downloading: MutableList<ImageInfo> = mutableListOf()
    private val loadListeners: MutableList<ImageLoadedListener> = mutableListOf()

    private fun loadImages(): List<ImageInfo> {
        val json = context.assets.open("Images.json")
        return jacksonObjectMapper().readValue(json)
    }

    private fun loadTimelineImages(): List<ImageInfo> {
        val json = context.assets.open("Timelines.json")
        val timelines: List<ImageInfo> = jacksonObjectMapper().readValue(json)
        timelines.forEach{ it.isTimeline = true }
        return timelines
    }

    private fun loadCollection(): List<ImageCollection> {
        val json = context.assets.open("ImageCollections.json")
        return jacksonObjectMapper().readValue(json)
    }

    init {
        getImageCollectionNames()
    }

    private fun getImageCollectionNames() {
        collection.forEach { collection -> collection.images.forEach { image -> image.name = getImageName(image.id) } }
    }

    private fun getImageName(id: String): String {
        return images.firstOrNull { image -> image.id == id }?.name ?: id
    }

    private fun downloadComplete(image: ImageInfo, json: String) {
        if (jsonIsValid(json)) {
            saveImage(image, json)
            for (loadListener in loadListeners) {
                loadListener.imageLoadComplete(image)
            }
        } else {
            val jsonSample = getSampleJson(json)
            Log.d(logTag, "${image.name} failed to download a proper json file: $jsonSample")
            downloading.remove(image)
            Toast.makeText(context, "${image.name} failed to download. Please try again.", Toast.LENGTH_LONG).show()
        }
    }

    private fun jsonIsValid(json: String): Boolean {
        return json.length > 100
                && (json.startsWith("{filename") && json.endsWith("]}")
                    || json.startsWith("{base") && json.endsWith("}}"))
    }

    fun addLoadListener(loadListener: ImageLoadedListener) {
        loadListeners.add(loadListener)
    }

    fun getImageInfoForImage(imageId: String): ImageInfo {
        Log.v(logTag, "Grabbing image info for image $imageId")
        return images.first { it.id == imageId }
    }

    fun getImageInfoForTimeline(imageId: String): ImageInfo {
        Log.v(logTag, "Grabbing timeline info for $imageId")
        return timelineImages.first { it.id == imageId }
    }

    fun getImageInfoForCollection(collectionName: String): ImageInfo {
        val imageCollection = collection.first { it.name == collectionName }
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        Log.v(logTag, "grabbing image info for collection $collectionName at hour $hour")
        val info = imageCollection.images
                .filter { it.startHour < hour }
                .sortedByDescending { it.startHour }
                .firstOrNull()
                ?: imageCollection.images
                        .sortedByDescending { it.startHour }
                        .last()

        Log.d(logTag, "grabbed ${info.name} with hour ${info.startHour}")
        return info
    }

    fun loadImage(image: ImageInfo): ColorCyclingImage {
        val json: String = readJson(loadInputStream(image.getFileName()))
        return ColorCyclingImage(parseImageJson(json))
    }

    fun loadTimelineImage(image: ImageInfo): TimelineImage {
        val json: String = readJson(loadInputStream(image.getFileName()))
        val timelineImage = TimelineImage(parseTimelineImageJson(json))
        timelineImage.loadOverrides(image)
        return timelineImage
    }

    fun imageIsReady(image: ImageInfo): Boolean {
        return context.getFileStreamPath(image.getFileName()).exists()
    }

    fun downloadImage(image: ImageInfo) {
        if (downloading.contains(image)) {
            Log.d(logTag, "Still attempting to download ${image.name}")
        } else {
            Log.d(logTag, "Unable to find ${image.name} locally, downloading using id ${image.id}")
            downloading.add(image)
            JsonDownloader(image, ::downloadComplete).download()
            Toast.makeText(context, "Unable to find ${image.name} locally. I'll change the image as soon as it's downloaded", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveImage(image: ImageInfo, json: String) {
        try {
            val stream = OutputStreamWriter(context.openFileOutput(image.getFileName(), Context.MODE_PRIVATE))
            stream.write(json)
            stream.close()
        } catch (e: Exception) {
            Log.e(logTag, "Unable to save image")
            e.printStackTrace()
        }
        Log.d(logTag, "saved ${image.name} as ${image.getFileName()}")
        downloading.remove(image)
    }

    private fun loadInputStream(fileName: String): InputStream {
        return if (context.getFileStreamPath(fileName).exists()) {
            FileInputStream(context.getFileStreamPath(fileName))
        } else {
            val defaultFileName = "DefaultImage.json";
            if (fileName != defaultFileName){
                Log.e(logTag, "Couldn't load $fileName.")
            }
            context.assets.open(defaultFileName)
        }
    }

    private fun readJson(inputStream: InputStream): String {
        val s = Scanner(inputStream).useDelimiter("\\A")
        val json = if (s.hasNext()) s.next() else ""
        inputStream.close()
        Log.d(logTag, "Reading json from disk: " + getSampleJson(json))
        return json
    }

    private fun parseImageJson(json: String): ImageJson {
        val mapper = jacksonObjectMapper()
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        return mapper.readValue(json)
    }

    private fun parseTimelineImageJson(json: String): TimelineImageJson {
        val mapper = jacksonObjectMapper()
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        return mapper.readValue(json)
    }

    private fun getSampleJson(json: String): String {
        return if (json.length > 100) "${json.substring(0, 100)} ... ${json.substring(json.length - 100)}" else json
    }

}