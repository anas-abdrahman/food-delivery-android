package com.kukus.administrator.user

import android.annotation.SuppressLint
import android.app.FragmentTransaction
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.kukus.administrator.R
import com.kukus.administrator.message.DialogMessage
import com.kukus.library.FirebaseUtils.Companion.getContact
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Message
import kotlinx.android.synthetic.main.activity_user.*
import java.text.SimpleDateFormat
import java.util.*


class ActivityUser : AppCompatActivity() {
    var userName = ""
    var userId = ""

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        userName = intent.extras.getString("userName", "")
        userId = intent.extras.getString("userId", "")

        add_news.text = userName


        contact.setOnClickListener {

            val fragmentManager = supportFragmentManager
            val newFragment = DialogMessage()

            val bundle = Bundle()
            bundle.putBoolean("show", false)
            bundle.putString("title", userName)
            bundle.putString("userId", userId)

            newFragment.arguments = bundle

            val transaction = fragmentManager?.beginTransaction()

            if (transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

            }
        }

        displayNewsMessage()

    }

    private fun displayNewsMessage(){

        val options = FirebaseListOptions.Builder<Message>()
                .setLayout(R.layout.list_contact)
                .setQuery(getContact(userId), Message::class.java)
                .setLifecycleOwner(this)
                .build()

        val mAdapter = object : FirebaseListAdapter<Message>(options) {

            override fun populateView(v: View, model: Message, position: Int) {

                empty.visibility = View.GONE

                val layout = v.findViewById(R.id.layout) as ConstraintLayout
                val name = v.findViewById(R.id.name) as TextView
                val email = v.findViewById(R.id.email) as TextView
                val date = v.findViewById(R.id.txt_date) as TextView
                val read = v.findViewById(R.id.read) as TextView

                name.text = model.title
                email.text = model.text
                date.text = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(model.time)

                if(!model.read && model.user_to == "admin"){
                    read.visibility = View.VISIBLE
                }else{
                    read.visibility = View.GONE
                }

                layout.setOnClickListener {

                    val fragmentManager = supportFragmentManager
                    val newFragment = DialogMessage()

                    val bundle = Bundle()
                    bundle.putString("userId", model.user_id)
                    bundle.putString("title", model.title)
                    bundle.putString("email", model.email)
                    bundle.putString("message", model.text)
                    bundle.putBoolean("show", true)

                    newFragment.arguments = bundle

                    val transaction = fragmentManager?.beginTransaction()

                    if (transaction != null) {
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

                        if(!model.read && model.user_to == "admin"){
                            getContact(getUserId).child(model.id).child("read").setValue(true)
                        }

                    }

                }

            }



        }

        if(mAdapter.isEmpty){

            progressbar.visibility = View.GONE

            empty.visibility = View.VISIBLE


        }

        list_of_news.adapter = mAdapter

    }
}
