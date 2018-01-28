package rak.pixellwp

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class ImageWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return ImageWallpaperEngine()
    }

    inner class ImageWallpaperEngine : Engine(){
        private val handler = Handler()
        private val drawRunner = Runnable { draw() }

        private var width: Int = 0
        private var height: Int = 0
        private var visible = true
        private val image = BitmapFactory.decodeResource(resources, R.drawable.beach)

        init {
            handler.post(drawRunner)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) handler.post(drawRunner) else handler.removeCallbacks(drawRunner)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            this.visible = false
            handler.removeCallbacks(drawRunner)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            this.width = width
            this.height = height
            super.onSurfaceChanged(holder, format, width, height)
        }

        private fun draw() {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null){
                    canvas.drawBitmap(image, 0f, 0f, null)
                }
            } finally {
                if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas)
            }
            handler.removeCallbacks(drawRunner)
            if (visible) handler.postDelayed(drawRunner, 1000)
        }
    }
}