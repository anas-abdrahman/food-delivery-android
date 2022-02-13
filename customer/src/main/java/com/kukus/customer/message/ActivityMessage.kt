package com.kukus.customer.message

import android.annotation.SuppressLint
import android.app.FragmentTransaction
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.kukus.customer.R
import com.kukus.customer.dialog.DialogContact
import com.kukus.library.FirebaseUtils.Companion.getContact
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Message
import kotlinx.android.synthetic.main.activity_message.*


class ActivityMessage : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        displayNewsMessage()

        contact.setOnClickListener {

            val fragmentManager = supportFragmentManager
            val newFragment = DialogContact()

            val bundle = Bundle()
            bundle.putBoolean("show", false)

            newFragment.arguments = bundle

            val transaction = fragmentManager?.beginTransaction()

            if (transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

            }
        }
    }

    private fun displayNewsMessage(){

        val options = FirebaseListOptions.Builder<Message>()
                .setLayout(R.layout.list_message)
                .setQuery(getContact(getUserId), Message::class.java)
                .setLifecycleOwner(this)
                .build()

        val mAdapter = object : FirebaseListAdapter<Message>(options) {

            override fun getItem(position: Int): Message {
                return super.getItem(count - 1 - position)
            }

            override fun populateView(v: View, model: Message, position: Int) {

                empty.visibility = View.GONE
                progressbar.visibility = View.GONE

                val layout = v.findViewById(R.id.layout) as ConstraintLayout
                val name = v.findViewById(R.id.name) as TextView
                val image = v.findViewById(R.id.img) as ImageView
                val message = v.findViewById(R.id.message) as TextView
                val date = v.findViewById(R.id.date) as TextView
                val read = v.findViewById(R.id.read) as TextView
                val inbox = v.findViewById(R.id.inbox) as TextView

                name.text = model.title
                message.text = model.text
                date.text = DateFormat.format("dd-MM-yyyy", model.time).toString()

                if(!model.read && model.user_to == getUserId){
                    read.visibility = View.VISIBLE
                }else{
                    read.visibility = View.GONE
                }

                if(model.user_to == getUserId){
                    image.setImageResource(R.drawable.ic_inbox)
                    inbox.setText("inbox")
                    inbox.setTextColor(Color.parseColor("#444444"))

                }else{
                    image.setImageResource(R.drawable.ic_outbox)
                    inbox.setText("send")
                    inbox.setTextColor(Color.parseColor("#2E87E6"))
                }

                if(!model.read && model.user_to == getUserId)
                {
                    inbox.setText("NEW")
                    inbox.setTextColor(Color.parseColor("#32c360"))
                }

                layout.setOnClickListener {

                    val fragmentManager = supportFragmentManager
                    val newFragment = DialogContact()

                    val bundle = Bundle()
                    bundle.putString("title", model.title)
                    bundle.putString("email", model.email)
                    bundle.putString("message", model.text)
                    bundle.putString("user_id", model.user_id)
                    bundle.putBoolean("show", true)

                    newFragment.arguments = bundle

                    val transaction = fragmentManager?.beginTransaction()

                    if (transaction != null) {
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

                        if(!model.read && model.user_to == getUserId){
                            getContact(getUserId).child(model.id).child("read").setValue(true)
                        }

                    }
                }
            }
        }

        list_of_news.adapter = mAdapter

        Handler().postDelayed({

            if(mAdapter.count == 0){

                progressbar.visibility = View.GONE
                empty.visibility = View.VISIBLE

            }

        }, 1000)


    }
}
