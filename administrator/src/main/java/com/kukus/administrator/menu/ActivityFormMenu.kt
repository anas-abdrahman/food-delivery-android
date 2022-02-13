package com.kukus.administrator.menu

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.androidhuman.rxfirebase2.database.rxSetValue
import com.androidhuman.rxfirebase2.database.rxUpdateChildren
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.kukus.administrator.R
import com.kukus.library.Constant.Companion.TYPE_ORDER
import com.kukus.library.Constant.Companion.isValidContextForGlide
import com.kukus.library.FirebaseUtils.Companion.getMenu
import com.kukus.library.FirebaseUtils.Companion.getPushKey
import com.kukus.library.model.Menu
import kotlinx.android.synthetic.main.activity_form_menu.*
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class ActivityFormMenu : AppCompatActivity() {

    private var imageUrl = ""
    private val PICK_IMAGE_REQUEST = 71
    private val menuOld = Menu()
    private var isUploadImage = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_form_menu)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        setViewContent()

        onClickListener()

    }

    private fun setViewContent() {

        menuOld.id = intent.extras.getString("id", "")
        menuOld.available = intent.extras.getBoolean("available", true)
        menuOld.show = intent.extras.getBoolean("show", true)
        menuOld.type = intent.getSerializableExtra("type") as TYPE_ORDER
        menuOld.image = intent.extras.getString("image", "")
        menuOld.name = intent.extras.getString("title", "")
        menuOld.description = intent.extras.getString("description", "")
        menuOld.label_price1 = intent.extras.getString("label_price1", "")
        menuOld.label_price2 = intent.extras.getString("label_price2", "")
        menuOld.price1 = intent.extras.getFloat("price2", 0f)
        menuOld.price2 = intent.extras.getFloat("price2", 0f)
        menuOld.limit = intent.extras.getInt("limit", 0)
        menuOld.currentLimit = intent.extras.getInt("currentLimit", 0)

        switch_available.isChecked = menuOld.available
        switch_show.isChecked = menuOld.show

        menu_name.setText(menuOld.name)
        menu_content.setText(menuOld.description)

        if(menuOld.price1 != 0f || menuOld.limit != 0) {
            menu_price.setText(menuOld.price1.toInt().toString())
            menu_limit.setText(menuOld.limit.toString())
        }

        if (menuOld.image != "") {

            if (isValidContextForGlide(this)) {

                Glide
                        .with(this)
                        .load(menuOld.image)
                        .apply(RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                        )
                        .into(image)

                progress.visibility = View.GONE

            }

        } else {

            progress.visibility = View.GONE

        }

    }

    private fun onClickListener() {

        upload_image.setOnClickListener {
            chooseImage()
        }

        btn_submit.setOnClickListener {

            if (menuOld.id == "") addMenu() else updateMenu()

        }
    }

    @SuppressLint("CheckResult")
    private fun addMenu() {

        if (menu_price.text.toString() != "") {

            btn_submit.startAnimation()

            val menu = Menu()

            menu.id = getPushKey
            menu.type = menuOld.type

            menu.available = switch_available.isChecked
            menu.show = switch_show.isChecked

            menu.image = imageUrl
            menu.name = menu_name.text.toString().toUpperCase()
            menu.description = menu_content.text.toString()
            menu.price1 = menu_price.text.toString().toFloat()
            menu.limit = if (menu_limit.text.toString() != "") menu_limit.text.toString().toInt() else 0

            getMenu(menu.id).rxSetValue(menu).subscribe {

                toast("Menu Added")

                Handler().postDelayed({ finish() }, 2000)

            }

        } else {
            toast("please add price1")
        }
    }

    @SuppressLint("CheckResult")
    private fun updateMenu() {

        if (menu_price.text.toString() != "") {

            btn_submit.startAnimation()

            val menu = HashMap<String, Any>()

            menu["available"] = switch_available.isChecked
            menu["show"] = switch_show.isChecked

            menu["title"] = menu_name.text.toString().toUpperCase()
            menu["description"] = menu_content.text.toString()
            menu["limit"] = if (menu_limit.text.toString() != "") menu_limit.text.toString().toInt() else 0
            menu["price2"] = menu_price.text.toString().toFloat()

            if (isUploadImage && imageUrl != "") {

                if (menuOld.image != "") {

                    val storageImage = FirebaseStorage.getInstance().reference.child(menuOld.image)

                    storageImage.downloadUrl.addOnSuccessListener {
                        storageImage.delete()
                    }

                }

                menu["image"] = imageUrl
            }

            getMenu(menuOld.id).rxUpdateChildren(menu).subscribe {

                toast("Updated")

                Handler().postDelayed({ finish() }, 2000)

            }

        } else {
            toast("please add price1")
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

            val filePath = data.data

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

    private fun uploadImage(compressor: ByteArray) {

        if (compressor.isNotEmpty()) {

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            imageUrl = "images/" + UUID.randomUUID().toString()

            val ref = FirebaseStorage.getInstance().reference.child(imageUrl)


            ref.putBytes(compressor)
                    .addOnSuccessListener {

                        it.storage.downloadUrl.addOnCompleteListener { data ->

                            isUploadImage = true

                            imageUrl = data.result.toString()

                            progressDialog.dismiss()

                            Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()

                        }

                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                                .totalByteCount
                        progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                    }
        }
    }
}
