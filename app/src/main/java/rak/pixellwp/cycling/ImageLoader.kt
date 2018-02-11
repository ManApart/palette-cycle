package rak.pixellwp.cycling

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import rak.pixellwp.cycling.jsonModels.JsonDownloader
import rak.pixellwp.cycling.jsonModels.ImageCollection
import rak.pixellwp.cycling.jsonModels.ImageInfo
import rak.pixellwp.cycling.jsonModels.ImgJson
import java.io.*
import java.util.*


class ImageLoader(private val context: Context) {
    private val collection: List<ImageCollection> = loadCollection()

    private fun loadCollection(): List<ImageCollection> {
        val json = context.assets.open("Images.json")
        return jacksonObjectMapper().readValue(json)
    }

    fun getBitmap(name: String) : ColorCyclingImage {
        val image = getImageInfo(name)

        val fileName = image.name + ".json"
        if (!context.getFileStreamPath(fileName).exists()){
            Log.d("Image Loader", "Unable to find $fileName locally, downloading from ${image.url}")
            JsonDownloader(fileName, image.url, context).execute()
        }
        return loadImage(fileName)
    }

    private fun getImageInfo(name: String): ImageInfo {
        val imageCollection = collection.first { it.name == name }
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        Log.d("Image Loader", "grabbing image for $name at hour $hour")
        return imageCollection.images
                .filter { hour > it.startHour }
                .sortedBy { it.startHour }
                .firstOrNull()
                ?: imageCollection.images
                        .sortedBy { it.startHour }
                        .last()
    }

    private fun loadImage(fileName: String): ColorCyclingImage {
        val json: InputStream = if (context.getFileStreamPath(fileName).exists()) {
            FileInputStream(context.getFileStreamPath(fileName))
        } else {
            Log.e("ImageLoader", "Couldn't load $fileName. Falling back to seascape")
            context.assets.open("Seascape.json")
        }
        Log.d("Image Loader", "Loading $fileName from input stream")
        val mapper = jacksonObjectMapper()
//        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
//        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)

//        val s = java.util.Scanner(json).useDelimiter("\\A")
//        val jsonString = if (s.hasNext()) s.next() else ""
//        Log.d("json", jsonString)

        val img: ImgJson = mapper.readValue(json)
        return ColorCyclingImage(img)
    }
}