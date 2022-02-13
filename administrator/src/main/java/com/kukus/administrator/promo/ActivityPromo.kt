package com.kukus.administrator.promo

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.administrator.R
import com.kukus.administrator.promo.adapter.PromoAdapter
import com.kukus.library.FirebaseUtils.Companion.getPromoRef
import com.kukus.library.model.Promo
import kotlinx.android.synthetic.main.activity_promo.*
import org.jetbrains.anko.doAsync

class ActivityPromo : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val mWalletAdapter = PromoAdapter()

        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        recycleView.layoutManager = linearLayoutManager
        recycleView.isNestedScrollingEnabled = false
        recycleView.setItemViewCacheSize(30)
        recycleView.adapter = mWalletAdapter

        val promoRef = getPromoRef


        add_voucher.setOnClickListener {


            val fragmentManager = supportFragmentManager
            val newFragment = DialogAddPromo()

            if ( newFragment.dialog != null ) newFragment.dialog.setCanceledOnTouchOutside(true);
            val transaction = fragmentManager?.beginTransaction()

            if(transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

            }

        }

        doAsync {

            promoRef.dataChanges().subscribe {

                setViewVisible(isProgress = true)

                if (it.exists()) {

                    val listPromo = arrayListOf<Promo>()

                    it.children.forEach { data ->

                        val promo = data.getValue(Promo::class.java)

                        if (promo != null) {


                            listPromo.add(Promo(promo.id, promo.code, promo.user, promo.amount, promo.createAt, promo.expireAt, promo.active, promo.limit,promo.currentLimit ))


                        }
                    }

                    if (listPromo.size > 0) {

                        mWalletAdapter.swapAdapter(listPromo)
                        mWalletAdapter.notifyDataSetChanged()

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

        if (!isProgress) {

            Handler().postDelayed({


                if (isVisible) {

                    recycleView.visibility = View.VISIBLE
                    empty.visibility = View.GONE

                } else {

                    recycleView.visibility = View.GONE
                    empty.visibility = View.VISIBLE

                }

                progressbar.visibility = View.GONE


            }, 1000)
        }
    }

}