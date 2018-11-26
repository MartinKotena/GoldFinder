package cz.unknown.domain.simpletracker

import android.Manifest
import android.app.Activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

import android.os.Build
import android.os.Bundle

import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bumptech.glide.Glide
import com.muddzdev.styleabletoastlibrary.StyleableToast
import kotlinx.android.synthetic.main.dialog_window.*
import kotlinx.android.synthetic.main.dialog_window.view.*
import kotlinx.android.synthetic.main.include_dialog_add.view.*
import java.io.File



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

        }

        return rootView
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PermissionUtils.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Glide.with(this).load(PermissionUtils.mCurrentPhotoPath1).into(ivPicture)
            mCurrentPhotoPath = PermissionUtils.mCurrentPhotoPath1
            StyleableToast.makeText(activity, "Photo is saved in " + mCurrentPhotoPath.substring(20,43), R.style.toastStorageOK).show()
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