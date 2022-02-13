package com.kukus.customer.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.jakewharton.rxbinding2.view.RxView
import com.kukus.customer.R
import com.kukus.customer.login.ActivityLogin.Companion.activityLogin
import com.kukus.customer.home.ActivityHome
import kotlinx.android.synthetic.main.activity_verify.*
import java.util.concurrent.TimeUnit
import android.os.CountDownTimer
import android.preference.PreferenceManager
import com.androidhuman.rxfirebase2.database.data
import com.kukus.library.Constant.Companion.getData
import com.kukus.library.Constant.Companion.setData
import com.kukus.library.Constant.Companion.setDataUser
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserAuth
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.User
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import java.util.*


class ActivityVerify : AppCompatActivity() {

    enum class CodeStatus {
        Loading,
        Complete,
        Pending,
        Error
    }

    private var number = ""
    private var verify = ""
    private var limitResend = 3

    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)



        number = intent.getStringExtra("number")
        verify = getData(applicationContext, "verify_id")

        setNumberVerification()
        setOnClickListener()

    }

    @SuppressLint("CheckResult")
    private fun setOnClickListener() {

        txt_number.text = number

        RxView.clicks(txt_resend)
                .filter {

                    limitResend--

                    if (limitResend > 0) {
                        true
                    } else {
                        toast("Please Try again after 30 minute")
                        false
                    }
                }
                .subscribe {

                    toast("Resend Successfully ($limitResend)")
                    setNumberVerification()

                }


        pinView.setPinViewEventListener { pin, _ ->

            if (pin.value.length == 6) {

                btn_submit.setBackgroundResource(R.color.colorGreen)
                btn_submit.isEnabled = true

            } else {
                btn_submit.setBackgroundResource(R.color.colorGrey)
                btn_submit.isEnabled = false
            }

        }

        btn_submit.setOnClickListener {

            verifyCode(pinView.value)

        }
    }

    private fun setNumberVerification() {

        setVisible(CodeStatus.Loading)

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,              // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,    // Unit of timeout
                this,                // Activity (for callback binding)
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    override fun onCodeSent(verifyId: String, token: PhoneAuthProvider.ForceResendingToken?) {

                        verify = verifyId

                        setData(sharedPreferences, "verify_id", verifyId)

                        longToast("send code")

                    }

                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                        val code = credential.smsCode

                        if (code != null) {

                            pinView.value = code

                            verifyCode(code)

                        }

                    }

                    override fun onVerificationFailed(e: FirebaseException) {

                        val text: String

                        when (e) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                text = "Invalid request"
                                toast(text) // kalu nomber salah
                            }
                            is FirebaseTooManyRequestsException -> {
                                text = "The SMS quota for the project has been exceeded"
                                toast(text)
                            }
                            is FirebaseNetworkException -> {
                                text = "Please check your Internet connection!"
                                toast(text)
                            }
                            else -> {
                                text = e.toString()
                                toast(text)
                            }
                        }

                        setVisible(CodeStatus.Error, text)

                    }
                })

    }

    private fun verifyCode(code: String) {


        if (verify != "") {

            btn_submit.startAnimation()

            val credential = PhoneAuthProvider.getCredential(verify, code)
            signInWithCredential(credential)

        } else {

            toast("auth failed")

        }
    }

    @SuppressLint("CheckResult")
    private fun signInWithCredential(credential: AuthCredential) {

        getUserAuth.signInWithCredential(credential).addOnCompleteListener {

            if (it.isSuccessful) {

                setVisible(CodeStatus.Complete)

                getUser(getUserId).data().subscribe { userRef ->

                    if (userRef.exists()) {

                        val user = userRef.getValue(User::class.java)

                        if (user != null) {


                            if (!user.block) {

                                getUser(getUserId).child("login_at").setValue(Date().time).addOnCompleteListener {

                                    if (user.name != "" && user.email != "") {

                                        onCompleteLogin(false)


                                    } else {

                                        onCompleteLogin(true)

                                    }


                                }

                            } else {

                                longToast("Your Account Is Blocked!")

                                getUserAuth.signOut()
                                startActivity(Intent(this, ActivityLogin::class.java))
                                finish()

                            }

                        } else {

                            registerNewUser()

                        }

                    } else {

                        registerNewUser()

                    }

                }

            } else {

                setVisible(CodeStatus.Error, it.exception?.message ?: "")

            }

        }
    }

    private fun registerNewUser() {

        val user = User()

        user.id = getUserId
        user.mobile = number
        user.level = 0
        user.point = 0
        user.walletId = FirebaseUtils.createRandomNumber(8)
        user.wallet = 0.0f
        user.created_at = Date().time
        user.login_at = Date().time
        user.refId = FirebaseUtils.createRandomCode(6)

        setDataUser(this, user)

        getUser(getUserId).setValue(user).addOnCompleteListener {
            onCompleteLogin(true)
        }

    }

    private fun onCompleteLogin(isNewUser: Boolean) {

        Handler().postDelayed({

            btn_submit.revertAnimation()

            setData(sharedPreferences, "verify_id", "")

            val intent = if (isNewUser) {
                Intent(this, ActivityRegister::class.java)
            } else {
                Intent(this, ActivityHome::class.java)
            }

            startActivity(intent)

            if (!activityLogin.isDestroyed) activityLogin.finish()

            finish()


        }, 1000)

    }

    lateinit var countDownTimer: CountDownTimer

    private fun setVisible(position: CodeStatus, text: String = "") {

        txt_error.text = text

        when (position) {

            CodeStatus.Loading -> {

                iv_complete.visibility = View.GONE
                iv_error.visibility = View.GONE
                txt_error.visibility = View.GONE
                txt_resend.visibility = View.GONE
                txt_countdown.visibility = View.VISIBLE

                countDownTimer = object : CountDownTimer(60000, 1000) {

                    override fun onTick(millisUntilFinished: Long) {

                        val count = (millisUntilFinished / 1000).toString()
                        val text_count = if (count.length == 1) "0" + count else count

                        txt_countdown.text = ("00:$text_count")
                    }

                    override fun onFinish() {

                        txt_countdown.text = ("00:00").toString()

                        setVisible(CodeStatus.Pending)
                    }

                }

                countDownTimer.start()

            }

            CodeStatus.Complete -> {

                iv_complete.visibility = View.VISIBLE
                iv_error.visibility = View.GONE
                txt_error.visibility = View.GONE

                txt_countdown.visibility = View.GONE
                txt_resend.visibility = View.GONE

                countDownTimer.cancel()

            }

            CodeStatus.Pending -> {

                txt_countdown.visibility = View.GONE
                txt_resend.visibility = View.VISIBLE

            }

            CodeStatus.Error -> {

                btn_submit.revertAnimation()

                iv_complete.visibility = View.GONE
                iv_error.visibility = View.VISIBLE
                txt_error.visibility = View.VISIBLE
                txt_error.text = text

                txt_countdown.visibility = View.GONE
                txt_resend.visibility = View.VISIBLE

                countDownTimer.cancel()

            }
        }
    }
}
