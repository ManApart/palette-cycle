package rak.pixellwp.cycling.models

import rak.pixellwp.cycling.jsonModels.PaletteJson
import rak.pixellwp.cycling.jsonModels.TimelineImageJson

class TimelineImage(json: TimelineImageJson) {
    val base: ColorCyclingImage = ColorCyclingImage(json.base)
    val palettes: List<Palette> = parsePalettes(json.palettes)
    val timeline: Timeline = Timeline(json.timeline)


    private fun parsePalettes(jsonPalettes: List<PaletteJson>): List<Palette> {
        return jsonPalettes.map { palette -> Palette(palette) }.toList()
    }

}