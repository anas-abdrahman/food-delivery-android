package com.kukus.administrator.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.androidhuman.rxfirebase2.database.data
import com.google.firebase.auth.FirebaseAuth
import com.kukus.administrator.R
import com.kukus.administrator.staff.ActivityAddStaff
import com.kukus.administrator.dashboard.DashboardAdmin
import com.kukus.administrator.dashboard.DashboardCashier
import com.kukus.administrator.dashboard.DashboardDelivery
import com.kukus.administrator.dashboard.DashboardWaiter
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getStaff
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.FirebaseUtils.Companion.getUserAuth
import com.kukus.library.FirebaseUtils.Companion.getUserCurrent
import com.kukus.library.model.Staff
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast

class ActivityLogin : AppCompatActivity() {

    companion object {
        lateinit var activityLogin: ActivityLogin
    }

    init {
        activityLogin = this

    }
    lateinit var mSharedPreferences: SharedPreferences

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        mSharedPreferences = getSharedPreferences("user-preference", Context.MODE_PRIVATE)


        signIn.setOnClickListener {

            if (checkForm()) {

                signIn.startAnimation()

                val email = email.text.toString()
                val password = password.text.toString()

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->

                    task.addOnSuccessListener {

                        val userId = FirebaseUtils.getUserId

                        val userRef = getStaff(userId)

                        doAsync {


                            userRef.data().subscribe ({

                                if (it.exists()) {

                                    val staff = it.getValue(Staff::class.java)

                                    if (staff != null) {

                                        if(!staff.block) {

                                            getStaff(staff.id).child("login_at").setValue(getTimestamp)

                                            val intent: Intent = when {

                                                staff.role == Constant.Companion.USER.ADMIN -> {
                                                    toast("admin")
                                                    mSharedPreferences.edit().putString("user_rule", Constant.Companion.USER.ADMIN.name).apply()
                                                    Intent(this@ActivityLogin, DashboardAdmin::class.java)
                                                }
                                                staff.role == Constant.Companion.USER.CASHIER -> {
                                                    toast("cashier")
                                                    mSharedPreferences.edit().putString("user_rule", Constant.Companion.USER.CASHIER.name).apply()
                                                    Intent(this@ActivityLogin, DashboardCashier::class.java)
                                                }
                                                staff.role == Constant.Companion.USER.DELIVERY -> {
                                                    toast("delivery")
                                                    mSharedPreferences.edit().putString("user_rule", Constant.Companion.USER.DELIVERY.name).apply()
                                                    Intent(this@ActivityLogin, DashboardDelivery::class.java)
                                                }
                                                staff.role == Constant.Companion.USER.WAITER -> {
                                                    toast("waiter")
                                                    mSharedPreferences.edit().putString("user_rule", Constant.Companion.USER.WAITER.name).apply()
                                                    Intent(this@ActivityLogin, DashboardWaiter::class.java)
                                                }
                                                else -> {
                                                    mSharedPreferences.edit().putString("user_rule", "").apply()
                                                    Intent()
                                                }
                                            }

                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            startActivity(intent)

                                            finish()

                                        }else{

                                            longToast("Your Account Is Blocked!")

                                            getUserAuth.signOut()
                                            mSharedPreferences.edit().putString("user_rule", "").apply()

                                            signIn.revertAnimation()

                                        }

                                    }

                                }

                            }){

                                Toast.makeText(baseContext, "Authentication failed...", Toast.LENGTH_SHORT).show()

                                signIn.revertAnimation()
                            }
                        }


                    }


                    task.addOnCanceledListener {

                        Toast.makeText(baseContext, "Authentication cancel.", Toast.LENGTH_SHORT).show()
                        signIn.revertAnimation()
                    }

                    task.addOnFailureListener {

                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        signIn.revertAnimation()
                    }

                }.addOnFailureListener {

                    toast("Authentication failed..")
                    it.message?.let { it1 -> toast(it1) }
                    signIn.revertAnimation()

                }.addOnCanceledListener {

                    toast("Authentication Cancel..")
                    signIn.revertAnimation()

                }


            }


        }

        //register.visibility = View.GONE
        register.setOnClickListener {

            val intent = Intent(this, ActivityAddStaff::class.java)
            intent.putExtra("role", Constant.Companion.USER.ADMIN)
            startActivity(intent)

        }

    }

    override fun onStart() {
        super.onStart()

        val rule = mSharedPreferences.getString("user_rule", "")

        if (getUserCurrent != null) {

            val intent : Intent

            when (rule) {
                Constant.Companion.USER.ADMIN.name -> {
                    intent = Intent(this, DashboardAdmin::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                Constant.Companion.USER.CASHIER.name -> {
                    intent = Intent(this, DashboardCashier::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                Constant.Companion.USER.DELIVERY.name -> {
                    intent = Intent(this, DashboardDelivery::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                Constant.Companion.USER.WAITER.name -> {
                    intent = Intent(this, DashboardWaiter::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }

        }

    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun checkForm(): Boolean {

        val mEmail = email.text.toString()
        val nPass = password.text.toString()

        if (isValidEmail(mEmail) && mEmail.isNotEmpty() && nPass.isNotEmpty()) {

            return true

        } else {

            if (!isValidEmail(mEmail)) email.setBackgroundResource(R.drawable.shape_radius_red)
            if (nPass.isEmpty()) password.setBackgroundResource(R.drawable.shape_radius_red)


            toast("please field empty form..")

        }


        return false
    }

    override fun onBackPressed() {

        if(signIn.isAnimating){

            signIn.revertAnimation()


        }else{

            super.onBackPressed()
            finish()
        }

    }
}
