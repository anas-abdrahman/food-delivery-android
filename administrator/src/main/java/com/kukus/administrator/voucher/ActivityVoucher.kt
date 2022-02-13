package com.kukus.administrator.voucher

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.administrator.R
import com.kukus.administrator.voucher.adapter.VoucherAdapter
import com.kukus.library.FirebaseUtils.Companion.getVoucherRef
import com.kukus.library.model.Voucher
import kotlinx.android.synthetic.main.activity_voucher.*
import org.jetbrains.anko.doAsync

class ActivityVoucher : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voucher)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val mWalletAdapter = VoucherAdapter()

        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        recycleView.layoutManager = linearLayoutManager
        recycleView.isNestedScrollingEnabled = false
        recycleView.setItemViewCacheSize(30)
        recycleView.adapter = mWalletAdapter


        add_voucher.setOnClickListener {


            val fragmentManager = supportFragmentManager
            val newFragment = DialogAddVoucher()

            if ( newFragment.dialog != null ) newFragment.dialog.setCanceledOnTouchOutside(true);
            val transaction = fragmentManager?.beginTransaction()

            if(transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

            }

        }

        doAsync {

            getVoucherRef.dataChanges().subscribe {

                setViewVisible(isProgress = true)

                if (it.exists()) {

                    val listVoucher = arrayListOf<Voucher>()

                    it.children.forEach { data ->

                        val voucher = data.getValue(Voucher::class.java)

                        if (voucher != null) {


                            listVoucher.add(Voucher(voucher.id, voucher.code, voucher.user, voucher.amount, voucher.createAt, voucher.expireAt, voucher.active, voucher.limit,voucher.currentLimit))


                        }
                    }

                    if (listVoucher.size > 0) {

                        mWalletAdapter.swapAdapter(listVoucher)
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