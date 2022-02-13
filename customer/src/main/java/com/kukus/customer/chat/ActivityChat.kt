package com.kukus.customer.chat

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import com.androidhuman.rxfirebase2.database.dataChanges
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.view.RxView
import com.kukus.customer.R
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getChatRef
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.FirebaseUtils.Companion.getUserRef
import com.kukus.library.model.Chat
import com.kukus.library.model.User
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.doAsync

class ActivityChat : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var username = "Guest"

        doAsync {


            getUserRef.dataChanges().subscribe {

                if (it.exists()) {

                    it.children.forEach { value ->

                        val user = value.getValue(User::class.java)

                        if (user != null) {

                            if (FirebaseAuth.getInstance().currentUser?.uid == user.id) {
                                username = user.name
                            }

                        }
                    }
                }
            }
        }

        RxView.clicks(fab)
                .filter {
                    input.text.isNotBlank() && input.text.isNotEmpty()
                }
                .subscribe {



                    getChatRef.push().setValue(
                            Chat
                            (
                                    userId      = getUserId,
                                    messageText = input.text.toString(),
                                    messageUser = username
                            )
                    )
                    input.setText("")

                }


        displayChatMessage()

        Handler().postDelayed({
            progressbar.visibility = View.GONE
        }, 1000)

    }

    private fun displayChatMessage(){

        val options = FirebaseListOptions.Builder<Chat>()
                .setLayout(R.layout.list_chat)
                .setQuery(getChatRef, Chat::class.java)
                .setLifecycleOwner(this)
                .build()

        val mAdapter = object : FirebaseListAdapter<Chat>(options) {

            override fun populateView(v: View, model: Chat, position: Int) {

                val messageText = v.findViewById(R.id.message_text) as TextView
                val messageUser = v.findViewById(R.id.message_user) as TextView
                val messageTime = v.findViewById(R.id.message_time) as TextView
                val admin = v.findViewById(R.id.txt_admin) as TextView

                if(model.role == "admin"){
                    admin.visibility = View.VISIBLE
                    admin.text = "ADMIN"
                    admin.setBackgroundResource(R.drawable.shape_radius_red_3)

                }else if(model.role == "delivery"){
                    admin.visibility = View.VISIBLE
                    admin.text = "DELIVERY"
                    admin.setBackgroundResource(R.drawable.shape_radius_black_3)
                }else{
                    admin.visibility = View.GONE
                }

                if(getUserId == model.userId)
                {

                    messageText.setBackgroundResource(R.drawable.shape_radius_red_5)
                    messageText.setTextColor(Color.WHITE)

                    messageUser.setTextColor(Color.parseColor("#F44336"))
                    messageTime.setTextColor(Color.parseColor("#F44336"))

                }else{

                    messageText.setBackgroundResource(R.drawable.shape_radius_grey_5)
                    messageText.setTextColor(Color.BLACK)

                    messageUser.setTextColor(Color.GRAY)
                    messageTime.setTextColor(Color.GRAY)

                }

                // Set their text
                messageText.text = model.messageText
                messageUser.text = model.messageUser.toUpperCase()

                // Format the date before showing it
                messageTime.text = DateFormat.format("dd/MM/yyyy - HH:mm:ss",  model.messageTime)

                progressbar.visibility = View.GONE

            }
        }

        list_of_messages.adapter = mAdapter
        list_of_messages.setSelection(mAdapter.count)

    }


}
