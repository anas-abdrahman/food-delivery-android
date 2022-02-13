package com.kukus.administrator.dialog


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.androidhuman.rxfirebase2.database.data
import com.kukus.administrator.R
import com.kukus.administrator.waiter.ActivityWaiter
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getStaff
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.FirebaseUtils.Companion.getWallet
import com.kukus.library.model.Staff
import com.kukus.library.model.Table
import com.kukus.library.model.User
import com.kukus.library.model.Wallet
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.toast


class DialogAddDiscount : DialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewRoot = inflater.inflate(R.layout.dialog_discount, container, false)

        val etAmount = viewRoot.find<EditText>(R.id.et_amount)
        val img_success = viewRoot.find<ImageView>(R.id.img_success)
        val txt_status = viewRoot.find<TextView>(R.id.txt_status)
        val txt_total = viewRoot.find<TextView>(R.id.txt_total)
        val progressbar = viewRoot.find<ProgressBar>(R.id.progressbar)
        val btn_submit = viewRoot.find<Button>(R.id.btn_submit)

        val id = arguments?.getString("id") ?: ""
        val total = arguments?.getString("total") ?: ""
        val ship_id = arguments?.getString("ship_id") ?: ""

        txt_total.text = total

        etAmount.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if(checkNumber(etAmount.text.toString())){

                    val amount = Integer.parseInt(etAmount.text.toString())

                    if(amount >= total.toInt()){
                        txt_total.text = ((amount - total.toInt()).toString())
                    }else{
                        txt_total.text = total
                    }

                }
            }
        })

        btn_submit.setOnClickListener {

            progressbar.visibility = View.VISIBLE
            txt_status.visibility = View.VISIBLE

            val update = HashMap<String, Any>()
            update["status"] = Constant.Companion.STATUS.COMPLETE
            update["discount"] = etAmount.text.toString().toInt()

            getWaiter(getDate).child(id).updateChildren(update).addOnCompleteListener {

                if (it.isSuccessful) {

                    Handler().postDelayed({

                        img_success.visibility = View.VISIBLE
                        progressbar.visibility = View.INVISIBLE
                        txt_status.text = "success"

                        dismiss()
                        removeTable(ship_id)

                    }, 1000)

                }
            }

        }

        return viewRoot
    }

    fun checkNumber(string: String) : Boolean {

        var numeric = true

        try {
            Integer.parseInt(string)
        } catch (e: NumberFormatException) {
            numeric = false
        }

        return numeric
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
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}