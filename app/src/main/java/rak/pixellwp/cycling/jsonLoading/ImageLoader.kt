package rak.pixellwp.cycling.jsonLoading

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.module.kotlin.readValue
import rak.pixellwp.cycling.models.ColorCyclingImage
import rak.pixellwp.cycling.jsonModels.*
import rak.pixellwp.cycling.models.TimelineImage
import rak.pixellwp.mapper
import java.io.*
import java.util.*
import kotlin.concurrent.thread

class ImageLoader(private val context: Context) {
    private val logTag = "ImageLoader"
    private val images = parseImages()
    private val timelineImages = parseTimelineImages()
    private val collection = parseCollection()
    private val downloading = mutableSetOf<ImageInfo>()
    private val loadListeners = mutableListOf<ImageLoadedListener>()

    private fun parseImages(): List<ImageInfo> {
        val json = context.assets.open("Images.json")
        return mapper.readValue(json)
    }

    private fun parseTimelineImages(): List<ImageCollection> {
        val json = context.assets.open("Timelines.json")
        val timelines: List<ImageCollection> = mapper.readValue(json)
        timelines.forEach{collection -> collection.images.forEach { it.isTimeline = true }}
        return timelines
    }

    private fun parseCollection(): List<ImageCollection> {
        val json = context.assets.open("ImageCollections.json")
        return mapper.readValue(json)
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

    private fun saveAndLoadImage(image: ImageInfo, json: String) {
        saveImage(image, json, true)
    }

    private fun saveImageWithoutLoading(image: ImageInfo, json: String) {
        saveImage(image, json, false)
    }

    private fun saveImage(image: ImageInfo, json: String, alsoChangeImage: Boolean) {
        if (jsonIsValid(json)) {
            saveImage(image, json)
            if (alsoChangeImage) {
                for (loadListener in loadListeners) {
                    loadListener.imageLoadComplete(image)
                }
            }
        } else {
            val jsonSample = json.getSample()
            Log.d(logTag, "${image.name} failed to download a proper json file: $jsonSample")
            downloading.remove(image)
            //Can't toast in other thread
//            Toast.makeText(context, "${image.name} failed to download. Please try again.", Toast.LENGTH_LONG).show()
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
        return images.firstOrNull { it.id == imageId } ?: throw IllegalArgumentException("Could not find single image for $imageId")
    }

    fun getImageInfoForTimeline(imageId: String): ImageInfo {
        val collection = timelineImages.firstOrNull { it.name == imageId } ?: throw IllegalArgumentException("Could not find timeline for $imageId")
        return getImageInfoForCollection(collection)
    }

    fun getImageInfoForCollection(collectionName: String): ImageInfo {
        val imageCollection = collection.first { it.name == collectionName }
        return getImageInfoForCollection(imageCollection)
    }

    private fun getImageInfoForCollection(collection: ImageCollection): ImageInfo {
        //TODO - grab weather
        // TODO - override time
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        Log.v(logTag, "grabbing image info for collection ${collection.name} at hour $hour")
        val info = collection.images
            .filter { it.startHour < hour }.maxByOrNull { it.startHour }
            ?: collection.images.minByOrNull { it.startHour }!!

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
            thread {
                JsonDownloader(image, ::saveAndLoadImage).download()
            }
            Toast.makeText(context, "Unable to find ${image.name} locally. I'll change the image as soon as it's downloaded", Toast.LENGTH_LONG).show()
        }
    }

    fun preloadImages() {
        val imagesToDownload = (images.filterNot { imageIsReady(it) } +
                timelineImages.flatMap { it.images }.filterNot { imageIsReady(it) } +
                collection.flatMap { it.images }.filterNot { imageIsReady(it) })
            .filterNot { downloading.contains(it) }

        if (imagesToDownload.isNotEmpty()) {
            Log.d(logTag, "Downloading ${imagesToDownload.size} images.")
            Toast.makeText(context, "Downloading ${imagesToDownload.size} images.", Toast.LENGTH_LONG).show()
            thread {
                imagesToDownload.forEach { downloadImageWithoutChanging(it) }
            }
        } else {
            Log.d(logTag, "All images downloaded.")
            Toast.makeText(context, "All images downloaded.", Toast.LENGTH_LONG).show()
        }
    }

    private fun downloadImageWithoutChanging(image: ImageInfo) {
        if (!downloading.contains(image)) {
            downloading.add(image)
            JsonDownloader(image, ::saveImageWithoutLoading).download()
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
            if (fileName != defaultFileName) {
                Log.e(logTag, "Couldn't load $fileName.")
            }
            context.assets.open(defaultFileName)
        }
    }

    private fun readJson(inputStream: InputStream): String {
        val s = Scanner(inputStream).useDelimiter("\\A")
        val json = if (s.hasNext()) s.next() else ""
        inputStream.close()
        Log.d(logTag, "Reading json from disk: " + json.getSample())
        return json
    }

    private fun parseImageJson(json: String): ImageJson {
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        return mapper.readValue(json)
    }

    private fun parseTimelineImageJson(json: String): TimelineImageJson {
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        return mapper.readValue(json)
    }

    private fun String.getSample(): String {
        return if (length > 100) "${substring(0, 100)} ... ${substring(length - 100)}" else this
    }

}