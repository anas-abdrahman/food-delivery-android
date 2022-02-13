package com.kukus.customer.servis

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.kukus.customer.R
import com.kukus.customer.order.ActivityListOrder
import com.kukus.customer.tracking.ActivityTracking
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Message
import com.kukus.library.model.Ship


class ServiceOrderNotification : Service(), ChildEventListener {

    override fun onCreate() {

        super.onCreate()

        getShip(getDate).addChildEventListener(this)

    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onChildRemoved(p0: DataSnapshot) {}

    override fun onCancelled(p0: DatabaseError) {}

    override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

    override fun onChildAdded(item: DataSnapshot, p1: String?) {}

    override fun onChildChanged(item: DataSnapshot, p1: String?) {

        val ship = item.getValue(Ship::class.java)

        if (ship != null && ship.user_id == getUserId) {

            val intentTo = Intent(baseContext, ActivityListOrder::class.java);

            when {

                ship.status == STATUS.WAITING -> showNotification("Kukus Cairo", ship.ship_id, "You Order #${ship.ship_id} Waiting", R.drawable.ic_notification, intentTo)
                ship.status == STATUS.PENDING && ship.deliveryId == "" -> showNotification("Kukus Cairo", ship.ship_id, "You Order #${ship.ship_id} Placed", R.drawable.ic_notification, intentTo)
                ship.status == STATUS.PENDING && ship.deliveryId != "" -> showNotification("Kukus Cairo", ship.ship_id, "You Order #${ship.ship_id} PREPARING", R.drawable.ic_notification, intentTo)

                ship.status == STATUS.DISPATCHED -> {

                    val messageChild = item.child("message")

                    val intent = Intent(baseContext, ActivityTracking::class.java)
                    intent.putExtra("lat", ship.address.lat)
                    intent.putExtra("lng", ship.address.lng)
                    intent.putExtra("deliveryId",ship.deliveryId)
                    intent.putExtra("id", ship.id)
                    intent.putExtra("date", ship.date)

                    if (messageChild.exists()) {

                        var message = Message()

                        messageChild.children.forEach {

                            message = it.getValue(Message::class.java)!!

                        }

                        if (message.user_id != getUserId) {

                            intent.putExtra("isMessage", true)

                            showNotification("Message From Delivery", ship.ship_id, message.text, R.drawable.ic_notification, intent)
                        }

                    } else {

                        showNotification("Kukus Cairo", ship.ship_id, "You Order #${ship.ship_id} is Upcoming, you can tracking your order.", R.drawable.ic_notification, intent)

                    }

                }
                ship.status == STATUS.COMPLETE -> showNotification("Kukus Cairo", ship.ship_id, "#${ship.ship_id} Delivered, Thank you for order..", R.drawable.ic_notification, intentTo)
            }

        }
    }

    /*private  fun <T : Any> showNotification(title : String, orderId : String, text : String, icon : Int, intentTo  : Class<T>) {

        val intent = Intent(baseContext, intentTo)
        val notification = NotificationCompat.Builder(baseContext, "KUKUS_CAIRO")
        val pendingIntent = PendingIntent.getActivity(baseContext, 0, intent, 0)
        val notificationManager = baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notification.setDefaults(Notification.DEFAULT_ALL)
                .setTicker("Nasi Kusus Cairo")
                .setContentTitle(title)
                //.setContentInfo(info)
                .setContentText(text)
                .setSmallIcon(icon)
                .setBadgeIconType(icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        //val random = Random().nextInt(9999 - 1) + 1

        notificationManager.notify(orderId.toInt(), notification.build())

    }*/


    private fun showNotification(title: String, orderId: String, message: String, icon: Int, intent: Intent) {

        val context = baseContext
        val channel_id = "KUKUS_CAIRO"
        val NOTIFY_ID = orderId.toInt() // ID of notification
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
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
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
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
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
