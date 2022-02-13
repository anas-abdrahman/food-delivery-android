package com.kukus.administrator.dialog


import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.androidhuman.rxfirebase2.database.data
import com.kukus.administrator.R
import com.kukus.administrator.printer.Printer
import com.kukus.administrator.waiter.tab.OrderAdapterCount
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.library.ExpandableHeightListView
import com.kukus.library.model.Table
import com.kukus.library.model.Waiter
import org.jetbrains.anko.find
import java.lang.Integer.parseInt
import java.util.*


class DialogCheckout : DialogFragment() {

    var checkDiscount = false

    @SuppressLint("CheckResult")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewRoot = inflater.inflate(R.layout.dialog_checkout, container, false)

        val order_id = arguments?.getString("id") ?: ""
        val date = arguments?.getString("date") ?: ""

        getOrderList(viewRoot, order_id)

        return viewRoot

    }

    fun checkNumber(string: String): Boolean {

        var numeric = true

        try {
            parseInt(string)
        } catch (e: NumberFormatException) {
            numeric = false
        }

        return numeric
    }


    @SuppressLint("CheckResult")
    private fun getOrderList(viewRoot: View, id: String) {

        val print = Printer(context!!)

        val etAmount = viewRoot.find<EditText>(R.id.et_amount)
        val txtBalance = viewRoot.find<TextView>(R.id.txt_balance)
        val etDiscount = viewRoot.find<EditText>(R.id.et_discount)
        val switch_discount = viewRoot.find<Switch>(R.id.switch_discount)
        val txtTotal = viewRoot.find<TextView>(R.id.txt_total)
        val txt_amount = viewRoot.find<TextView>(R.id.txt_amount)
        val progressBar = viewRoot.find<ProgressBar>(R.id.progressBar)

        getWaiter(getDate).child(id).data().subscribe { data ->

            if (data.exists()) {

                val waiter = data.getValue(Waiter::class.java)

                if (waiter != null) {

                    etAmount.addTextChangedListener(object : TextWatcher {

                        override fun afterTextChanged(p0: Editable?) {}
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                            if (checkNumber(etAmount.text.toString())) {

                                val amount = if(etAmount.text.toString() == "") 0 else etAmount.text.toString().toInt()
                                var total = waiter.price.toInt()
                                val discount = if(etDiscount.text.toString() == "") 0 else etDiscount.text.toString().toInt()

                                if (switch_discount.isChecked) {

                                    total = (total - discount)

                                }

                                if (amount >= total && discount in 0..total) {

                                    txtBalance.text = ("Balance : LE ${(amount - total)}")

                                } else {

                                    txtBalance.text = ("")

                                }

                                //txt_amount.text = total.toDouble().toString()

                            }
                        }
                    })

                    etDiscount.addTextChangedListener(object : TextWatcher {

                        override fun afterTextChanged(p0: Editable?) {}
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                            var total = waiter.price.toInt()
                            var discount = if(etDiscount.text.toString() == "") 0 else etDiscount.text.toString().toInt()
                            val amount = if(etAmount.text.toString() == "") 0 else etAmount.text.toString().toInt()

                            if (switch_discount.isChecked) {

                                if (total >= discount) {

                                    total -= discount
                                    txt_amount.text = (total).toDouble().toString()
                                    etDiscount.setBackgroundResource(R.drawable.shape_radius_green)
                                    checkDiscount = true

                                } else {

                                    txt_amount.text = total.toDouble().toString()
                                    etDiscount.setBackgroundResource(R.drawable.shape_radius_red)
                                    checkDiscount = false

                                }

                                if (amount >= total && discount in 0..total) {

                                    txtBalance.text = ("Balance : LE ${(amount - total)}")

                                } else {

                                    txtBalance.text = ("")

                                }

                            }

                        }

                    })

                    viewRoot.find<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    viewRoot.find<TextView>(R.id.txt_amount).visibility = View.VISIBLE
                    viewRoot.find<TextView>(R.id.txt_amount).text = waiter.price.toString()
                    txtTotal.text = "LE ${waiter.price}"


                    viewRoot.find<ExpandableHeightListView>(R.id.list_order).adapter = OrderAdapterCount(context!!, Constant.getOrderList(waiter.menu, false), false)


                    viewRoot.find<Switch>(R.id.switch_discount).setOnCheckedChangeListener { compoundButton, b ->

                        val total = waiter.price.toInt()
                        val amount = if(etAmount.text.toString() == "") 0 else etAmount.text.toString().toInt()

                        if (compoundButton.isChecked) {

                            etDiscount.isEnabled = true
                            etDiscount.setBackgroundResource(R.drawable.shape_radius_green)

                        } else {

                            etDiscount.isEnabled = false
                            etDiscount.setText("")
                            etDiscount.setBackgroundResource(R.drawable.shape_radius_grey)
                            checkDiscount = false

                            txt_amount.text = total.toDouble().toString()

                            if (amount >= waiter.price.toInt()) {

                                txtBalance.text = ("Balance : LE ${(amount - total)}")

                            } else {

                                txtBalance.text = ("")
                            }
                        }

                    }

                    /* button pay */
                    viewRoot.find<Button>(R.id.btn_pay).setOnClickListener {

                        progressBar.visibility = View.VISIBLE

                        val update = HashMap<String, Any>()
                        update["status"] = Constant.Companion.STATUS.COMPLETE

                        val finalDiscount = if(checkDiscount){
                            etDiscount.text.toString().toInt()
                        } else {
                            0
                        }

                        update["discount"] = finalDiscount


                        getWaiter(getDate).child(id).updateChildren(update).addOnCompleteListener {

                            if (it.isSuccessful) {

                                waiter.discount = finalDiscount

                                print.printReceiptCustomer(waiter)
                                removeTable(waiter.ship_id)

                                Handler().postDelayed({

                                    viewRoot.find<ProgressBar>(R.id.progressBar).visibility = View.GONE
                                    //viewRoot.find<ImageView>(R.id.success).visibility = View.VISIBLE

                                }, 1000)


                                Handler().postDelayed({

                                    dismiss()

                                }, 2000)

                            }
                        }
                    }

                   /* *//* button amount *//*
                    viewRoot.find<Button>(R.id.btn_discount).setOnClickListener {

                        val fragmentManager = (context as FragmentActivity).supportFragmentManager
                        val newFragment = DialogAddDiscount()
                        val bundle = Bundle()

                        bundle.putString("id", waiter.id)
                        bundle.putString("ship_id", waiter.ship_id)
                        bundle.putString("total", waiter.price.toInt().toString())

                        newFragment.arguments = bundle

                        val transaction = fragmentManager?.beginTransaction()

                        if (transaction != null) {
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()
                        }
                    }*/

                }
            }

        }

    }

    @SuppressLint("CheckResult")
    fun removeTable(shipId: String) {

        FirebaseUtils.getTableRef.data().subscribe { checkTable ->

            if (checkTable.exists()) {

                val myTable = checkTable.getValue(Table::class.java)

                if (myTable != null) {

                    if (myTable.order_1 == shipId) {
                        FirebaseUtils.getTableRef.child("table_1").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_1").setValue("")
                    }
                    if (myTable.order_2 == shipId) {
                        FirebaseUtils.getTableRef.child("table_2").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_2").setValue("")
                    }
                    if (myTable.order_3 == shipId) {
                        FirebaseUtils.getTableRef.child("table_3").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_3").setValue("")
                    }
                    if (myTable.order_4 == shipId) {
                        FirebaseUtils.getTableRef.child("table_4").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_4").setValue("")
                    }
                    if (myTable.order_5 == shipId) {
                        FirebaseUtils.getTableRef.child("table_5").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_5").setValue("")
                    }
                    if (myTable.order_6 == shipId) {
                        FirebaseUtils.getTableRef.child("table_6").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_6").setValue("")
                    }
                    if (myTable.order_7 == shipId) {
                        FirebaseUtils.getTableRef.child("table_7").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_7").setValue("")
                    }
                    if (myTable.order_8 == shipId) {
                        FirebaseUtils.getTableRef.child("table_8").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_8").setValue("")
                    }
                    if (myTable.order_9 == shipId) {
                        FirebaseUtils.getTableRef.child("table_9").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_9").setValue("")
                    }
                    if (myTable.order_10 == shipId) {
                        FirebaseUtils.getTableRef.child("table_10").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_10").setValue("")
                    }
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        return dialog
    }

}