package cz.unknown.domain.simpletracker

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.dialog_window.view.*
import org.w3c.dom.Text
import java.io.File

class DetailActivity : AppCompatActivity() {

    private var markersList: MyMarker? = null
    private var imageview: ImageView? = null
    private var textview_title: TextView? = null
    private var textview_description: TextView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        imageview = findViewById(R.id.detailImageView)
        textview_title = findViewById(R.id.textView_title)
        textview_description = findViewById(R.id.textView_description)



        val newString: String?
        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                newString = null
            } else {
                newString = extras.getString("bod")
            }
        } else {
            newString = savedInstanceState.getSerializable("bod") as String
        }
        val turnsType = object : TypeToken<MyMarker>() {}.type
        val turns = Gson().fromJson<MyMarker>(newString, turnsType)
        markersList = turns
        napln(markersList)
        imageview?.setOnClickListener {

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                var imgFile = File(markersList?.path)
                if(imgFile.exists()){
                    val mBuilder = AlertDialog.Builder(this@DetailActivity)
                    val mView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
                    val photoView = mView.findViewById<PhotoView>(R.id.imageView)
                    photoView.setImageURI(Uri.fromFile(File(markersList?.path)))
                    mBuilder.setView(mView)
                    val mDialog = mBuilder.create()
                    mDialog.show()
                }
            }*/
            var bundle = Bundle()
            bundle.putString("image", newString)
            val detailfragment = detailFragment()
            detailfragment.arguments = bundle
            detailfragment.show(supportFragmentManager, "detailFragment")

            // Open camera and add photo if empty (confirm after image taken), open photo if exists
        }




    }

    fun napln(mark:MyMarker?){
        var f = File(mark?.path)
        if (f.exists()){
            Glide.with(this).load(mark?.path).apply(RequestOptions()
                    .placeholder(R.drawable.ic_add_a_photo_black_24dp)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate()).into(imageview)
            textview_title?.setText(mark?.title)
            textview_description?.setText(mark?.snippet)
        }

    }
}
