package com.kukus.administrator.dialog


import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.androidhuman.rxfirebase2.database.data
import com.kukus.administrator.R
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getStaff
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.FirebaseUtils.Companion.getWallet
import com.kukus.library.model.Staff
import com.kukus.library.model.User
import com.kukus.library.model.Wallet
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.toast


class DialogPayWallet : DialogFragment() {


    var totalAmoaut = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewRoot = inflater.inflate(R.layout.dialog_pay_wallet, container, false)

        val layout = viewRoot.find<LinearLayout>(R.id.layout)
        val etAmount = viewRoot.find<EditText>(R.id.et_amount)
        val amount = viewRoot.find<TextView>(R.id.amount)
        val submit = viewRoot.find<Button>(R.id.submit)


        val staffID = arguments?.getString("staffID") ?: ""

        fun checkWallet(): Boolean {

            if (etAmount.text.toString() == "") etAmount.setText("0")

            totalAmoaut = amount.text.toString().toFloat()

            val balance = totalAmoaut - etAmount.text.toString().toInt()

            // 5 - 30 = -25

            if (balance >= 0) {

                return true

            }

            return false

        }

        doAsync {

            getStaff(staffID).data().subscribe{ it->

                if (it.exists()) {

                    val user = it.getValue(User::class.java)

                    if (user != null) {


                        if(user.wallet > 0) {

                            amount.text = (user.wallet.toString())

                        }else{

                            layout.visibility = View.GONE
                            etAmount.isEnabled = false
                            etAmount.setText("Sorry, the staff don't have Any Wallet")

                            submit.isEnabled = false
                            submit.visibility = View.GONE

                        }
                    }
                }
            }
        }

        submit.setOnClickListener {

            if (checkWallet()) {

                getStaff(staffID).data().subscribe { userSnapshot ->

                    if (userSnapshot.exists()) {

                        val user = userSnapshot.getValue(Staff::class.java)

                        if (user != null) {

                            val pay = etAmount.text.toString().toFloat()
                            val update = HashMap<String, Any>()

                            update["wallet"] = totalAmoaut - pay

                            getStaff(staffID).updateChildren(update).addOnCompleteListener {

                                getWallet(user.id).push().setValue(Wallet(user.id, pay, "out", true, "pay salary", getTimestamp))

                                Handler().postDelayed({

                                    //viewRoot.find<ProgressBar>(R.id.progress).visibility = View.GONE
                                    //viewRoot.find<ImageView>(R.id.success).visibility = View.VISIBLE

                                    context!!.toast("Pay successfully")

                                }, 1000)


                                Handler().postDelayed({

                                    dismiss()

                                }, 2500)


                            }

                        }
                    }
                }


            } else {
                context?.toast("please enter amount")
            }

        }

        return viewRoot
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

}