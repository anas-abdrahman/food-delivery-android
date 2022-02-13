package com.kukus.customer.about

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.kukus.customer.R
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.toast
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class ActivityAbout : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)


        btn_whatsapp.setOnClickListener {

            val contact = "+201100355679" // use country code with your phone number
            val url = "https://api.whatsapp.com/send?phone=$contact"
            try {
                val pm = packageManager
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } catch (e: PackageManager.NameNotFoundException) {
                toast("WhatsApp app not installed in your phone")
                e.printStackTrace()
            }

        }

        btn_facebook.setOnClickListener {
            startActivity(getOpenFacebookIntent())
        }

        btn_call.setOnClickListener {

            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:+201100355679")

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
                return@setOnClickListener
            }

            startActivity(callIntent)
        }
    }

    fun getOpenFacebookIntent(): Intent {

        return try {
            packageManager.getPackageInfo("com.facebook.katana", 0)
            Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100011379319732"))
        } catch (e: Exception) {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/nasikukus.ayamrempah"))
        }

    }
}
