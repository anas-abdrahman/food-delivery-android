package com.kukus.administrator.message

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.administrator.R
import com.kukus.library.FirebaseUtils
import com.kukus.library.model.Message
import kotlinx.android.synthetic.main.activity_message.*
import org.jetbrains.anko.doAsync

class ActivityMessage : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        displayNewsMessage()

    }

    private fun displayNewsMessage(){

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val mWalletAdapter = MassageAdapter()

        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setItemViewCacheSize(30)
        recyclerView.adapter = mWalletAdapter


        doAsync {

            FirebaseUtils.getContactRef.dataChanges().subscribe ({

                setViewVisible(isProgress = true)

                if (it.exists()) {

                    val listWallet = arrayListOf<Message>()

                    it.children.forEach { data ->

                        data?.children?.forEach { dataValue ->

                            val contact = dataValue.getValue(Message::class.java)

                            if (contact != null ) {

                                if(contact.user_to == "admin" || contact.user_id == "admin") {

                                    listWallet.add(contact)

                                }
                            }

                        }
                    }

                    if (listWallet.size > 0) {

                        mWalletAdapter.swapAdapter(listWallet)
                        mWalletAdapter.notifyDataSetChanged()

                        setViewVisible(true)


                    } else {

                        setViewVisible(false)

                    }


                } else {

                    setViewVisible(false)

                }

            }){}
        }

    }

    private fun setViewVisible(isVisible: Boolean = false, isProgress: Boolean = false) {

        progressbar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        empty.visibility = View.GONE

        if (!isProgress) {

            Handler().postDelayed({


                if (isVisible) {

                    recyclerView.visibility = View.VISIBLE
                    empty.visibility = View.GONE

                } else {

                    recyclerView.visibility = View.GONE
                    empty.visibility = View.VISIBLE

                }

                progressbar.visibility = View.GONE


            }, 500)
        }
    }
}
