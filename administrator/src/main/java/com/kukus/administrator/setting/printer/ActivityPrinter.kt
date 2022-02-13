package com.kukus.administrator.setting.printer

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.kukus.administrator.R
import com.kukus.administrator.printer.Printer
import com.kukus.library.Constant.Companion.getIP
import kotlinx.android.synthetic.main.activity_printer.*
import org.jetbrains.anko.UI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast


class ActivityPrinter : AppCompatActivity() {


    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var  printer : Printer
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_printer)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        printer = Printer(this)

        getStatus()

        btnSave.setOnClickListener {

            if(etIp.text.length > 8) {

                printer.setIP(etIp.text.toString())

                toast("saved!")

            }else{

                toast("please enter ip address!")

            }

        }

        btnSampleReceipt.setOnClickListener {

            toast("print")

            printer.testPrint()

        }

        txtRefresh.setOnClickListener {

            toast("refresh")

            txtStatus.text = "---"

            printer.refresh()

            getStatus()

        }

    }

    private fun getStatus(){


        Handler().postDelayed({

            UI {
                etIp.setText(getIP(sharedPreferences))
                txtStatus.text = printer.getStatus()
            }

        }, 1000)
    }


}