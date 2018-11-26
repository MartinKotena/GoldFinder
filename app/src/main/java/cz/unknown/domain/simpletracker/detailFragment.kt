package cz.unknown.domain.simpletracker

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.dialog_upload_image.view.*
import java.io.File
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task



class detailFragment: DialogFragment() {

    private lateinit var mStorageRef:StorageReference
    private lateinit var mDatabaseRef: DatabaseReference


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.dialog_upload_image, container,false)
        var bundle = this.arguments
        var bundleString = bundle.getString("image")
        val turnsType = object : TypeToken<MyMarker>() {}.type
        val turns = Gson().fromJson<MyMarker>(bundleString, turnsType)

        mStorageRef = FirebaseStorage.getInstance().getReference("GoldFinder")
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("GoldFinder")
        rootView.buttonView.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                var imgFile = File(turns.path)
                if(imgFile.exists()){
                    val mBuilder = AlertDialog.Builder(this@detailFragment.context)
                    val mView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
                    val photoView = mView.findViewById<PhotoView>(R.id.imageView)
                    photoView.setImageURI(Uri.fromFile(File(turns.path)))
                    mBuilder.setView(mView)
                    val mDialog = mBuilder.create()
                    mDialog.show()
                }
            }
        }

        rootView.buttonUpload.setOnClickListener{
            uploadFile(rootView,turns)
        }










        return rootView
    }

   /*private fun getFileExtension(uri : Uri): String? {
       var cR = ContentResolver()
       var mime = MimeTypeMap.getSingleton()
       return mime.getExtensionFromMimeType(cR.getType(uri))
   }*/

    private fun uploadFile(v:View,bun:MyMarker){
        var filereference = mStorageRef.child(bun.path.substring(43))
        filereference.putFile(Uri.fromFile(File(bun.path)))
                .addOnSuccessListener {
                    var handler = Handler()
                    var r = Runnable {
                        kotlin.run {
                            v.progress_bar_upload.progress = 0
                        }
                    }
                    handler.postDelayed(r,500)
                    Toast.makeText(this.context,"Upload successful",Toast.LENGTH_LONG).show()
                    var profileImageRef = FirebaseStorage.getInstance().getReference("GoldFinder/" + bun.path.substring(43))
                    val task = it.getMetadata()?.getReference()?.getDownloadUrl()
                    task?.addOnSuccessListener {
                        var upload = Upload(bun.title, it.toString())
                        var uploadId = mDatabaseRef.push().key.toString()
                        mDatabaseRef.child(uploadId).setValue(upload)
                    }



                }
                .addOnFailureListener{
                    Toast.makeText(this.context,it.message,Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener {
                    var progress = (100.0 * it.bytesTransferred / it.totalByteCount)
                    v.progress_bar_upload.progress = progress.toInt()
                }
    }
}