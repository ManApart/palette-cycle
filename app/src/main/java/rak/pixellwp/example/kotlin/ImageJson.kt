package rak.pixellwp.example.kotlin

import rak.pixellwp.cycling.models.Cycle

data class ImageJson(val width: Int, val height: Int, val cycles: List<Cycle>, val pixels: List<Int>)