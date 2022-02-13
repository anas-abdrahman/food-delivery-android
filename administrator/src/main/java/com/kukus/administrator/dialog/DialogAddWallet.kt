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
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getStaff
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.FirebaseUtils.Companion.getWallet
import com.kukus.library.model.Staff
import com.kukus.library.model.User
import com.kukus.library.model.Wallet
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.toast


class DialogAddWallet : DialogFragment() {

    var jumlah = 0f
    var jumlahWallet = 0f
    var walletCheck = (0f)
    var wallet = (0f)
    var amountEditText = 0
    var isWallet = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewRoot = inflater.inflate(R.layout.dialog_wallet, container, false)
        val etAmount = viewRoot.find<EditText>(R.id.et_amount)
        val amount = viewRoot.find<TextView>(R.id.amount)
        val wallet_amount = viewRoot.find<TextView>(R.id.wallet_amount)
        val submit = viewRoot.find<Button>(R.id.submit)
        val switch = viewRoot.find<Switch>(R.id.switch_wallet)

        val userShip = arguments?.getString("user") ?: ""
        val shipId = arguments?.getString("shipId") ?: ""
        val date = arguments?.getString("date") ?: ""
        val id = arguments?.getString("id") ?: ""
        var userprice = arguments?.getFloat("price2", 0f)
        val pricetotal = arguments?.getFloat("pricetotal", 0f)
        val delivery = arguments?.getInt("delivery", 0)

        jumlah = pricetotal!!
        jumlahWallet = wallet

        switch.setOnCheckedChangeListener { compoundButton, b ->

            if (compoundButton.isChecked) {

                wallet_amount.setTextColor(Color.parseColor("#32c360"))

                if (wallet >= pricetotal) {

                    jumlah = 0f
                    jumlahWallet = (wallet - pricetotal)

                    amount.setText(jumlah.toString())
                    wallet_amount.setText("LE " + (jumlahWallet).toString())

                } else {

                    jumlah = pricetotal - wallet.toInt()
                    jumlahWallet = 0f

                    amount.setText(jumlah.toString())
                    wallet_amount.setText("LE " + (jumlahWallet).toString())

                }

            } else {

                wallet_amount.setTextColor(Color.GRAY)

                jumlah = pricetotal
                jumlahWallet = wallet

                amount.setText(jumlah.toString())
                wallet_amount.setText("LE " + (jumlahWallet).toString())
            }


        }

        fun checkWallet(): Boolean {

            if (etAmount.text.toString() == "") etAmount.setText("0")

            val balance = etAmount.text.toString().toInt() - pricetotal

            // 100

            // 5 - 30 = -25

            if (balance < 0) {

                if (switch.isChecked) {

                    viewRoot.find<ProgressBar>(R.id.progressbar).visibility = View.GONE

                    return when {
                        wallet < Math.abs(balance) -> {

                            wallet_amount.setTextColor(Color.RED)
                            activity?.toast("your wallet not enough")
                            false

                        }

                        else -> {

                            wallet_amount.setTextColor(Color.GREEN)
                            true

                        }
                    }

                } else {
                    activity?.toast("not enough")
                    return false
                }

            } else {

                wallet_amount.setTextColor(Color.BLACK)
            }

            return true
        }


        submit.setOnClickListener {


            if (checkWallet()) {

                val update = HashMap<String, Any>()
                update["status"] = Constant.Companion.STATUS.COMPLETE

                viewRoot.find<ProgressBar>(R.id.progressbar).visibility = View.VISIBLE

                getShip(date).child(id).updateChildren(update).addOnCompleteListener {

                    if (it.isSuccessful) {

                        getUser(userShip).data().subscribe { userSnapshot ->

                            if (userSnapshot.exists()) {

                                val user = userSnapshot.getValue(User::class.java)

                                if (user != null) {

                                    var balance: Float
                                    val updatePoint = HashMap<String, Any>()

                                    if (switch.isChecked) {

                                        if (etAmount.text.toString().toInt() == 0) {

                                            balance = pricetotal - user.wallet

                                            if (balance <= 0) {
                                                updatePoint["wallet"] = Math.abs(balance)
                                            }

                                        } else {

                                            balance = (etAmount.text.toString().toInt() - pricetotal) + user.wallet.toInt()
                                            updatePoint["wallet"] = user.wallet + balance
                                        }

                                    } else {

                                        balance = etAmount.text.toString().toInt() - pricetotal

                                        updatePoint["wallet"] = user.wallet + balance

                                    }

                                    updatePoint["point"] = (pricetotal.div(10)) + user.point

                                    getUser(userShip).updateChildren(updatePoint).addOnCompleteListener {


                                        val walletUserRef = getWallet(user.id)
                                        if (balance > 0) {
                                            walletUserRef.push().setValue(Wallet(user.id, balance.toFloat(), "in", true, "credit delivery for order #$shipId", FirebaseUtils.getTimestamp))
                                        } else {
                                            if (switch.isChecked) walletUserRef.push().setValue(Wallet(user.id, Math.abs(balance).toFloat(), "out", true, "credit delivery for order #$shipId", FirebaseUtils.getTimestamp))
                                        }

                                        getStaff(getUserId).data().subscribe({ walletStaff ->

                                            if (walletStaff.exists()) {

                                                val staff = walletStaff.getValue(Staff::class.java)
                                                var charge = 0f

                                                if (staff != null) {
                                                    charge = staff.wallet + delivery!!.toFloat()
                                                }

                                                getStaff(getUserId).child("wallet").setValue(charge)

                                                val walletRef = getWallet(getUserId)
                                                walletRef.push().setValue(Wallet(getUserId, delivery!!.toFloat(), "in", true, "credit delivery for order #$shipId", FirebaseUtils.getTimestamp)).addOnCompleteListener {

                                                    Handler().postDelayed({

                                                        viewRoot.find<ProgressBar>(R.id.progressbar).visibility = View.GONE
                                                        viewRoot.find<ImageView>(R.id.success).visibility = View.VISIBLE

                                                    }, 1000)


                                                    Handler().postDelayed({

                                                        dismiss()
                                                        activity?.finish()

                                                    }, 2500)

                                                }


                                            }
                                        }) {}


                                    }


                                }
                            }
                        }


                    }
                }

            } else {
                context?.toast("please enter amount")
            }

        }

        doAsync {

            getUser(userShip).data().subscribe({

                if (it.exists()) {

                    val user = it.getValue(User::class.java)

                    if (user != null) {
                        wallet = user.wallet
                        walletCheck = user.wallet
                        wallet_amount.setText("LE " + user.wallet.toString())
                        amount.setText(pricetotal.toString())
                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
    }

}