package com.kukus.library

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.preference.PreferenceManager
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.kukus.customer.halper.remote.RetrofitClient
import com.kukus.library.model.Order
import com.kukus.library.model.User
import com.kukus.library.remote.IGeoCoordinates


class Constant{

    companion object {

        // Google Api Key
        //const val GOOGLE_API_KEY = "AIzaSyAn8CR5zdmzuoLN9vaS_-xy4iwIAfxFhqY"
        const val GOOGLE_API_KEY = "AIzaSyDjsQk8fdU9fO5W_jrwVHYUOl4nxAFE2m4"

        // Firebase Database For Customer
        const val FIREBASE_TABLE_DELIVERY = "delivery"
        const val FIREBASE_TABLE_USERS = "users"
        const val FIREBASE_TABLE_SHIP = "shipping"
        const val FIREBASE_TABLE_WAITER = "waiter"
        const val FIREBASE_TABLE_TABLE = "table"
        const val FIREBASE_TABLE_ADDRESS = "address"
        const val FIREBASE_TABLE_STAFF = "staff"
        const val FIREBASE_TABLE_TIME = "staff"
        const val FIREBASE_TABLE_NEWS = "news"
        const val FIREBASE_TABLE_MENUS = "menus"
        const val FIREBASE_TABLE_LIMIT = "limit"
        const val FIREBASE_TABLE_CONTACT = "contact"
        const val FIREBASE_TABLE_WALLET = "wallet"
        const val FIREBASE_TABLE_POINT = "point"
        const val FIREBASE_TABLE_CHAT = "chat"
        const val FIREBASE_TABLE_PROMO = "promo"
        const val FIREBASE_TABLE_VOUCHER = "voucher"
        const val FIREBASE_TABLE_SETTING = "setting"

        // Firebase Database For Delivery
        // Intent Extra
        const val EXTRA_STATUS = "STATUS"

        const val EXTRA_USER_ID = "USER_ID"
        const val EXTRA_USER_NAME = "USER_NAME"
        const val EXTRA_USER_EMAIL = "USER_EMAIL"
        const val EXTRA_USER_MOBILE = "USER_BLOCK"
        const val EXTRA_USER_BLOCK = "USER_MOBILE"
        const val EXTRA_USER_REFID = "USER_REFID"
        const val EXTRA_USER_WALLET = "USER_WALLET"
        const val EXTRA_USER_WALLETID = "USER_WALLETID"
        const val EXTRA_USER_LEVEL = "USER_LEVEL"
        const val EXTRA_USER_PROMOID = "USER_PROMOID"
        const val EXTRA_USER_REF = "USER_REF"
        const val EXTRA_USER_POINT = "USER_POINT"
        const val EXTRA_USER_CREATE = "USER_CREATE"
        const val EXTRA_USER_UPDATE = "USER_UPDATE"
        const val EXTRA_USER_LOGIN = "USER_LOGIN"
        const val EXTRA_USER_ONLINE = "USER_ONLINE"
        const val EXTRA_USER_PASSWORD = "USER_PASSWORD"
        const val EXTRA_PRINT_IP = "PRINT_IP"
        const val WIFINAME = "wifiname"


        const val UPDATE = "UPDATE"
        const val DELETE = "DELETE"

        enum class STATUS {

            // UNTUK ORDER DELIVERY
            ORDER,
            WAITING,
            PENDING,
            PREPARING,
            DISPATCHED,
            COMPLETE,
            CANCEL,

        }

        enum class USER {
            ADMIN,
            DELIVERY,
            WAITER,
            CASHIER
        }

        enum class TYPE_ORDER {
            FOOD,
            DRINK,
            EXTRA,
            RESET,
            UPDATE,
            EMPTY
        }

        enum class TRIP {
            NEW,
            ACTIVE,
            UPCOMING,
            PAST
        }

        enum class TABLE {
            EMPTY,
            WAITING,
            COMBINE,
            PLACED
        }


        // Result for Activity
        const val RESULT_MAP = 1000
        const val RESULT_CHECKOUT = 1001
        const val RESULT_MENU_DETAILS = 1002
        const val LOCATION_PERMISSION_REQUEST_CODE = 1003
        const val RESULT_SIGN_IN = 1004
        const val RESULT_WAITER = 1005

        // Init Location
        const val LOCATION_INTERVAL: Long = 10000
        const val FASTEST_LOCATION_INTERVAL: Long = 5000

        fun getGeoCodeService(): IGeoCoordinates {
            return RetrofitClient.getClient("https://maps.googleapis.com").create(IGeoCoordinates::class.java)
        }

        fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor {

            val background = ContextCompat.getDrawable(context, vectorDrawableResourceId)
            background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)

            val bitmap = Bitmap.createBitmap(background.intrinsicWidth, background.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            background.draw(canvas)

            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }

        fun setIP(sharedPreferences: SharedPreferences, ip : String){

            sharedPreferences.edit().putString(EXTRA_PRINT_IP, ip).apply()

        }

        fun getIP(sharedPreferences: SharedPreferences) : String{

            val ip =  sharedPreferences.getString(EXTRA_PRINT_IP, "192.168.0.250")

            return ip ?: "192.168.0.250"

        }


        fun getMapStatic(latLag: LatLng, width: Int = 400, height : Int = 100): String {

            return "https://maps.googleapis.com/maps/api/staticmap?center=${latLag.latitude},${latLag.longitude}&markers=color:red%7Clabel:X%7C${latLag.latitude},${latLag.longitude}&zoom=17&size=${width}x${height}&key=$GOOGLE_API_KEY"

        }

        fun setData(sharedPreferences: SharedPreferences, key: String, data: String) {

            sharedPreferences.edit().putString(key.toUpperCase(), data).apply()

        }

        fun getData(context: Context, key: String): String {

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getString(key.toUpperCase(), "")

        }

        fun setDataUser(context: Context, user: User) {

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.edit().putString(EXTRA_USER_ID, user.id).apply()
            sharedPreferences.edit().putString(EXTRA_USER_NAME, user.name).apply()
            sharedPreferences.edit().putString(EXTRA_USER_MOBILE, user.mobile).apply()
            sharedPreferences.edit().putString(EXTRA_USER_EMAIL, user.email).apply()
            sharedPreferences.edit().putString(EXTRA_USER_PASSWORD, user.password).apply()
            sharedPreferences.edit().putInt(EXTRA_USER_LEVEL, user.level).apply()
            sharedPreferences.edit().putString(EXTRA_USER_WALLETID, user.walletId).apply()
            sharedPreferences.edit().putFloat(EXTRA_USER_WALLET, user.wallet).apply()
            sharedPreferences.edit().putString(EXTRA_USER_REFID, user.refId).apply()
            sharedPreferences.edit().putString(EXTRA_USER_REF, user.referral).apply()
            sharedPreferences.edit().putString(EXTRA_USER_PROMOID, user.promoId).apply()
            sharedPreferences.edit().putLong(EXTRA_USER_CREATE, user.created_at).apply()
            sharedPreferences.edit().putLong(EXTRA_USER_UPDATE, user.updated_at).apply()
            sharedPreferences.edit().putLong(EXTRA_USER_LOGIN, user.login_at).apply()
            sharedPreferences.edit().putBoolean(EXTRA_USER_ONLINE, user.online).apply()
            sharedPreferences.edit().putBoolean(EXTRA_USER_BLOCK, user.block).apply()
            sharedPreferences.edit().putInt(EXTRA_USER_POINT, user.point).apply()

        }

        fun getDataUser(context: Context): User {

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val user = User()

            user.id = sharedPreferences.getString(EXTRA_USER_ID, "")
            user.name = sharedPreferences.getString(EXTRA_USER_NAME, "")
            user.mobile = sharedPreferences.getString(EXTRA_USER_MOBILE, "")
            user.email = sharedPreferences.getString(EXTRA_USER_EMAIL, "")
            user.password = sharedPreferences.getString(EXTRA_USER_PASSWORD, "")
            user.level = sharedPreferences.getInt(EXTRA_USER_LEVEL, 0)
            user.walletId = sharedPreferences.getString(EXTRA_USER_WALLETID, "")
            user.wallet = sharedPreferences.getFloat(EXTRA_USER_WALLET, 0f)
            user.refId = sharedPreferences.getString(EXTRA_USER_REFID, "")
            user.referral = sharedPreferences.getString(EXTRA_USER_REF, "")
            user.promoId = sharedPreferences.getString(EXTRA_USER_PROMOID, "")
            user.created_at = sharedPreferences.getLong(EXTRA_USER_CREATE, 0)
            user.updated_at = sharedPreferences.getLong(EXTRA_USER_UPDATE, 0)
            user.login_at = sharedPreferences.getLong(EXTRA_USER_LOGIN, 0)
            user.online = sharedPreferences.getBoolean(EXTRA_USER_ONLINE, false)
            user.block = sharedPreferences.getBoolean(EXTRA_USER_BLOCK, false)
            user.point = sharedPreferences.getInt(EXTRA_USER_POINT, 0)

            return user

        }


        fun isValidContextForGlide(context: Context?): Boolean {
            if (context == null) {
                return false
            }
            if (context is Activity) {
                val activity = context as Activity?
                if (activity!!.isDestroyed || activity.isFinishing) {
                    return false
                }
            }
            return true
        }

        fun getOrderList(orderListMenu: ArrayList<Order>, isDetails: Boolean = false): ArrayList<Order> {

            val orderList = arrayListOf<Order>()

            orderListMenu.forEach { order ->

                if (isDetails) {

                    if (!orderList.any { it.menu_id == order.menu_id + order.note } || isDetails) {

                        val myOrder = Order()
                        myOrder.id = order.id
                        myOrder.menu_id = order.menu_id + order.note
                        myOrder.title = order.title
                        myOrder.price1 = order.price1
                        myOrder.price2 = order.price2
                        myOrder.quantity = 1
                        myOrder.note = order.note
                        myOrder.type = order.type

                        orderList.add(myOrder)

                    }else {

                        orderList.forEach { checkOrder ->

                            if (checkOrder.menu_id == order.menu_id + order.note) {

                                checkOrder.quantity += 1

                            }
                        }
                    }

                } else {

                    if (!orderList.any { it.menu_id == order.menu_id }) {

                        val myOrder = Order()
                        myOrder.id = order.id
                        myOrder.menu_id = order.menu_id
                        myOrder.title = order.title
                        myOrder.price1 = order.price1
                        myOrder.price2 = order.price2
                        myOrder.quantity = 1
                        myOrder.note = order.note
                        myOrder.type = order.type

                        orderList.add(myOrder)

                    } else {

                        orderList.forEach { checkOrder ->

                            if (checkOrder.menu_id == order.menu_id) {

                                checkOrder.quantity += 1

                            }
                        }
                    }

                }
            }

            return orderList
        }

        /*fun getOrderList(orderListMenu: ArrayList<Order>, isDetails: Boolean = false): ArrayList<Order> {

            val orderList = arrayListOf<Order>()

            orderListMenu.forEach { order ->

                if (order.note == "" || !isDetails) {

                    if (!orderList.any { it.menu_id == order.menu_id }) {

                        val myOrder = Order()
                        myOrder.id = order.id
                        myOrder.menu_id = order.menu_id
                        myOrder.title = order.title
                        myOrder.price1 = order.price1
                        myOrder.price2 = order.price2
                        myOrder.quantity = 1
                        myOrder.note = order.note
                        myOrder.type = order.type

                        orderList.add(myOrder)

                    } else {

                        orderList.forEach { checkOrder ->

                            if (checkOrder.menu_id == order.menu_id) {

                                checkOrder.quantity += 1

                            }
                        }
                    }

                } else {

                    if (!orderList.any { it.menu_id == order.menu_id + order.note }) {

                        val myOrder = Order()
                        myOrder.id = order.id
                        myOrder.menu_id = order.menu_id + order.note
                        myOrder.title = order.title
                        myOrder.price1 = order.price1
                        myOrder.price2 = order.price2
                        myOrder.quantity = 1
                        myOrder.note = order.note
                        myOrder.type = order.type

                        orderList.add(myOrder)

                    } else {

                        orderList.forEach { checkOrder ->

                            if (checkOrder.menu_id == order.menu_id + order.note) {

                                checkOrder.quantity += 1

                            }
                        }
                    }
                }
            }

            return orderList
        }*/
    }
}
