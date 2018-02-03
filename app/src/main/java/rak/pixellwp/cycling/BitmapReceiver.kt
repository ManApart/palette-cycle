package rak.pixellwp.cycling

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver

class BitmapReceiver(private val receiver: CyclingWallpaperService.CyclingWallpaperEngine, handler: Handler) : ResultReceiver(handler) {

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        if (resultData != null) {
            receiver.onReceiveResult(resultCode, resultData)
        }
    }


}