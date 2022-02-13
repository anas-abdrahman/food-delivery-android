package com.kukus.administrator.setting.printer

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.kukus.administrator.R
import com.kukus.library.Constant.Companion.WIFINAME


class IpEditActivity : Activity() {
    // private MyDialog dialog;
    private var layout: LinearLayout? = null
    private val mIDs = intArrayOf(R.id.ip_edit_1, R.id.ip_edit_2, R.id.ip_edit_3, R.id.ip_edit_4)
    private var tv_wifi_name: TextView? = null
    private val ipEdits = arrayOf<EditText>()
    private var wifiName: String? = null

    private val mReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {

                val info = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                if (info.state == NetworkInfo.State.CONNECTED) {

                    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val wifiInfo = wifiManager.connectionInfo

                    wifiName = "current wifi:" + wifiInfo.ssid
                    tv_wifi_name!!.text = wifiName

                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ip_edit)

        val filter = IntentFilter()
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        registerReceiver(mReceiver, filter)

        val mTextWatcher = arrayOfNulls<MyTextWatcher>(4)
        for (i in 0..3) {
            ipEdits[i] = findViewById<View>(mIDs[i]) as EditText
            mTextWatcher[i] = MyTextWatcher(ipEdits[i])
            ipEdits[i].addTextChangedListener(mTextWatcher[i])
        }

        val split = ipAddress.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        Log.i("ip", "split:" + split.size)
        for (i in split.indices) {
            ipEdits[i].setText(split[i])
        }
        // dialog=new MyDialog(this);
        layout = findViewById<View>(R.id.exit_layout) as LinearLayout
        tv_wifi_name = findViewById<View>(R.id.tv_wifi_name) as TextView
        val intent = intent
        wifiName = "current wifi:" + intent.getStringExtra(WIFINAME)
        tv_wifi_name!!.text = wifiName

        layout!!.setOnClickListener {
            // TODO Auto-generated method stub
            // Toast.makeText(getApplicationContext(),
            // "click other outside window close",
            // Toast.LENGTH_SHORT).show();
        }
    }

    internal inner class MyTextWatcher(var mEditText: EditText) : TextWatcher {

        override fun afterTextChanged(s: Editable) {
            // TODO Auto-generated method stub
            if (s.length == 3) {
                if (Integer.parseInt(mEditText.editableText.toString()) > 255) {
                    mEditText.setText("255")
                }
                if (this.mEditText === ipEdits[0]) {
                    ipEdits[1].requestFocus()
                } else if (this.mEditText === ipEdits[1]) {
                    ipEdits[2].requestFocus()
                } else if (this.mEditText === ipEdits[2]) {
                    ipEdits[3].requestFocus()
                }

                if (this.mEditText === ipEdits[3]) {
                    ipEdits[3].setSelection(3)
                }

            } else if (s.length == 0) {
                if (this.mEditText === ipEdits[3]) {
                    ipEdits[2].requestFocus()
                    ipEdits[2].setSelection(ipEdits[2].length())
                } else if (this.mEditText === ipEdits[2]) {
                    ipEdits[1].requestFocus()
                    ipEdits[1].setSelection(ipEdits[1].length())
                } else if (this.mEditText === ipEdits[1]) {
                    ipEdits[0].requestFocus()
                    ipEdits[0].setSelection(ipEdits[0].length())
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                       after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                   count: Int) {
            // TODO Auto-generated method stub

        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // click other outside window close
        // finish();
        return true
    }

    fun connect(v: View) {

        val sb = StringBuffer()

        for (i in 0..3) {
            sb.append(ipEdits[i].editableText)
            if (i != 3) {
                sb.append(".")
            }
        }

        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show()

        val intent = Intent()
        intent.putExtra("ip_address", sb.toString())
        ipAddress = sb.toString()

        Log.i("ip", "ip:$ipAddress")

        // Set result and finish this Activity
        setResult(RESULT_OK, intent)
        this.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

    companion object {
        private var ipAddress = "192.168.1.114"
    }
}
