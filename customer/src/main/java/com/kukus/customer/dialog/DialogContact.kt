package com.kukus.customer.dialog


import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.kukus.customer.R
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils.Companion.getContact
import com.kukus.library.FirebaseUtils.Companion.getPushKey
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Message
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.util.*


class DialogContact : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewRoot = inflater.inflate(R.layout.dialog_contact, container, false)

        val et_title = viewRoot.find<EditText>(R.id.et_title)
        val et_email = viewRoot.find<EditText>(R.id.et_email)
        val et_message = viewRoot.find<EditText>(R.id.et_message)
        val replay = viewRoot.find<Button>(R.id.btn_replay)
        val submit = viewRoot.find<Button>(R.id.btn_send)

        val ex_title = arguments?.getString("title", "") ?: ""
        val ex_email = arguments?.getString("email", "") ?: ""
        val ex_user_id = arguments?.getString("user_id", "") ?: ""
        val ex_message = arguments?.getString("message", "") ?: ""
        val show = arguments?.getBoolean("show", false) ?: false


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

            et_title.setText(ex_title)
            et_email.setText(ex_email)
            et_message.setText(ex_message)

            et_title.isEnabled = false
            et_email.isEnabled = false
            et_message.isEnabled = false

            et_email.visibility = View.GONE

            if(ex_user_id == getUserId){
                replay.visibility = View.GONE
            }else{
                replay.visibility = View.VISIBLE
            }

            submit.visibility = View.GONE

        }else{

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

            val name = sharedPreferences.getString(Constant.EXTRA_USER_NAME,"")
            val email = sharedPreferences.getString(Constant.EXTRA_USER_EMAIL, "")

            //et_title.setText(name)
            et_email.setText(email)
            et_email.visibility = View.GONE


        }

        submit.setOnClickListener {

            if(et_title.text.isNotEmpty() && et_message.text.isNotEmpty()){

                val key = getPushKey

                getContact(getUserId).child(key).setValue(
                        Message(key,
                                getUserId,
                                "admin",
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