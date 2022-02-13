package com.kukus.library

import android.annotation.SuppressLint
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.kukus.customer.halper.Interface.ConnectionListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CheckConnection(private val connection: ConnectionListener){


    var isConnect = false

    @SuppressLint("CheckResult")

    fun builder() : CheckConnection {

            ReactiveNetwork
                    .observeInternetConnectivity()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { isConnectedToInternet ->

                        isConnect = if (isConnectedToInternet) {

                            connection.onlineListener()
                            true

                        } else {

                            connection.offlineListener()
                            false

                        }

                    }

        return this
    }

}