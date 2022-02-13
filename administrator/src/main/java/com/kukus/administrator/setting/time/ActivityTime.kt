package com.kukus.administrator.setting.time

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.androidhuman.rxfirebase2.database.data
import com.kukus.administrator.R
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getDateConvert
import com.kukus.library.model.Time
import kotlinx.android.synthetic.main.activity_time.*

class ActivityTime : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)

        txt_date.text = getDateConvert(FirebaseUtils.getDate)

        FirebaseUtils.getSetting("time").data().subscribe { data ->

            if(data.exists()) {

                val dataTime = data.getValue(Time::class.java)

                if(dataTime != null) {

                    switch_open.isChecked = dataTime.open

                    et_note.setText(dataTime.note)

                    et_time_open.setText(dataTime.time_1)
                    et_time_close.setText(dataTime.time_2)

                    time_1.isChecked = dataTime.time_type_1 == "AM"
                    time_2.isChecked = dataTime.time_type_2 == "AM"

                }
            }
        }

        btn_save.setOnClickListener {

            val time  = Time()
            time.open = switch_open.isChecked
            time.time_1 = et_time_open.text.toString()
            time.time_type_1 = if (time_1.isChecked) "AM" else "PM"
            time.time_2 = et_time_close.text.toString()
            time.time_type_2 = if (time_2.isChecked) "AM" else "PM"
            time.note = et_note.text.toString()

            FirebaseUtils.getSetting("time").setValue(time).addOnCompleteListener {

                finish()

            }

        }

    }
}
