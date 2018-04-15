package rak.pixellwp.cycling.jsonModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ImageInfo(var name: String = "Unknown", @JsonProperty("id") private val imageId: String, val startHour: Int = 0, val month: String = "", val script: String = "", val remap: Map<Int, IntArray> = HashMap()){
    val id = imageId + month + script
    var isTimeline = false

    fun getJustId() : String {
        return imageId
    }

    fun getFileName() : String{
        return "$id.json"
    }

}