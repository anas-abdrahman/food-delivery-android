package com.kukus.administrator.news

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.jakewharton.rxbinding2.view.RxView
import com.kukus.administrator.R
import com.kukus.library.Constant.Companion.isValidContextForGlide
import com.kukus.library.FirebaseUtils.Companion.getNewsRef
import com.kukus.library.model.News
import kotlinx.android.synthetic.main.activity_news.*

class ActivityNews : AppCompatActivity()
{

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        displayNewsMessage()

                RxView.clicks(add_news)
                .subscribe {

                    val intent = Intent(this, ActivityAddNews::class.java)
                    startActivity(intent)

                }

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

                val btn_edit = v.findViewById(R.id.btn_edit) as ImageButton
                val image = v.findViewById(R.id.image) as ImageView
                val progress = v.findViewById(R.id.progress) as ProgressBar
                val date = v.findViewById(R.id.txt_date) as TextView

                if (model.image != "" && isValidContextForGlide(applicationContext)) {

                    Glide
                            .with(applicationContext)
                            .load(model.image)
                            .into(image)

                    progress.visibility = View.GONE

                }else{
                    progress.visibility = View.GONE
                }


                date.text = DateFormat.format("dd/MM/yyyy - HH:mm:ss",  model.date)

                btn_edit.setOnClickListener {
                    val intent = Intent(this@ActivityNews, ActivityEditNews::class.java)
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
