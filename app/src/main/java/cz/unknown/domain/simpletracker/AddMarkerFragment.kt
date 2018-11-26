package cz.unknown.domain.simpletracker

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_window.*
import kotlinx.android.synthetic.main.dialog_window.view.*
import kotlinx.android.synthetic.main.include_dialog_add.view.*
import java.io.File
import java.io.IOException
import java.util.*


class AddMarkerFragment: DialogFragment(){

    private var mCurrentPhotoPath: String = ""
    private val ALL_PERMISSIONS = 101

    object GlobalVariable {
        var deleteImage: Boolean = false
    }


    private var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)

    private var mCallback: IDialogAddData? = null

    interface IDialogAddData {
        fun onDialogAddBtnClick(title: String, description: String, path: String)
    }

    override fun onDestroy() {
        if(GlobalVariable.deleteImage) {
            val file = File(mCurrentPhotoPath)
            file.delete()
        }
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.include_dialog_add, container,false)
        dialog.setTitle("About DialogFragment")

        rootView.buttonAdd.setOnClickListener {
            mCallback?.onDialogAddBtnClick(editTextTitle.text.toString(), editTextDescription.text.toString(), mCurrentPhotoPath)
            GlobalVariable.deleteImage = false
            this.dismiss()
        }

        rootView.ibDeleteImage.setOnClickListener {
            val file = File(mCurrentPhotoPath)
            file.delete()
            ivPicture.setImageResource(R.drawable.ic_add_a_photo_black_24dp)
            mCurrentPhotoPath = ""
            rootView.ibDeleteImage.visibility = View.GONE
        }

        rootView.ivPicture.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(activity.applicationContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)  {
                        PermissionUtils.dispatchTakePictureIntent(this)
                    } else {
               ActivityCompat.requestPermissions(activity, permissions, ALL_PERMISSIONS)
            }

           // Open camera and add photo if empty (confirm after image taken), open photo if exists
        }

        return rootView
    }

 /*   @RequiresApi(Build.VERSION_CODES.N)
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        //val p = Environment.getExternalStorageDirectory().absolutePath + File.separator + "Android" + File.separator + "data" + File.separator + "cz.unknown.domain.simpletracker" + File.separator + "files"
        val p = Environment.getExternalStorageDirectory().absolutePath + File.separator+ Environment.DIRECTORY_PICTURES + File.separator + "SimpleTracker"
        val konec = File(p)
        konec.mkdirs()
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                konec    /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun dispatchTakePictureIntent() {
         val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile();
            } catch (e:IOException) {
                Toast.makeText(activity.applicationContext, "Nejede", Toast.LENGTH_SHORT).show()
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(context,
                        "cz.unknown.domain.simpletracker",
                        photoFile)
               takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PermissionUtils.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Glide.with(this).load(PermissionUtils.mCurrentPhotoPath1).into(ivPicture)
            mCurrentPhotoPath = PermissionUtils.mCurrentPhotoPath1
            GlobalVariable.deleteImage = true
            ibDeleteImage.visibility = View.VISIBLE

        }else {
            val file = File(PermissionUtils.mCurrentPhotoPath1)
            mCurrentPhotoPath = ""
            file.delete()
            GlobalVariable.deleteImage = false
        }
    }




    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            mCallback = activity as IDialogAddData
        } catch (e: ClassCastException) {
            Log.d("MyDialog", "Activity doesn't implement the IDialogAddData interface")
        }

    }
}