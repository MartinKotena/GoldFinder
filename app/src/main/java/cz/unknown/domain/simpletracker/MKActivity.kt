package cz.unknown.domain.simpletracker

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MKActivity : AppCompatActivity(), MyRecyclerViewAdapter.ItemClickListener {

    var adapter: MyRecyclerViewAdapter? = null
    var animalNames = ArrayList<MyMarker>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mk)
        loadPrefs(savedInstanceState)
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

