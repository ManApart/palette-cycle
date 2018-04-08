package rak.pixellwp.cycling.jsonModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ImageInfo(var name: String = "Unknown", val id: String, val startHour: Int = 0, val month: String = "", val script: String = ""){
//    val fileName = "$id${if()}.json"
    var isTimeline = false

    fun getFileName() : String{
        return "$id${if(isTimeline) "t" else ""}.json"
    }

}