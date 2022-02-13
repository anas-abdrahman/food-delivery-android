package com.kukus.customer.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import com.androidhuman.rxfirebase2.database.data
import com.kukus.customer.R
import com.kukus.customer.home.ActivityHome
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.User
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast

class ActivityRegister : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        btn_submit.setOnClickListener {

            if (checkForm()) {

                getUserRegister()

            }
        }

        et_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_title.setBackgroundResource(R.drawable.shape_radius_grey)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        et_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                et_email.setBackgroundResource(R.drawable.shape_radius_grey)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

    }

    @SuppressLint("CheckResult")
    private fun getUserRegister() {

        btn_submit.startAnimation()

        getUser(getUserId).data().subscribe { dataRef, _ ->

            if (dataRef.exists()) {

                val user = dataRef.getValue(User::class.java)

                if (user != null) {

                    toast("Register Successfully")

                    user.name = et_title.text.toString()
                    user.email = et_email.text.toString()

                    getUser(getUserId).setValue(user)

                    Handler().postDelayed({

                        val intent = Intent(this, ActivityHome::class.java)
                        startActivity(intent)

                        finish()

                    }, 1000)

                }
            }
        }
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun checkForm(): Boolean {

        error.visibility = View.GONE
        error.text = ""

        val name = et_title.text.toString()
        val email = et_email.text.toString()

        if (name.isNotEmpty() && isValidEmail(email)) {

            return true

        } else {

            if (et_title.text.isEmpty()) et_title.setBackgroundResource(R.drawable.shape_radius_red)
            if (!isValidEmail(et_email.text)) et_email.setBackgroundResource(R.drawable.shape_radius_red)

            error.visibility = View.VISIBLE
            error.text = ("Please field empty form")
        }

        return false
    }
}
