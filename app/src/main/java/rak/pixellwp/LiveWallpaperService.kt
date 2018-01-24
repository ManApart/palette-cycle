package rak.pixellwp

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.preference.PreferenceManager
import android.service.wallpaper.WallpaperService
import rak.pixellwp.drawing.CirclePoint


class LiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return LiveWallpaperEngine(this)
    }

    inner class LiveWallpaperEngine(context: WallpaperService) : Engine() {
        private val handler = Handler()
        private val drawRunner = Runnable { draw() }
        private val width: Int = 0
        private var height: Int = 0
        private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        private var maxNumber: Int = Integer.valueOf(prefs.getString("numberOfCircles", "4"))
        private var circles: List<CirclePoint> = ArrayList()
        private var visible = true
        private var touchEnabled: Boolean = prefs.getBoolean("touch", false);

        private val paint = Paint()

        init {
            paint.isAntiAlias = true
            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeWidth = 10f
            handler.post(drawRunner)
        }


        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
        }


        fun draw() {

        }
    }

}