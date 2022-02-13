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
import com.kukus.library.Constant
import com.kukus.library.Constant.Companion.TYPE_ORDER
import com.kukus.library.FirebaseUtils.Companion.getMenu
import com.kukus.library.FirebaseUtils.Companion.getPushKey
import kotlinx.android.synthetic.main.activity_form_drink.*
import com.kukus.library.model.Menu
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


class ActivityFormDrink : AppCompatActivity() {

    private var imageUrl = ""
    private val PICK_IMAGE_REQUEST = 71
    private var isUploadImage = false
    private val menuOld = Menu()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_form_drink)

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

        if(menuOld.limit != 0) {
            menu_limit.setText(menuOld.limit.toString())
        }

        if (menuOld.image != "") {

            if (Constant.isValidContextForGlide(this)) {

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

        if (menuOld.price1 != 0f) {

            checkBox_price1.isChecked = true

            label_price1.isEnabled = true
            label_price1.setText(menuOld.label_price1)

            et_price1.setBackgroundResource(R.drawable.border_grey_5)
            et_price1.isEnabled = true
            et_price1.setText(menuOld.price1.toInt().toString())


        } else {

            checkBox_price1.isChecked = false

            label_price1.isEnabled = false

            et_price1.setBackgroundResource(R.drawable.shape_radius_grey_5)
            et_price1.isEnabled = false

        }

        if (menuOld.price2 != 0f) {

            checkBox_price2.isChecked = true

            label_price2.isEnabled = true
            label_price2.setText(menuOld.label_price2)

            et_price2.setBackgroundResource(R.drawable.border_grey_5)
            et_price2.isEnabled = true
            et_price2.setText(menuOld.price2.toInt().toString())


        } else {

            checkBox_price2.isChecked = false

            label_price2.isEnabled = false

            et_price2.setBackgroundResource(R.drawable.shape_radius_grey_5)
            et_price2.isEnabled = false

        }

    }

    private fun onClickListener() {

        upload_image.setOnClickListener {
            chooseImage()
        }

        checkBox_price1.setOnCheckedChangeListener { compoundButton, _ ->

            if (compoundButton.isChecked) {

                label_price1.isEnabled = true

                et_price1.setBackgroundResource(R.drawable.border_grey_5)
                et_price1.isEnabled = true

            } else {

                label_price1.isEnabled = false

                et_price1.setBackgroundResource(R.drawable.shape_radius_grey_5)
                et_price1.isEnabled = false

            }


        }

        checkBox_price2.setOnCheckedChangeListener { compoundButton, _ ->

            if (compoundButton.isChecked) {

                label_price2.isEnabled = true

                et_price2.setBackgroundResource(R.drawable.border_grey_5)
                et_price2.isEnabled = true

            } else {

                label_price2.isEnabled = false

                et_price2.setBackgroundResource(R.drawable.shape_radius_grey_5)
                et_price2.isEnabled = false

            }
        }

        btn_submit.setOnClickListener {

            if (menuOld.id == "") addMenu() else updateMenu()

        }
    }

    @SuppressLint("CheckResult")
    private fun addMenu() {

        val label_1 = label_price1.text.toString()
        val label_2 = label_price2.text.toString()

        val price_1 = et_price1.text.toString()
        val price_2 = et_price2.text.toString()

        if ((checkBox_price1.isChecked && price_1 != "" && label_1 != "") || (checkBox_price2.isChecked && price_2 != "" && label_2 != "")) {

            btn_submit.startAnimation()

            val menu = Menu()

            menu.id = getPushKey
            menu.type = menuOld.type

            menu.available = switch_available.isChecked
            menu.show = switch_show.isChecked

            menu.image = imageUrl
            menu.name = menu_name.text.toString().toUpperCase()
            menu.limit = if (menu_limit.text.toString() != "") menu_limit.text.toString().toInt() else 0

            if (checkBox_price1.isChecked && price_1 != "" && label_1 != "") {

                menu.label_price1 = label_1.toUpperCase()
                menu.price1 = price_1.toFloat()

            }

            if (checkBox_price2.isChecked && price_2 != "" && label_2 != "") {

                menu.label_price2 = label_2.toUpperCase()
                menu.price2 = price_2.toFloat()

            }

            getMenu(menu.id).rxSetValue(menu).subscribe {

                toast("Drink Added")

                Handler().postDelayed({ finish() }, 2000)

            }
        } else {
            toast("please add price1")
        }
    }

    @SuppressLint("CheckResult")
    private fun updateMenu() {

        val label_1 = label_price1.text.toString()
        val label_2 = label_price2.text.toString()

        val price_1 = et_price1.text.toString()
        val price_2 = et_price2.text.toString()

        if ((checkBox_price1.isChecked && price_1 != "" && label_1 != "") || (checkBox_price2.isChecked && price_2 != "" && label_2 != "")) {

            btn_submit.startAnimation()

            val menu = HashMap<String, Any>()

            menu["available"] = switch_available.isChecked
            menu["show"] = switch_show.isChecked

            menu["title"] = menu_name.text.toString().toUpperCase()
            menu["limit"] = if (menu_limit.text.toString() != "") menu_limit.text.toString().toInt() else 0

            if (isUploadImage && imageUrl != "") {

                if (menuOld.image != "") {

                    val storageImage = FirebaseStorage.getInstance().reference.child(menuOld.image)

                    storageImage.downloadUrl.addOnSuccessListener {
                        storageImage.delete()
                    }

                }

                menu["image"] = imageUrl
            }

            if (checkBox_price1.isChecked && price_1 != "" && label_1 != "") {

                menu["label_price1"] = label_1.toUpperCase()
                menu["price2"] = price_1.toFloat()

            } else {

                menu["label_price1"] = ""
                menu["price2"] = 0f

            }

            if (checkBox_price2.isChecked && price_2 != "" && label_2 != "") {

                menu["label_price2"] = label_2.toUpperCase()
                menu["price2"] = price_2.toFloat()

            } else {

                menu["label_price2"] = ""
                menu["price2"] = 0f

            }

            getMenu(menuOld.id).rxUpdateChildren(menu).subscribe {

                toast("Updated")

                Handler().postDelayed({ finish() }, 2000)

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
