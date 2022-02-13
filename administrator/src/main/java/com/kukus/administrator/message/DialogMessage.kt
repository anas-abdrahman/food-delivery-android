package com.kukus.administrator.message


import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.kukus.administrator.R
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getContact
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getPushKey
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.model.Message
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*


class DialogMessage : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewRoot = inflater.inflate(R.layout.dialog_message, container, false)

        val et_title = viewRoot.find<EditText>(R.id.et_title)
        val et_email = viewRoot.find<EditText>(R.id.et_email)
        val et_message = viewRoot.find<EditText>(R.id.et_message)
        val submit = viewRoot.find<Button>(R.id.btn_send)
        val replay = viewRoot.find<Button>(R.id.btn_replay)
        val txt_date = viewRoot.find<TextView>(R.id.txt_date)

        val ex_userId = arguments?.getString("userId", "") ?: ""
        val ex_title = arguments?.getString("title", "")?: ""
        val ex_email = arguments?.getString("email", "") ?: ""
        val ex_message = arguments?.getString("message", "") ?: ""
        val ex_date = arguments?.getLong("date")
        val show = arguments?.getBoolean("show", false) ?: false

        et_title.setText(ex_title)
        et_email.setText(ex_email)
        et_message.setText(ex_message)

        txt_date.text = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(getTimestamp)

        replay.setOnClickListener {

            if(ex_title.contains("Re: ")){
                et_title.setText(ex_title)
            }else{
                et_title.setText("Re: $ex_title")
            }

            et_message.isEnabled = true
            et_message.setText("")

            //et_title.isEnabled = true

            submit.visibility = View.VISIBLE
            replay.visibility = View.GONE

        }

        if(show){

            txt_date.text = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(ex_date)

            et_email.visibility = View.GONE
            et_title.isEnabled = false
            et_email.isEnabled = false
            et_message.isEnabled = false

            if(ex_userId == "admin"){
                replay.visibility = View.GONE

            }else{
                replay.visibility = View.VISIBLE
            }

            submit.visibility = View.GONE

        }

        submit.setOnClickListener {

            if(et_title.text.isNotEmpty() && et_message.text.isNotEmpty()){

                val key = getPushKey

                getContact(ex_userId).child(key).setValue(
                        Message(key,
                                "admin",
                                ex_userId,
                                et_email.text.toString(),
                                et_title.text.toString(),
                                et_message.text.toString(),
                                false,
                                Date().time
                        ))

                context?.toast("your have message send successfully")

                Handler().postDelayed({
                    dismiss()
                }, 1000)


            }else{

                context?.toast("please field empty form")
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