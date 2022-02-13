package com.kukus.customer.setting

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kukus.customer.R
import com.kukus.library.Constant.Companion.FIREBASE_TABLE_MENUS
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.model.Menu
import kotlinx.android.synthetic.main.activity_setting.*

class ActivitySetting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

    }
}
