package rak.pixellwp.cycling

import android.graphics.Rect
import android.service.wallpaper.WallpaperService
import rak.pixellwp.cycling.jsonLoading.ImageLoadedListener
import rak.pixellwp.cycling.jsonModels.ImageInfo


class CyclingWallpaperService : WallpaperService() {
    override fun onCreate() {
        println("Created!")
        super.onCreate()
    }

    override fun onCreateEngine(): Engine {
        return CyclingWallpaperEngine()
    }

    inner class CyclingWallpaperEngine : Engine(), ImageLoadedListener {
        val screenDimensions: Rect = Rect()

        override fun imageLoadComplete(image: ImageInfo) {
            println("image load complete")
        }

        fun getOffsetImage(): Rect {
            return Rect()
        }
    }
}