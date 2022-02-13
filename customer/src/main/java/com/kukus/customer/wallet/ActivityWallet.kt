package com.kukus.customer.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.androidhuman.rxfirebase2.database.dataChanges
import com.google.zxing.WriterException
import com.kukus.customer.R
import com.kukus.customer.wallet.adapter.WalletAdapter
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.FirebaseUtils.Companion.getWallet
import com.kukus.library.model.User
import com.kukus.library.model.Wallet
import kotlinx.android.synthetic.main.activity_wallet.*
import org.jetbrains.anko.doAsync


class ActivityWallet : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        var smallerDimension = if (width < height) width else height

        smallerDimension = smallerDimension * 3 / 4

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val mWalletAdapter = WalletAdapter()

        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        recycleView.layoutManager = linearLayoutManager
        recycleView.isNestedScrollingEnabled = false
        recycleView.setItemViewCacheSize(30)
        recycleView.adapter = mWalletAdapter


        doAsync {

            getUser(getUserId).dataChanges().subscribe {

                if (it.exists()) {

                    val user = it.getValue(User::class.java)

                    if (user != null) {

                        if (user.id == getUserId) {

                            balance.text = StringBuilder("EGP ${user.wallet}")
                            code_wallet.text = "wallet id : " + user.walletId

                            val qrgEncoder = QRGEncoder(user.walletId, null, QRGContents.Type.TEXT, smallerDimension)

                            try {

                                qrcode.setImageBitmap(qrgEncoder.encodeAsBitmap())

                            } catch (e: WriterException) { }

                        }

                    }

                }

            }

            getWallet(getUserId).dataChanges().subscribe {

                setViewVisible(isProgress = true)

                if (it.exists()) {

                    val listWallet = arrayListOf<Wallet>()

                    it.children.forEach { data ->

                        val wallet = data.getValue(Wallet::class.java)

                        if (wallet != null) {

                            if (wallet.user_id == getUserId) {

                                listWallet.add(Wallet(wallet.user_id, wallet.amount, wallet.inOut, wallet.isCash, wallet.note, wallet.date))

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