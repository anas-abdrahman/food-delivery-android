package com.kukus.administrator.staff

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.kukus.administrator.R
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getStaff
import com.kukus.library.model.Staff
import kotlinx.android.synthetic.main.activity_add_staff.*
import org.jetbrains.anko.toast

class ActivityAddStaff : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_staff)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        val role = intent.extras.getSerializable("role") as Constant.Companion.USER

        auth = FirebaseAuth.getInstance()

        btn_submit.setOnClickListener {

            val name = et_name.text.toString()
            val email = et_email.text.toString()
            val pass1 = et_password.text.toString()
            val pass2 = et_password_conform.text.toString()
            val tel = et_no_tel.text.toString()

            if (pass1 == pass2) {

                auth.createUserWithEmailAndPassword(email, pass1).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val staff = Staff()

                        staff.id = FirebaseUtils.getUserId
                        staff.name = name
                        staff.mobile = tel
                        staff.email = email
                        staff.pass = pass1
                        staff.role = role
                        staff.walletId = FirebaseUtils.createRandomNumber(8)

                        getStaff(staff.id).setValue(staff)

                        Handler().postDelayed({

                            finish()

                            toast("createUserWithEmail:success")

                        }, 1000)

                    } else {

                        toast("createUserWithEmail:failure")

                    }
                }

            }


        }
    }
}
