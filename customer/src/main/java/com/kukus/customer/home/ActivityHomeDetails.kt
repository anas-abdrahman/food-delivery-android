package com.kukus.customer.home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.kukus.customer.R
import com.kukus.library.Constant.Companion.isValidContextForGlide
import kotlinx.android.synthetic.main.activity_menu_details.*
import org.jetbrains.anko.toast
import java.lang.Exception

class ActivityHomeDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_details)

        val title = intent?.extras?.getString("title", "")
        val description = intent?.extras?.getString("description", "")
        val label1 = intent?.extras?.getString("label1", "")
        val label2 = intent?.extras?.getString("label2", "")
        val price1 = intent?.extras?.getString("price1", "")
        val price2 = intent?.extras?.getString("price2", "")
        val image = intent?.extras?.getString("image", "")

        text_name.text = title
        text_description.text = description

        if(label1 != ""){

            layout_price1.visibility = View.VISIBLE
            txt_label_1.text = ("$label1 :")
            txt_price_1.text = price1

        }

        if(label2 != ""){

            layout_price2.visibility = View.VISIBLE
            txt_label_2.text = ("$label2 :")
            txt_price_2.text = price2

        }

        if (image != "") {

            if (isValidContextForGlide(this)) {

                Glide
                        .with(this)
                        .load(image)
                        .apply(RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                        )
                        .into(image_menu)

                progressbar.visibility = View.GONE

            }

        } else {

            progressbar.visibility = View.GONE

        }

    }
}
