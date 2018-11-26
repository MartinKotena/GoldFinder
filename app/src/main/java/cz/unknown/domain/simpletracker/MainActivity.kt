package cz.unknown.domain.simpletracker

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.net.wifi.WifiManager
import android.widget.Toast
import com.muddzdev.styleabletoastlibrary.StyleableToast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val SHARED_PREFS: String = "sharedPrefs"
    private val KEY_MYMARKER: String = "keymyMarker"
    private var markers:String = ""
    companion object {
        const val shrdprefs = "markers"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val wifimanager: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        btnMM.setOnClickListener {
            loadPrefs()
            if (wifimanager.isWifiEnabled){
                StyleableToast.makeText(this,"Wifi is enabled", R.style.toastOK).show()
            }else
            {
                wifimanager.setWifiEnabled(true)
                StyleableToast.makeText(this,"Wifi was disabled -> now is enabled.", R.style.toastNotOk).show()
            }
            var intent1 = Intent(this, MMActivity::class.java)
            intent1.putExtra(shrdprefs, markers)
            startActivity(intent1)
        }

        btnOnline.setOnClickListener {
            var intent2 = Intent(this, ImagesActivity::class.java)
            startActivity(intent2)
        }

        btnMK.setOnClickListener{
            loadPrefs()
            var intent = Intent(this, MKActivity::class.java)
            intent.putExtra(shrdprefs, markers)
            startActivity(intent)
        }
    }

    fun loadPrefs(){
        val prefs = this.getSharedPreferences(SHARED_PREFS,MODE_PRIVATE)
        markers = prefs.getString(KEY_MYMARKER,"")
    }

}
