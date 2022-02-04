package rak.pixellwp

import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import rak.pixellwp.cycling.CyclingWallpaperService

class SetWallpaperActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isFinishing){
            showCyclingWallpaper()
        }
//        finish()
    }

    private fun showCyclingWallpaper(){
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this@SetWallpaperActivity, CyclingWallpaperService::class.java
                ))
        startActivity(intent)

    }
}
