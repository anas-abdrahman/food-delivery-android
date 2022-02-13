package com.kukus.customer.news

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.jakewharton.rxbinding2.view.RxView
import com.kukus.customer.R
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getNews
import com.kukus.library.FirebaseUtils.Companion.getNewsRef
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.model.News
import kotlinx.android.synthetic.main.activity_news.*


class ActivityNews : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        displayNewsMessage()

    }

    private fun displayNewsMessage(){

        val options = FirebaseListOptions.Builder<News>()
                .setLayout(R.layout.list_news)
                .setQuery(getNewsRef, News::class.java)
                .setLifecycleOwner(this)
                .build()

        val mAdapter = object : FirebaseListAdapter<News>(options) {

            override fun populateView(v: View, model: News, position: Int) {

                empty.visibility = View.GONE


                val close = v.findViewById(R.id.close) as TextView
                val image = v.findViewById(R.id.image) as ImageView

                close.visibility = View.GONE

                if (model.image != "" && Constant.isValidContextForGlide(applicationContext)) {

                    Glide
                            .with(applicationContext)
                            .load(model.image)
                            .into(image)

                }

                image.setOnClickListener {
                    val intent = Intent(this@ActivityNews, ActivityNewsDetails::class.java)
                    intent.putExtra("id", model.id)
                    intent.putExtra("image", model.image)
                    intent.putExtra("date", model.date)
                    startActivity(intent)
                }
            }

            override fun getItem(position: Int): News {
                return super.getItem(count - 1 - position)
            }
        }
        
        if(mAdapter.isEmpty){

            progressbar.visibility = View.GONE

            empty.visibility = View.VISIBLE


        }

        list_of_news.adapter = mAdapter

    }
}
