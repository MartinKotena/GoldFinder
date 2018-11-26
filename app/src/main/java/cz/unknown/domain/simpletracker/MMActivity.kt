package cz.unknown.domain.simpletracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_mm.*
import kotlinx.android.synthetic.main.info_window.view.*
import java.io.File


class MMActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, AddMarkerFragment.IDialogAddData, EditMarkerFragment.IDialogEditData   {

    private val SHARED_PREFS: String = "sharedPrefs"
    private val KEY_MYMARKER: String = "keymyMarker"
    private val KEY_MARKERS: String = "keymarkers"
    private val  ALL_PERMISSIONS = 101;

    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
    private lateinit var mMap: GoogleMap
    private var locationPermitted = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var currentLatLng = LatLng(0.0,0.0)
    private var marker: Marker? = null
    private var lastShownInfoWindowMarker: Marker? = null
    private var markersList = ArrayList<MyMarker>()
    private var markersMapList = ArrayList<Marker>()
    private var backPressedTime: Long = 0
    private var poprve:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mm)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        saveButton.setOnClickListener {
            val addMarkerFragment = AddMarkerFragment()

            addMarkerFragment.show(supportFragmentManager, "AddMarkerFragment")
        }
        poprve = true
    }

    override fun onDialogEditBtnClick(id: String, title: String, description: String, path: String) {
        for (marker in markersList) {
            if (marker.tag == id) {
                marker.title = title
                marker.snippet = description
                marker.path = path
                lastShownInfoWindowMarker?.showInfoWindow()
            }
        }
    }

    override fun onDialogDeleteBtnClick(id: String) {
        for (marker in markersList) {
            if (marker.tag == id) {
                val file = File(marker.path)
                if(file.exists()){
                    file.delete()
                }
                markersList.remove(marker)
            }
        }
        for (marker in markersMapList) {
            if (marker.tag == id) {
                markersMapList.remove(marker)
                marker.remove()
            }
        }
    }

    override fun onDialogAddBtnClick(title: String, description: String, path: String) {
        if (locationPermitted) {
            getUserLocation()
            val tempMarker = mMap.addMarker(MarkerOptions().position(LatLng(lastLocation.latitude, lastLocation.longitude)).title(title).snippet(description).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
            tempMarker.tag = tempMarker.id
            markersMapList.add(tempMarker)
            markersList.add(MyMarker(tempMarker.id, tempMarker.title, tempMarker.snippet, path, tempMarker.position.longitude, tempMarker.position.latitude))
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        if(p0?.snippet == null) {
            return true
        }
        return false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpMap()
        if(poprve)
        {
            loadPrefs()
            turnGPSOn()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        if (locationPermitted) {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 35f))
                }
            }
        }
    }

    private fun setUpMap() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.isMyLocationEnabled = true
            locationPermitted = true
            getUserLocation()
        } else {
            ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS)
        }

        mMap.setOnMapLongClickListener { point ->
            if (marker != null) {
                marker!!.remove()
            }
            marker = mMap.addMarker(MarkerOptions().position(point).title("Marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
        }

        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                val v = layoutInflater.inflate(R.layout.info_window, null)

                for (m in markersList) {
                    if (m.tag == marker.tag) {
                        v.tvTitle.text = m.title
                        v.tvSnippet.text = m.snippet
                    }
                }
                return v
            }

            override fun getInfoContents(marker: Marker): View? {
                return null
            }
        })

        mMap.setOnInfoWindowClickListener { marker ->
            lastShownInfoWindowMarker = marker
            if (marker.isVisible) {
                for (m in markersList) {
                    if (m.tag == marker.tag) {
                        var bundle = Bundle()
                        val gson = Gson()
                        bundle.putString("clicked_marker",gson.toJson(m))
                        val editMarkerFragment = EditMarkerFragment()
                        editMarkerFragment.arguments = bundle
                        editMarkerFragment.show(supportFragmentManager, "EditMarkerFragment")
                    }
                }
            }
        }
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            for (i in 0 until permissions.size) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                        setUpMap()
                    } else {
                        Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
                    }
                }else if(permissions[i].equals(Manifest.permission.CAMERA)){
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                        setUpMap()
                    } else {
                        Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
                    }
                }else if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                        setUpMap()
                    } else {
                        Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
                    }
                }else if(permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                        setUpMap()
                    } else {
                        Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun loadPrefs(){
        poprve = false
        val newString: String?
            val extras = intent.extras
            if (extras == null) {
                newString = ""
            } else {
                newString = extras.getString(MainActivity.shrdprefs)
            }

        val turnsType = object : TypeToken<ArrayList<MyMarker>>() {}.type
        if(newString == "")
        {
            markersList = ArrayList<MyMarker>()
        }else
        {
            markersList = Gson().fromJson<ArrayList<MyMarker>>(newString, turnsType)
            for(mark in markersList)
            {
                val tempMarker = mMap.addMarker(MarkerOptions().position(LatLng(mark.latitude, mark.longitude)).title(mark.title).snippet(mark.snippet).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                tempMarker.tag = tempMarker.id
                markersMapList.add(tempMarker)
            }

        }

    }

    private fun updatePrefs(){
       val prefs = this.getSharedPreferences(SHARED_PREFS,MODE_PRIVATE)
        var markersListArray = Gson().toJsonTree(markersList).asJsonArray
        val editor = prefs.edit()
        editor.putString(KEY_MYMARKER,markersListArray.toString())
        editor.apply()
    }

    override fun onStop() {
        super.onStop()
        updatePrefs()
    }


    override fun onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            updatePrefs()
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()

    }

    private fun turnGPSOn(){
        val manager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this,"Gps is not enabled, please turn it ON",Toast.LENGTH_LONG).show()
        }
    }

}
