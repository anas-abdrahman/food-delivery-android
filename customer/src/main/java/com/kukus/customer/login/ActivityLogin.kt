package com.kukus.customer.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import com.kukus.customer.R
import com.kukus.customer.home.ActivityHome
import com.kukus.library.Constant.Companion.getDataUser
import com.kukus.library.FirebaseUtils.Companion.getUserCurrent
import com.kukus.library.FirebaseUtils.Companion.getUserId
import kotlinx.android.synthetic.main.activity_login.*


class ActivityLogin : AppCompatActivity() {

    companion object { lateinit var activityLogin: ActivityLogin }

    init { activityLogin = this }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        et_tel.editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {

                if (et_tel.isValid) {
                    et_tel.editText.setBackgroundResource(R.drawable.shape_radius_green)

                } else {

                    et_tel.editText.setBackgroundResource(R.drawable.shape_radius_grey)
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        btn_login.setOnClickListener {

            if (et_tel.isValid && et_tel.phoneNumber.isNotEmpty()) {

                btn_login.startAnimation()

                Handler().postDelayed({

                    runOnUiThread {
                        btn_login.revertAnimation()
                    }

                    val intent = Intent(this, ActivityVerify::class.java)

                    intent.putExtra("number", et_tel.phoneNumber)

                    startActivity(intent)

                }, 1000)

            } else {

                et_tel.editText.setBackgroundResource(R.drawable.shape_radius_red)
                //et_tel.setError("phone number")

            }
        }
    }
}
