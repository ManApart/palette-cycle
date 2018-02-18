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


class ImageLoader(private val context: Context, private val listener: JsonDownloadListener) {
    private val collection: List<ImageCollection> = loadCollection()
    private val images: List<ImageInfo> = loadImages()

    private fun loadCollection(): List<ImageCollection> {
        val json = context.assets.open("ImageCollections.json")
        return jacksonObjectMapper().readValue(json)
    }

    private fun loadImages(): List<ImageInfo> {
        val json = context.assets.open("Images.json")
        return jacksonObjectMapper().readValue(json)
    }

    fun getImageInfoForImage(imageId: String): ImageInfo {
        Log.d("Image Loader", "grabbing image info for $imageId")
        return images.first { it.id == imageId }
    }

    fun getImageInfoForCollection(collectionName: String): ImageInfo {
        val imageCollection = collection.first { it.name == collectionName }
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        Log.d("Image Loader", "grabbing image info for $collectionName at hour $hour")
        return imageCollection.images
                .filter { hour > it.startHour }
                .sortedBy { it.startHour }
                .firstOrNull()
                ?: imageCollection.images
                        .sortedBy { it.startHour }
                        .last()
    }

    fun loadImage(image: ImageInfo): ColorCyclingImage {
        startDownloadingMissingFile(image)
        val json: String = readJson(loadInputStream(image.fileName))
        Log.d("Image Loader", "load json: ${json.substring(0, 100)} ... ${json.substring(json.length - 100)}")
        return ColorCyclingImage(parseJson(json))
    }

    private fun startDownloadingMissingFile(image: ImageInfo) {
        if (!context.getFileStreamPath(image.fileName).exists()) {
            Log.d("Image Loader", "Unable to find ${image.fileName} locally, downloading using id ${image.id}")
            JsonDownloader(image, context, listener).execute()
            Toast.makeText(context, "Unable to find ${image.fileName} locally. I'll change the image as soon as it's downloaded", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadInputStream(fileName: String): InputStream {
        return if (context.getFileStreamPath(fileName).exists()) {
            FileInputStream(context.getFileStreamPath(fileName))
        } else {
            Log.e("ImageLoader", "Couldn't load $fileName.")
            context.assets.open("DefaultImage.json")
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