package com.kukus.customer.intro

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.kukus.customer.R
import com.kukus.customer.home.ActivityHome
import com.kukus.customer.login.ActivityLogin
import com.kukus.customer.login.ActivityRegister
import com.kukus.library.Constant
import com.kukus.library.Constant.Companion.getDataUser
import com.kukus.library.FirebaseUtils.Companion.getUserCurrent

class ActivitySplash : AppCompatActivity() {

    var isFirst = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

    }

    override fun onResume() {

        super.onResume()

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), Constant.LOCATION_PERMISSION_REQUEST_CODE)
            return

        }else{

            splash()

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {

            Constant.LOCATION_PERMISSION_REQUEST_CODE -> {

                //if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {}

                splash()

            }
        }
    }

    private fun splash() {

        if(isFirst) {

            isFirst = false

            Handler().postDelayed({

                val intent = if (getUserCurrent != null) {

                    val user = getDataUser(this)

                    if (user.name == "" || user.email == "") {

                        Intent(this, ActivityRegister::class.java)

                    } else {

                        Intent(this, ActivityHome::class.java)

                    }

                } else {

                    Intent(this, ActivityLogin::class.java)

                }

                startActivity(intent)

                finish()

            }, 1500)

        }

    }

}
