package com.kukus.customer.point

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.customer.R
import com.kukus.customer.dialog.DialogPromo
import com.kukus.customer.dialog.DialogVoucher
import com.kukus.customer.point.adapter.PointAdapter
import com.kukus.customer.reward.ActivityReward
import com.kukus.library.FirebaseUtils.Companion.getPoint
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Point
import com.kukus.library.model.User
import kotlinx.android.synthetic.main.activity_point.*
import org.jetbrains.anko.doAsync


class ActivityPoint : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val mPointAdapter = PointAdapter()

        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        recycleView.layoutManager = linearLayoutManager
        recycleView.isNestedScrollingEnabled = false
        recycleView.setItemViewCacheSize(30)
        recycleView.adapter = mPointAdapter

        reward_point.setOnClickListener {

            startActivity(Intent(this, ActivityReward::class.java))

        }
        reward_voucher.setOnClickListener {

            val fragmentManager = supportFragmentManager
            val newFragment = DialogVoucher()
            val transaction = fragmentManager?.beginTransaction()

            if (transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()
            }

        }
        reward_promo.setOnClickListener {

            val fragmentManager = supportFragmentManager
            val newFragment = DialogPromo()
            val transaction = fragmentManager?.beginTransaction()

            if (transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

            }

        }

        doAsync {

            getUser(getUserId).dataChanges().subscribe {

                if (it.exists()) {

                    val user = it.getValue(User::class.java)

                    if (user != null) {

                        if (user.id == getUserId) {

                            point.text = StringBuilder("${user.point}")

                        }

                    }

                }

            }

            getPoint(getUserId).dataChanges().subscribe {

                setViewVisible(isProgress = true)

                if (it.exists()) {

                    val listPoint = arrayListOf<Point>()

                    it.children.forEach { data ->

                        val point = data.getValue(Point::class.java)

                        if (point != null) {

                            if (point.user_id == getUserId) {

                                listPoint.add(point)

                            }
                        }
                    }

                    if (listPoint.size > 0) {

                        mPointAdapter.swapAdapter(listPoint)
                        mPointAdapter.notifyDataSetChanged()

                        setViewVisible(true)


                    } else {

                        setViewVisible(false)

                    }


                } else {

                    setViewVisible(false)

                }

            }
        }

    }


    private fun setViewVisible(isVisible: Boolean = false, isProgress: Boolean = false) {

        progressbar.visibility = View.VISIBLE
        recycleView.visibility = View.GONE
        empty.visibility = View.GONE

        if(!isProgress) {

            Handler().postDelayed({

                if (isVisible) {

                    recycleView.visibility = View.VISIBLE
                    empty.visibility = View.GONE

                } else {

                    recycleView.visibility = View.GONE
                    empty.visibility = View.VISIBLE

                }

                progressbar.visibility = View.GONE


            }, 1500)
        }
    }

}