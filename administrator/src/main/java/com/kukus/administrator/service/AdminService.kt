package com.kukus.administrator.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.text.format.DateFormat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.kukus.administrator.R
import com.kukus.administrator.order.ActivityOrders
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.model.Ship
import android.app.NotificationChannel
import android.os.Build
import com.androidhuman.rxfirebase2.database.data
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils.Companion.getStaff
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Staff
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class AdminService : Service(){

    val ACTION = "service.AdminService"
    var staff = Staff()

    @SuppressLint("CheckResult")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if(intent?.getStringExtra("service_admin").equals(ACTION)) {

            getStaff(getUserId).data().subscribe { data ->

                val staff = data.getValue(Staff::class.java)

                if (staff != null) {
                    this.staff = staff
                    getShip(getDate).addChildEventListener(object : ChildEventListener {

                        override fun onChildRemoved(p0: DataSnapshot) {}
                        override fun onCancelled(p0: DatabaseError) {}
                        override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                        override fun onChildChanged(item: DataSnapshot, p1: String?) {}
                        override fun onChildAdded(item: DataSnapshot, p1: String?) {

                            val ship = item.getValue(Ship::class.java)

                            if(ship != null) {

                                when {
                                    ship.status == STATUS.WAITING && staff.role ==  Constant.Companion.USER.ADMIN -> showNotification("Kukus Cairo", ship.ship_id, "Order #${ship.ship_id} Waiting to conform", R.drawable.ic_notification, ActivityOrders::class.java)
                                }

                            }
                        }

                    })
                }

            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? = null

    private  fun <T : Any> showNotification(title : String, orderId : String, message : String, icon : Int, intentTo  : Class<T>) {

        val context = baseContext
        val channel_id = "KUKUS_CAIRO"
        val NOTIFY_ID = orderId.toInt() // ID of notification
        val intent: Intent
        val pendingIntent: PendingIntent
        val builder: NotificationCompat.Builder

        if (notifManager == null) {
            notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_HIGH
            var mChannel: NotificationChannel? = notifManager!!.getNotificationChannel(channel_id)

            if (mChannel == null) {
                mChannel = NotificationChannel(channel_id, title, importance)
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                notifManager!!.createNotificationChannel(mChannel)
            }

            builder = NotificationCompat.Builder(context, channel_id)
            intent = Intent(context, intentTo)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            builder.setContentTitle(title)                            // required
                    .setSmallIcon(icon)   // required
                    .setContentText(message) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(message)
                    .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        } else {

            builder = NotificationCompat.Builder(context, channel_id)
            intent = Intent(context, intentTo)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            builder.setContentTitle(title)                            // required
                    .setSmallIcon(icon)   // required
                    .setContentText(message) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(message)
                    .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)).priority = Notification.PRIORITY_HIGH
        }

        val notification = builder.build()
        notifManager!!.notify(NOTIFY_ID, notification)

    }

    private var notifManager: NotificationManager? = null

}
