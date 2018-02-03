package rak.pixellwp

import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import rak.pixellwp.circles.CircleWallpaperService
import rak.pixellwp.cycling.CyclingWallpaperService
import rak.pixellwp.singleImage.ImageWallpaperService

class SetWallpaperActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showCyclingWallpaper(View(this))
    }


    fun showCircleWallpaper(view: View){
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this, CircleWallpaperService::class.java
        ))
        startActivity(intent)
    }

    fun showImageWallpaper(view: View){
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this, ImageWallpaperService::class.java
        ))
        startActivity(intent)
    }

    fun showCyclingWallpaper(view: View){
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this, CyclingWallpaperService::class.java
        ))
        startActivity(intent)
    }
}