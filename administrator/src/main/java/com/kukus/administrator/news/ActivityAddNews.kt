package com.kukus.administrator.news

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import com.androidhuman.rxfirebase2.database.rxSetValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kukus.administrator.R
import com.kukus.library.FirebaseUtils.Companion.getNews
import com.kukus.library.FirebaseUtils.Companion.getPushKey
import com.kukus.library.model.News
import kotlinx.android.synthetic.main.activity_edit_news.*
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class ActivityAddNews : AppCompatActivity() {

    private var filePath: Uri? = null

    private var imageUrl = ""

    private val PICK_IMAGE_REQUEST = 71

    private var isUploadImage = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_news)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        date.visibility = View.GONE
        btn_remove.visibility = View.GONE

        upload_image.setOnClickListener {
            chooseImage()
        }

        btn_submit.setOnClickListener {

            val key = getPushKey
            val news = News()

            if(imageUrl != "") {

                news.id = key
                news.image = imageUrl

                getNews(news.id).rxSetValue(news).subscribe {

                    toast("add complete")

                    finish()

                }

            }else{
                toast("please field empty form")
            }
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            filePath = data.data

            try {


                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)

                image.setImageBitmap(bitmap)

                val compressor = getBytesFromBitmap(bitmap, 60)

                uploadImage(compressor)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun getBytesFromBitmap(bitmap: Bitmap, quality: Int): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }

    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null

    private fun uploadImage(compressor: ByteArray) {

        if (compressor.isNotEmpty()) {

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            imageUrl = "images/" + UUID.randomUUID().toString()

            val ref = storageReference!!.child(imageUrl)


            ref.putBytes(compressor)
                    .addOnSuccessListener {

                        it.storage.downloadUrl.addOnCompleteListener { data ->

                            isUploadImage = true

                            imageUrl = data.result.toString()

                            progressDialog.dismiss()

                            toast("Uploaded")
                        }


                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        toast("Failed")
                    }
                    .addOnProgressListener { taskSnapshot ->


                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                                .totalByteCount
                        progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                    }
        }
    }
}
