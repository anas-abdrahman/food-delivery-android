package com.kukus.administrator.news

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kukus.administrator.R
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils.Companion.getDateConvert
import com.kukus.library.FirebaseUtils.Companion.getNews
import kotlinx.android.synthetic.main.activity_edit_news.*
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class ActivityEditNews : AppCompatActivity() {

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

        upload_image.setOnClickListener {
            chooseImage()
        }

        val xId = intent.extras.getString("id", "")
        val xImage = intent.extras.getString("image", "")
        val xDate = intent.extras.getLong("date", 0)

        if (xImage != "") {

            if (Constant.isValidContextForGlide(applicationContext)) {

                Glide
                        .with(applicationContext)
                        .load(xImage)
                        .into(image)
            }

        }

        date.text = DateFormat.format("hh:mm a dd/MM/yyyy", xDate).toString()

        btn_remove.setOnClickListener {

            getNews(xId).removeValue().addOnCompleteListener {

                if (isUploadImage && imageUrl != "") {

                    if (xImage != "") {

                        val storageImage = FirebaseStorage.getInstance().reference.child(xImage)

                        storageImage.downloadUrl.addOnSuccessListener {
                            storageImage.delete()
                        }

                    }

                }

                toast("deleted!")
                finish()

            }

        }

        btn_submit.setOnClickListener {

            val news = HashMap<String, Any>()

            if (isUploadImage && imageUrl != "") {

                if (xImage != "") {
                    val storageImage = FirebaseStorage.getInstance().reference.child(xImage)

                    storageImage.downloadUrl.addOnSuccessListener {
                        storageImage.delete()
                    }
                }

                news["image"] = imageUrl
            }


            getNews(xId).updateChildren(news).addOnCompleteListener {

                toast("updated")

                finish()

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
