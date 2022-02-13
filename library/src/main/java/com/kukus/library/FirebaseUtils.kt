package com.kukus.library

import android.annotation.SuppressLint
import android.text.format.DateFormat
import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.dataChanges
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kukus.library.model.Point
import com.kukus.library.model.Staff
import com.kukus.library.model.User
import com.kukus.library.model.Wallet
import io.reactivex.disposables.Disposable
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*

class FirebaseUtils {

    companion object {

        private var REF_TABLE_USERS: DatabaseReference
        private var REF_TABLE_SETTING: DatabaseReference
        private var REF_TABLE_VOUCHER: DatabaseReference
        private var REF_TABLE_LIMIT: DatabaseReference
        private var REF_TABLE_NEWS: DatabaseReference
        private var REF_TABLE_WALLET: DatabaseReference
        private var REF_TABLE_WAITER: DatabaseReference
        private var REF_TABLE_ADDRESS: DatabaseReference
        private var REF_TABLE_STAFF: DatabaseReference
        private var REF_TABLE_CHAT: DatabaseReference
        private var REF_TABLE_POINT: DatabaseReference
        private var REF_TABLE_PROMO: DatabaseReference
        private var REF_TABLE_CONTACT: DatabaseReference
        private var REF_TABLE_MENUS: DatabaseReference
        private var REF_TABLE_TABLE: DatabaseReference
        private var REF_TABLE_SHIP: DatabaseReference

        init {

            FirebaseDatabase.getInstance().setPersistenceEnabled(true)

            REF_TABLE_USERS = databaseRef(Constant.FIREBASE_TABLE_USERS)
            REF_TABLE_SETTING = databaseRef(Constant.FIREBASE_TABLE_SETTING)
            REF_TABLE_VOUCHER = databaseRef(Constant.FIREBASE_TABLE_VOUCHER)
            REF_TABLE_LIMIT = databaseRef(Constant.FIREBASE_TABLE_LIMIT)
            REF_TABLE_NEWS = databaseRef(Constant.FIREBASE_TABLE_NEWS)
            REF_TABLE_WALLET = databaseRef(Constant.FIREBASE_TABLE_WALLET)
            REF_TABLE_WAITER = databaseRef(Constant.FIREBASE_TABLE_WAITER)
            REF_TABLE_ADDRESS = databaseRef(Constant.FIREBASE_TABLE_ADDRESS)
            REF_TABLE_STAFF = databaseRef(Constant.FIREBASE_TABLE_STAFF)
            REF_TABLE_CHAT = databaseRef(Constant.FIREBASE_TABLE_CHAT)
            REF_TABLE_POINT = databaseRef(Constant.FIREBASE_TABLE_POINT)
            REF_TABLE_PROMO = databaseRef(Constant.FIREBASE_TABLE_PROMO)
            REF_TABLE_CONTACT = databaseRef(Constant.FIREBASE_TABLE_CONTACT)
            REF_TABLE_MENUS = databaseRef(Constant.FIREBASE_TABLE_MENUS)
            REF_TABLE_TABLE = databaseRef(Constant.FIREBASE_TABLE_TABLE)
            REF_TABLE_SHIP = databaseRef(Constant.FIREBASE_TABLE_SHIP)


            if(true) {
               //FirebaseDatabase.getInstance().setPersistenceEnabled(true)
               REF_TABLE_USERS.keepSynced(true)
               REF_TABLE_SETTING.keepSynced(true)
               REF_TABLE_VOUCHER.keepSynced(true)
               REF_TABLE_LIMIT.keepSynced(true)
               REF_TABLE_NEWS.keepSynced(true)
               REF_TABLE_WALLET.keepSynced(true)
               REF_TABLE_WAITER.keepSynced(true)
               REF_TABLE_ADDRESS.keepSynced(true)
               REF_TABLE_STAFF.keepSynced(true)
               REF_TABLE_CHAT.keepSynced(true)
               REF_TABLE_POINT.keepSynced(true)
               REF_TABLE_PROMO.keepSynced(true)
               REF_TABLE_CONTACT.keepSynced(true)
               REF_TABLE_MENUS.keepSynced(true)
               REF_TABLE_TABLE.keepSynced(true)
               REF_TABLE_SHIP.keepSynced(true)
            }

        }

        val getPushKey: String get() = FirebaseDatabase.getInstance().reference.push().key ?: getTimestamp.toString()

        private fun databaseRef(path: String): DatabaseReference = FirebaseDatabase.getInstance().getReference(path)


        val getUserRef: DatabaseReference get() = REF_TABLE_USERS
        fun getUser(id: String): DatabaseReference = REF_TABLE_USERS.child(id)

        fun getSetting(child: String): DatabaseReference = REF_TABLE_SETTING.child(child)

        val getPromoRef: DatabaseReference get() = REF_TABLE_PROMO
        fun getPromo(id: String): DatabaseReference = REF_TABLE_PROMO.child(id)

        val getVoucherRef: DatabaseReference get() = (REF_TABLE_VOUCHER)
        fun getVoucher(id: String): DatabaseReference = (REF_TABLE_VOUCHER).child(id)

        val getMenuRef: DatabaseReference get() = REF_TABLE_MENUS
        fun getMenu(id: String): DatabaseReference = REF_TABLE_MENUS.child(id)

        val getLimitRef: DatabaseReference get() = REF_TABLE_LIMIT
        fun getLimit(id: String): DatabaseReference = REF_TABLE_LIMIT.child(id)

        val getNewsRef: DatabaseReference get() = REF_TABLE_NEWS
        fun getNews(id: String): DatabaseReference = REF_TABLE_NEWS.child(id)

        val getShipRef: DatabaseReference get() = REF_TABLE_SHIP
        fun getShip(id: String): DatabaseReference = REF_TABLE_SHIP.child(id)

        val getWaiterRef: DatabaseReference get() = REF_TABLE_WAITER
        fun getWaiter(id: String): DatabaseReference = REF_TABLE_WAITER.child(id)

        val getTableRef: DatabaseReference get() = REF_TABLE_TABLE
        fun getTable(id: String): DatabaseReference = REF_TABLE_TABLE.child(id)

        val getWalletRef: DatabaseReference get() = REF_TABLE_WALLET
        fun getWallet(id: String): DatabaseReference = REF_TABLE_WALLET.child(id)

        fun getPoint(id: String): DatabaseReference = REF_TABLE_POINT.child(id)

        val getChatRef: DatabaseReference get() = REF_TABLE_CHAT
        fun getChat(id: String): DatabaseReference = REF_TABLE_CHAT.child(id)

        val getContactRef: DatabaseReference get() = REF_TABLE_CONTACT
        fun getContact(id: String): DatabaseReference = REF_TABLE_CONTACT.child(id)

        val getStaffRef: DatabaseReference get() = REF_TABLE_STAFF
        fun getStaff(id: String): DatabaseReference = REF_TABLE_STAFF.child(id)

        val getAddressRef: DatabaseReference get() = REF_TABLE_ADDRESS
        fun getAddress(id: String): DatabaseReference = REF_TABLE_ADDRESS.child(id)


        fun getStorage(path: String): StorageReference = FirebaseStorage.getInstance()
                .reference
                .child(path)

        /* User Auth*/
        val getUserCurrent: FirebaseUser? get() = FirebaseAuth.getInstance().currentUser

        val getUserAuth: FirebaseAuth get() = FirebaseAuth.getInstance()

        val getUserId: String  get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        /* Date and Time*/
        val getTimestamp: Long get() = Date().time

        val getDate: String get() = DateFormat.format("yyyyMMdd", Date().time).toString()

        fun getDateConvert(date: String, split: String = "-"): String {
            val tradeDate = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(date)
            return SimpleDateFormat("dd${split}MM${split}yyyy", Locale.ENGLISH).format(tradeDate)
        }

        fun getDateLong(time: String, am : String): Long {

            val tradeDate = SimpleDateFormat("hh:mm a yyyyMMdd", Locale.ENGLISH).parse("$time $am $getDate")
            return tradeDate.time
        }

        /* Promotion */
        fun getPromotion(price: Float, percentPromo: Int): Float {

            // nak harga detail buang toInt()
            val promo = (price * (percentPromo / 100.0)).toInt()

            return promo.toFloat()
        }

        /* Price*/
        fun getPrice(priceAll: Float, delivery: Float, wallet: Float): Float {

            return (priceAll + delivery) - wallet
        }

        fun getPricePromo(priceAll: Float, delivery: Float, promotion: Float, wallet: Float): Float {

            return ((priceAll + delivery) - promotion) - wallet
        }


        /* Random Code */
        fun createRandomLatter(codeLength: Int): String {
            val chars = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray()
            val sb = StringBuilder()
            val random = SecureRandom()
            for (i in 0 until codeLength) {
                val c = chars[random.nextInt(chars.size)]
                sb.append(c)
            }
            return sb.toString()
        }

        fun createRandomCode(codeLength: Int): String {
            val chars = "abcdefghijklmnopqrstuvwxyz1234567890".toUpperCase().toCharArray()
            val sb = StringBuilder()
            val random = SecureRandom()
            for (i in 0 until codeLength) {
                val c = chars[random.nextInt(chars.size)]
                sb.append(c)
            }
            return sb.toString()
        }

        fun createRandomNumber(codeLength: Int): String {
            val chars = "123456789".toUpperCase().toCharArray()
            val sb = StringBuilder()
            val random = SecureRandom()
            for (i in 0 until codeLength) {
                val c = chars[random.nextInt(chars.size)]
                sb.append(c)
            }
            return sb.toString()
        }

        @SuppressLint("CheckResult")
        fun addWalletToUser(userId: String = getUserId, amount: Float, note: String) {

            getUser(userId).data().subscribe { userData ->
                if (userData.exists()) {

                    val user = userData.getValue(User::class.java)

                    if (user != null) {

                        getWallet(user.id).push().setValue(Wallet(user.id, amount, "in", false, note, getTimestamp))
                        getUser(user.id).child("wallet").setValue(user.wallet + amount)

                    }
                }
            }
        }

        @SuppressLint("CheckResult")
        fun addWalletToStaff(userId: String, amount: Float, note: String) {

            getStaff(userId).data().subscribe { walletStaff ->

                if (walletStaff.exists()) {

                    val staff = walletStaff.getValue(Staff::class.java)

                    if (staff != null) {

                        getWallet(staff.id).push().setValue(Wallet(staff.id, amount, "in", false, note, getTimestamp))
                        getStaff(staff.id).child("wallet").setValue(staff.wallet + amount)

                    }
                }
            }
        }

        @SuppressLint("CheckResult")
        fun addPriceToPoint(userId: String, priceAll: Float, note: String) {

            getUser(userId).data().subscribe { userData ->

                if (userData.exists()) {

                    val user = userData.getValue(User::class.java)

                    if (user != null) {

                        val point = (priceAll.toInt() / 10)

                        if(point > 0) {

                            getUser(user.id).child("point").setValue((user.point + point))
                            getPoint(user.id).push().setValue(Point(user.id, point, "reward $point point for $note", getTimestamp, "in"))

                        }
                    }
                }
            }
        }

        @SuppressLint("CheckResult")
        fun addRewardWallet(userId: String, amount: Float, point: Int, note: String) {

            getUser(userId).data().subscribe { userData ->
                if (userData.exists()) {

                    val user = userData.getValue(User::class.java)

                    if (user != null) {

                        getUser(user.id).child("wallet").setValue(user.wallet + amount)
                        getUser(user.id).child("point").setValue(user.point - point)

                        getWallet(user.id).push().setValue(Wallet(user.id, amount, "in", false, note, getTimestamp))
                        getPoint(user.id).push().setValue(Point(user.id, point, note, getTimestamp, "out"))

                    }
                }
            }
        }

    }


}