package com.kukus.administrator.staff

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.kukus.administrator.R
import kotlinx.android.synthetic.main.activity_add_staff.*

class ActivityEditStaff : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_staff)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        val name = intent.extras.getString("title")
        val email = intent.extras.getString("email")
        val tel = intent.extras.getString("tel")
        val pass = intent.extras.getString("pass")

        et_name.setText(name)
        et_email.setText(email)
        et_no_tel.setText(tel)
        et_password.setText(pass)
        et_password_conform.setText(pass)

    }
}
