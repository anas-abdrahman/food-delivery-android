package com.kukus.administrator.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.kukus.administrator.R
import com.kukus.library.FirebaseUtils.Companion.getUserRef
import com.kukus.library.model.User
import kotlinx.android.synthetic.main.activity_user.*
import android.graphics.Color
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.text.format.DateFormat
import com.androidhuman.rxfirebase2.database.rxUpdateChildren
import com.kukus.library.FirebaseUtils
import org.jetbrains.anko.toast

class ActivityUserList : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        contact.visibility = View.GONE

        displayNewsMessage()

    }

    private fun displayNewsMessage(){

        val options = FirebaseListOptions.Builder<User>()
                .setLayout(R.layout.list_contact)
                .setQuery(getUserRef, User::class.java)
                .setLifecycleOwner(this)
                .build()

        val mAdapter = object : FirebaseListAdapter<User>(options) {

            override fun populateView(v: View, model: User, position: Int) {

                empty.visibility = View.GONE

                val layout = v.findViewById(R.id.layout) as ConstraintLayout
                val name = v.findViewById(R.id.name) as TextView
                val email = v.findViewById(R.id.email) as TextView
                val status = v.findViewById(R.id.read) as TextView
                val date = v.findViewById(R.id.txt_date) as TextView

                name.text = model.name
                email.text = model.email
                date.text = ("last Login : " + DateFormat.format("dd-MM-yyyy", model.login_at))

                if(model.block){
                    status.text = "BLOCK"
                    status.setTextColor(Color.parseColor("#ef554a"))
                }else{
                    status.text = "ACTIVE"
                    status.setTextColor(Color.parseColor("#32c360"))
                }

                layout.setOnClickListener {
                    //val intent = Intent(this@ActivityUserList, ActivityUser::class.java)
                    //intent.putExtra("userId", model.id)
                    //startActivity(intent)

                    val menus = arrayOf("Block")

                    val builder = AlertDialog.Builder(this@ActivityUserList)
                    builder.setTitle("User")
                    builder.setItems(menus) { _, which ->
                        when (which)
                        {
                            /*0 -> {
                                val intent = Intent(this@ActivityUserList, ActivityUser::class.java)
                                intent.putExtra("userName", model.title)
                                intent.putExtra("userId", model.id)
                                startActivity(intent)
                            }*/
                            0 -> {

                                val user = FirebaseUtils.getUser(model.id)
                                val update = mapOf("block" to !model.block)

                                user.rxUpdateChildren(update).subscribe {

                                    Handler().postDelayed({

                                        toast(if(!model.block) "block" else "active")

                                    }, 500)
                                }


                            }
                        }
                    }

                    builder.show()
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
