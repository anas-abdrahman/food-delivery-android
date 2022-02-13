package com.kukus.customer.dialog


import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.androidhuman.rxfirebase2.database.data
import com.kukus.customer.R
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.FirebaseUtils.Companion.getVoucherRef
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.FirebaseUtils.Companion.getWallet
import com.kukus.library.model.Voucher
import com.kukus.library.model.User
import com.kukus.library.model.Wallet
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.util.*


class DialogVoucher : DialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewRoot = inflater.inflate(R.layout.dialog_voucher, container, false)
        val code = viewRoot.find<EditText>(R.id.code)
        val close = viewRoot.find<ImageView>(R.id.close)
        val image = viewRoot.find<ImageView>(R.id.image_gift)
        val progress = viewRoot.find<ProgressBar>(R.id.progressbar_promo)
        val complete = viewRoot.find<ImageView>(R.id.image_complate)
        val valid = viewRoot.find<TextView>(R.id.valid)

        val codePromo = arguments?.getString("code") ?: ""


        if (codePromo != "") {

            code.setText(codePromo)
            code.setBackgroundResource(R.drawable.shape_radius_green)

        }



        close.setOnClickListener {

            dismiss()
        }

        code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                code.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                code.setBackgroundResource(R.drawable.shape_radius_grey)
                valid.visibility = View.GONE
                valid.text = ""
            }

        })

        code.filters = arrayOf<InputFilter>(InputFilter.AllCaps())

        viewRoot.find<Button>(R.id.submit).setOnClickListener {

            complete.visibility = View.GONE
            progress.visibility = View.VISIBLE
            image.visibility = View.GONE

            getVoucherRef.data().subscribe({

                var found = false

                it.children.forEach { value ->

                    val voucher = value.getValue(Voucher::class.java)

                    if (voucher != null) {

                        var userNotFound = true

                        voucher.user.forEach {userId ->

                            if(getUserId == userId) {
                                userNotFound = false
                            }

                        }

                        if(userNotFound) {

                            if (voucher.code == code.text.toString() && voucher.active) {

                                if (voucher.expireAt > System.currentTimeMillis()) {

                                    if (voucher.currentLimit < voucher.limit) {

                                        found = true

                                        getUser(getUserId).data().subscribe({ data ->

                                            if (data.exists()) {

                                                val user = data.getValue(User::class.java)

                                                if (user != null) {

                                                    activity?.toast("Congratulations!! Vouchertion code is valid")

                                                    val userList = arrayListOf<String>()

                                                    voucher.user.forEach { userData ->

                                                        userList.add(userData)

                                                    }

                                                    userList.add(user.id)

                                                    value.ref.child("currentLimit").setValue((voucher.currentLimit) + 1)
                                                    data.ref.child("wallet").setValue(user.wallet + voucher.amount)
                                                    value.ref.child("user").setValue(userList)

                                                    getWallet(getUserId).push().setValue(Wallet(getUserId, voucher.amount.toFloat(), "in", true, "reward voucher (${voucher.code})", getTimestamp))


                                                    Handler().postDelayed({

                                                        progress.visibility = View.GONE
                                                        complete.visibility = View.VISIBLE
                                                        image.visibility = View.GONE

                                                        code.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_white, 0, R.drawable.ic_done_green, 0)
                                                        code.setBackgroundResource(R.drawable.shape_radius_green)


                                                    }, 1000)

                                                    Handler().postDelayed({

                                                        dismiss()

                                                    }, 2000)


                                                }

                                            } else {

                                                progress.visibility = View.GONE
                                                complete.visibility = View.GONE
                                                image.visibility = View.VISIBLE

                                            }

                                        }) {}

                                    } else {
                                        activity?.toast("promo has limit...")
                                    }


                                } else {

                                    activity?.toast("promo code expire...")

                                }

                            }

                        }else{

                            activity?.toast("already use...")

                        }

                    }


                }


                if (!found) {

                    Handler().postDelayed({

                        progress.visibility = View.GONE
                        complete.visibility = View.GONE
                        image.visibility = View.VISIBLE

                        code.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_white, 0, R.drawable.ic_close, 0)
                        code.setBackgroundResource(R.drawable.shape_radius_red)

                        valid.visibility = View.VISIBLE
                        valid.text = "Voucher code not valid"

                    }, 1000)

                }


            }) {}


        }

        return viewRoot
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

}