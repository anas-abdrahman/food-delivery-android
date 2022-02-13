package com.kukus.administrator.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kukus.library.FirebaseUtils.Companion.getStaff
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.FirebaseUtils.Companion.getUserId
import android.os.StrictMode
import android.os.StrictMode.VmPolicy

open class ActivityDashboard : AppCompatActivity() {

    lateinit var mSharedPreferences: SharedPreferences

    var DEVELOPER_MODE = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSharedPreferences = getSharedPreferences("user-preference", Context.MODE_PRIVATE)

        if(getUserId != "") {
            getStaff(getUserId).child("login_at").setValue(getTimestamp)
        }

        if (DEVELOPER_MODE) {

            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build())

            StrictMode.setVmPolicy(VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build())

        }

        //val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        //StrictMode.setThreadPolicy(policy)

    }

}
