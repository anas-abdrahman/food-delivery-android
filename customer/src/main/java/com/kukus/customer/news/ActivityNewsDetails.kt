package com.kukus.customer.news

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kukus.customer.R
import com.kukus.library.Constant
import kotlinx.android.synthetic.main.activity_news_details.*


class ActivityNewsDetails : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)

        val xImage = intent.extras.getString("image")
        val xDate = intent.extras.getLong("date")

        if (xImage != "") {

            if (Constant.isValidContextForGlide(applicationContext)) {

                Glide
                        .with(applicationContext)
                        .load(xImage)
                        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(image)
            }
        }

        txt_date.text = DateFormat.format("dd/MM/yyyy - HH:mm:ss",  xDate)

    }
}
