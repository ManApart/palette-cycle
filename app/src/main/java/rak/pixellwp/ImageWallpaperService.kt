package rak.pixellwp

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
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
        private val image = BitmapFactory.decodeResource(resources, R.drawable.beach)

        private var visible = true
        private var imageSrc: Rect = Rect(0, 0, image.width, image.height)
        private var imageDest: Rect = Rect()

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
            imageDest = Rect(0, 0, width, height)
            super.onSurfaceChanged(holder, format, width, height)
        }

        private fun draw() {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null){
                    canvas.drawBitmap(image, imageSrc, imageDest, null)
                }
            } finally {
                if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas)
            }
            handler.removeCallbacks(drawRunner)
            if (visible) handler.postDelayed(drawRunner, 1000)
        }
    }
}