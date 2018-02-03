package rak.pixellwp.cycling

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import com.beust.klaxon.Klaxon
import rak.pixellwp.cycling.jsonModels.ColorJsonConverter
import rak.pixellwp.cycling.jsonModels.ImgJson

class BitmapService : IntentService("BitmapService") {

    override fun onHandleIntent(intent: Intent?) {
        val receiver = intent!!.getParcelableExtra<BitmapReceiver>("receiver")
        receiver.send(0, Bundle.EMPTY)

        val bundle = Bundle()
        bundle.putParcelable("bitmap", getBitmap())
        receiver.send(1, bundle)
    }


    private fun getBitmap() : Bitmap {
        val img: ImgJson = Klaxon().converter(ColorJsonConverter).parse<ImgJson>(this@BitmapService.assets.open("SampleFile.json"))!!
        return Bitmap(img)
    }
}