package com.kukus.administrator.setting

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kukus.administrator.R
import com.kukus.administrator.setting.printer.ActivityPrinter
import com.kukus.administrator.setting.time.ActivityTime
import kotlinx.android.synthetic.main.activity_setting.*

class ActivitySetting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        btn_printer.setOnClickListener {
            startActivity(Intent(this, ActivityPrinter::class.java))
        }
        btn_time.setOnClickListener {
            startActivity(Intent(this, ActivityTime::class.java))
        }
    }
}
