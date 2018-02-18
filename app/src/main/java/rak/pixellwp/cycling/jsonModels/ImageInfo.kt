package rak.pixellwp.cycling.jsonModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ImageInfo(val name: String, val id: String, val startHour: Int = 0){
    val fileName = name +".json"
}