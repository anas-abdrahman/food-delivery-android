package com.kukus.administrator.voucher


import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.androidhuman.rxfirebase2.database.rxSetValue
import com.kukus.administrator.R
import com.kukus.library.FirebaseUtils.Companion.createRandomCode
import com.kukus.library.FirebaseUtils.Companion.getPromoRef
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.FirebaseUtils.Companion.getVoucher
import com.kukus.library.model.Voucher
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.util.*


class DialogAddVoucher : DialogFragment() {

    var dateLong : Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewRoot = inflater.inflate(R.layout.dialog_voucher, container, false)
        val code = viewRoot.find<EditText>(R.id.code)
        val amount = viewRoot.find<EditText>(R.id.amount)
        val limit = viewRoot.find<EditText>(R.id.limit)
        val active = viewRoot.find<CheckBox>(R.id.active)
        val date = viewRoot.find<TextView>(R.id.txt_date)
        val generate = viewRoot.find<ImageButton>(R.id.generate_code)

        val vId = arguments?.getString("id") ?: ""
        val vCode = arguments?.getString("code") ?: ""
        val vPercent = arguments?.getInt("percent") ?: 0
        val vActive = arguments?.getBoolean("active") ?: true
        val vCreate = arguments?.getLong("create") ?: 0
        val vExpire = arguments?.getLong("expire") ?: 0
        val vLimit = arguments?.getInt("limit") ?: 0
        val vCurLimit = arguments?.getInt("currentLimit") ?: 0

        if(vCode != ""){

            code.setText(vCode)
            code.isEnabled = false
            generate.isEnabled = false

            amount.setText(vPercent.toString())
            limit.setText(vLimit.toString())

            dateLong = vExpire

            val mExpire = Date(dateLong)
            val mDay = DateFormat.format("dd-MM-yyyy", mExpire).toString()

            date.text = "$mDay"
            active.isChecked = vActive

        }else{
            code.setText(createRandomCode(6))
        }

        date.setOnClickListener {

            getDatePicker(date)

        }

        generate.setOnClickListener {

            code.setText(createRandomCode(6))

        }

        viewRoot.find<Button>(R.id.submit).setOnClickListener {

            val id = getPromoRef.push().key ?: getTimestamp.toString()

            val voucher = Voucher()

            voucher.id = if(vId != "") vId else id
            voucher.user = arrayListOf()
            voucher.active = active.isChecked
            voucher.amount = amount.text.toString().toInt()
            voucher.createAt = if(vCreate != 0.toLong()) vCreate else getTimestamp
            voucher.expireAt = dateLong
            voucher.currentLimit = vCurLimit
            voucher.code = code.text.toString()
            voucher.limit = limit.text.toString().toInt()

            getVoucher(code.text.toString()).rxSetValue(voucher).subscribe {

                this.dismiss()
                context?.toast("complate")


            }

        }

        return viewRoot
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }


    private fun getDatePicker(it: TextView) {

        val c = Calendar.getInstance()
        var mYear = c.get(Calendar.YEAR)
        var mMonth = c.get(Calendar.MONTH)
        var mDay = c.get(Calendar.DAY_OF_MONTH)


        if(dateLong != 0.toLong()){

            val mExpire = Date(dateLong)
            mDay = DateFormat.format("dd", mExpire).toString().toInt()
            mMonth = DateFormat.format("MM", mExpire).toString().toInt() - 1
            mYear = DateFormat.format("yyyy", mExpire).toString().toInt()

        }

        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfYear ->

            view.maxDate = dateLong

            var month   = (monthOfYear + 1).toString()
            var day     = (dayOfYear).toString()

            if (month.toInt() < 10) {

                month = "0" + month
            }

            if (day.toInt() < 10) {

                day = "0" + day
            }

            val date = ("$day-$month-$year")

            val calendar = GregorianCalendar(year, monthOfYear, dayOfYear)

            dateLong = calendar.timeInMillis

            it.text = date


        }, mYear, mMonth, mDay).show()

    }
}