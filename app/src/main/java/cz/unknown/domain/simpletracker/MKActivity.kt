package cz.unknown.domain.simpletracker

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.recyclerview.R.attr.layoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import android.support.v7.widget.DividerItemDecoration
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MKActivity : AppCompatActivity(), MyRecyclerViewAdapter.ItemClickListener {

    public val SHARED_PREFS: String = "sharedPrefs"
    public val KEY_MYMARKER: String = "keymyMarker"
    var adapter: MyRecyclerViewAdapter? = null
    var animalNames = ArrayList<MyMarker>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mk)
        loadPrefs(savedInstanceState)
        // set up the RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rvAnimals)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyRecyclerViewAdapter(this, animalNames)
        adapter!!.setClickListener(this)
        recyclerView.adapter = adapter
        }


    override fun onItemClick(view: View, position: Int) {
        var intent = Intent(this, DetailActivity::class.java)
        var markersListArray = Gson().toJson(adapter?.getItem(position))
        intent.putExtra("bod", markersListArray.toString())
        startActivity(intent)
       //Toast.makeText(this, "You clicked " + adapter?.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show()
    }

    fun loadPrefs(savedInstanceState: Bundle?){
        val newString: String?
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                newString = ""
            } else {
                newString = extras.getString(MainActivity.shrdprefs)
            }
        } else {
            newString = savedInstanceState.getSerializable(MainActivity.shrdprefs) as String
        }
        val turnsType = object : TypeToken<ArrayList<MyMarker>>() {}.type
        if(newString == "")
        {
            animalNames = ArrayList<MyMarker>()
        }else
        {
            animalNames = Gson().fromJson<ArrayList<MyMarker>>(newString, turnsType)
        }
    }

}

