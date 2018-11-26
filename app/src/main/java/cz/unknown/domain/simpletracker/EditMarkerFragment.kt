package cz.unknown.domain.simpletracker

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dialog_window.*
import kotlinx.android.synthetic.main.dialog_window.view.*
import kotlinx.android.synthetic.main.include_dialog_edit.view.*
import java.io.File
import com.github.chrisbanes.photoview.PhotoView


class EditMarkerFragment: DialogFragment(){

    private var mCallback: IDialogEditData? = null
    private var mCurrentPhotoPath: String = ""

    interface IDialogEditData {
        fun onDialogEditBtnClick(id: String, title: String, description: String, path: String)
        fun onDialogDeleteBtnClick(id: String)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.include_dialog_edit, container,false)
        dialog.setTitle("About DialogFragment")

        var bundle = this.arguments
        var bundleString = bundle.getString("clicked_marker")
        val gson = Gson()
        var clickedMarker: MyMarker = gson.fromJson(bundleString, MyMarker::class.java)



        if(clickedMarker.path != "") {
            Glide.with(this).load(clickedMarker.path).apply(RequestOptions()
                    .placeholder(R.drawable.ic_add_a_photo_black_24dp)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .dontAnimate()).into(rootView.ivPicture)
            rootView.ibDeleteImage.visibility = View.VISIBLE
        }
        rootView.editTextTitle.setText(clickedMarker.title)
        rootView.editTextDescription.setText(clickedMarker.snippet)

        rootView.buttonEdit.setOnClickListener {
           // clickedMarker.path = mCurrentPhotoPath
            var tempFileSoubor = File(clickedMarker.path)
            var tempString = clickedMarker.path
            if(tempFileSoubor.exists())
            {
                if(tempString == PermissionUtils.mCurrentPhotoPath1)
                {
                    clickedMarker.path = PermissionUtils.mCurrentPhotoPath1
                }else if(tempString ==mCurrentPhotoPath) {
                    clickedMarker.path = mCurrentPhotoPath
                }
                else{
                    clickedMarker.path = clickedMarker.path

                }
            }else{
                clickedMarker.path = mCurrentPhotoPath
            }
           // clickedMarker.path = PermissionUtils.mCurrentPhotoPath1
            mCallback?.onDialogEditBtnClick(clickedMarker.tag, editTextTitle.text.toString(), editTextDescription.text.toString(), clickedMarker.path)
            this.dismiss()
        }

        rootView.buttonDelete.setOnClickListener {
            mCallback?.onDialogDeleteBtnClick(clickedMarker.tag)
            this.dismiss()
        }

        rootView.ivPicture.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                var imgFile = File(clickedMarker.path)
                if(imgFile.exists()){
                        val mBuilder = AlertDialog.Builder(this@EditMarkerFragment.context)
                        val mView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
                        val photoView = mView.findViewById<PhotoView>(R.id.imageView)
                        photoView.setImageURI(Uri.fromFile(File(clickedMarker.path)))
                        mBuilder.setView(mView)
                        val mDialog = mBuilder.create()
                        mDialog.show()
                }else{
                    PermissionUtils.dispatchTakePictureIntent(this)
                }
            }
            // Open camera and add photo if empty (confirm after image taken), open photo if exists
        }

        rootView.ibDeleteImage.setOnClickListener {
            val file = File(clickedMarker.path)
            file.delete()
            ivPicture.setImageResource(R.drawable.ic_add_a_photo_black_24dp)
            clickedMarker.path = ""
            mCurrentPhotoPath = ""
            rootView.ibDeleteImage.visibility = View.GONE
        }


        return rootView
    }
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PermissionUtils.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK)
        {
            Glide.with(this).load(PermissionUtils.mCurrentPhotoPath1).into(ivPicture)
            mCurrentPhotoPath = PermissionUtils.mCurrentPhotoPath1
            AddMarkerFragment.GlobalVariable.deleteImage = true
            ibDeleteImage.visibility = View.VISIBLE
        } else {
                val file = File(PermissionUtils.mCurrentPhotoPath1)
                file.delete()
                mCurrentPhotoPath = ""
                AddMarkerFragment.GlobalVariable.deleteImage = false
        }
    }



    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            mCallback = activity as IDialogEditData
        } catch (e: ClassCastException) {
            Log.d("MyDialog", "Activity doesn't implement the IDialogAddData interface")
        }

    }


}