package com.kukus.customer.checkout

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.dataChanges
import com.androidhuman.rxfirebase2.database.rxSetValue
import com.kukus.customer.R
import com.kukus.customer.address.AddressAdapter
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.Constant.Companion.RESULT_MAP
import com.kukus.library.FirebaseUtils.Companion.getAddress
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getMenu
import com.kukus.library.FirebaseUtils.Companion.getMenuRef
import com.kukus.library.FirebaseUtils.Companion.getPrice
import com.kukus.library.FirebaseUtils.Companion.getPricePromo
import com.kukus.library.FirebaseUtils.Companion.getPromotion
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getShipRef
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.FirebaseUtils.Companion.getWallet
import com.kukus.library.model.*
import kotlinx.android.synthetic.main.activity_checkout.*

class ActivityCheckout : AppCompatActivity() {

    var mListOrder = arrayListOf<Order>()
    var addressList = arrayListOf<Address>()
    lateinit var adapter: AddressAdapter

    var postion = 0
    var promoCode = ""
    var promoAmount = 0
    var keepWallet = 0f
    var delivery = 0f
    var wallet = 0f

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        mListOrder  = intent.extras.getParcelableArrayList<Order>("list")
        promoCode   = intent.extras.getString("userPromoCode", "")
        promoAmount = intent.extras.getInt("userPromoAmount", 0)
        wallet      = intent.extras.getFloat("userWallet", 0f)

        if (wallet > 0) {
            layout_wallet.visibility = View.VISIBLE
        } else {
            layout_wallet.visibility = View.GONE
        }

        getAddressAdapter()
        calculatePrice()

        list_order.adapter = ListOrderAdapter(this, mListOrder)

        switch_wallet.setOnCheckedChangeListener { compoundButton, b ->

            if (compoundButton.isChecked) {
                calculatePrice(true)
                amount_wallet.setTextColor(Color.parseColor("#32c360"))
            } else {
                calculatePrice(false)
                amount_wallet.setTextColor(Color.GRAY)
            }

        }

        btn_confirm.isEnabled = false
        btn_confirm.setBackgroundResource(R.color.colorGrey)
        btn_confirm.setOnClickListener {
            addOrder()
        }
    }

    @SuppressLint("CheckResult")
    private fun getAddressAdapter(){

        addressList.add(Address())
        adapter = AddressAdapter(addressList, this, 0)

        viewPager.adapter = adapter
        viewPager.setPadding(70, 0, 70, 0)

        getAddress(getUserId).dataChanges().subscribe({

            if (it.exists()) {

                addressList.clear()
                addressList.add(Address())

                it.children.forEach { data ->

                    val address = data.getValue(Address::class.java)

                    if (address != null) {

                        addressList.add(address)

                    }
                }

                if (addressList.size > 0) {

                    adapter = AddressAdapter(addressList, this, 0)

                    viewPager.adapter = adapter
                    viewPager.currentItem = addressList.size - 1

                }
            }
        }) {}
    }

    @SuppressLint("CheckResult") private fun addOrder(){

        var balance = 0f

        btn_confirm.startAnimation()

        var shipID = "1".padStart(5, '0') // value : 00001

        getShip(getDate).data().subscribe{ value ->

            if (value.exists()) {

                value.children.forEach { data ->

                    val ship = data.getValue(Ship::class.java)

                    if (ship != null) {
                        shipID = (Integer.parseInt(ship.ship_id) + 1).toString().padStart(5, '0')
                    }

                }
            }

            if (switch_wallet.isChecked && wallet > 0f) {

                if (wallet > keepWallet) {

                    balance = (wallet - keepWallet)

                    getWallet(getUserId).push().setValue(Wallet(getUserId, balance, "out", true, "pay for order #$shipID", getTimestamp))
                    getUser(getUserId).child("wallet").setValue(keepWallet)

                }
            }

            getMenuRef.data().subscribe { menuRef ->

                menuRef.children.forEach { menuData ->

                    val menu = menuData.getValue(Menu::class.java)

                    if (menu != null) {

                        mListOrder.forEach { order ->

                            if (order.menu_id == menu.id) {
                                getMenu(menu.id).child("currentLimit").setValue(menu.currentLimit + order.quantity)
                            }
                        }
                    }
                }

                val locationAddress = Address()
                locationAddress.user_id     = getUserId
                locationAddress.street      = street
                locationAddress.building    = builder
                locationAddress.floor       = floor
                locationAddress.apartment   = apertment
                locationAddress.lat         = lat
                locationAddress.lng         = lng
                locationAddress.delivery    = delivery

                val ship = Ship()
                ship.id             = getShipRef.push().key ?: getTimestamp.toString()
                ship.user_id        = getUserId
                ship.ship_id        = shipID

                ship.address        = locationAddress
                ship.delivery       = delivery
                ship.deliveryId     = ""

                ship.date           = getDate
                ship.status         = STATUS.WAITING
                ship.menu           = mListOrder

                ship.promo          = promoCode
                ship.promoAmount    = promoAmount

                ship.wallet         = balance
                ship.price          = priceFinal

                getShip(getDate).child(ship.id).rxSetValue(ship).subscribe{

                    addressList.removeAt(0)

                    val addressRef = getAddress(getUserId)
                    addressRef.removeValue()
                    addressRef.setValue(addressList)

                    Toast.makeText(baseContext, "Your Order have Successfully", Toast.LENGTH_LONG).show()

                    Handler().postDelayed({

                        intent.putExtra("ship_id", ship.id)
                        intent.putExtra("date", ship.date)

                        setResult(Activity.RESULT_OK, intent)

                        finish()

                    }, 1000)

                }
            }
        }
    }

    var priceFinal = 0f

    private fun calculatePrice(isWallet: Boolean = false) {

        var priceAll = 0f

        val totalPrice: Float
        val useWallet = if (isWallet) wallet else 0f

        mListOrder.forEach {
            priceAll += (it.price1 + it.price2) * it.quantity
        }

        if (delivery != 0f) {

            layout_delivery.visibility = View.VISIBLE
            divider.visibility = View.VISIBLE

            txt_delivery_price.text = ("LE $delivery")

            btn_confirm.setBackgroundResource(R.color.colorGreen)
            btn_confirm.isEnabled = true

        } else {

            layout_delivery.visibility = View.GONE
            divider.visibility = View.GONE

            txt_delivery_price.text = ("LE $delivery")

            btn_confirm.setBackgroundResource(R.color.colorGrey)
            btn_confirm.isEnabled = false

        }

        if (promoCode != "" && promoAmount != 0) {

            val promotion = getPromotion(priceAll, promoAmount)

            txt_promo.text = ("Promotion Code ($promoCode) - $promoAmount%")
            txt_price_promo.text = ("- LE ${(promotion)}")
            layout_promo_gift.visibility = View.VISIBLE

            totalPrice = getPricePromo(priceAll, delivery, promotion, useWallet)

        } else {

            totalPrice = getPrice(priceAll, delivery, useWallet)

        }

        if (totalPrice < 0) {

            val amount = Math.abs(totalPrice)

            amount_wallet.text = ("LE $amount")
            amount_wallet.paint.flags = Paint.ANTI_ALIAS_FLAG

            txt_total_price_list.text = ("LE 0.0")

            priceFinal = 0f
            keepWallet = amount

        } else {

            if (isWallet) {

                keepWallet = 0f
                amount_wallet.paint.flags = (Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)

            } else {

                keepWallet = wallet
                amount_wallet.paint.flags = Paint.ANTI_ALIAS_FLAG

            }

            priceFinal = totalPrice
            amount_wallet.text = ("LE $wallet")
            txt_total_price_list.text = ("LE $totalPrice")

        }
    }

    var lat     = ""
    var lng     = ""
    var street  = ""
    var builder = ""
    var floor   = ""
    var apertment = ""

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == RESULT_MAP && resultCode == Activity.RESULT_OK) {

            if (data != null)
            {
                lat         = data.getStringExtra("lat")
                lng         = data.getStringExtra("lng")
                street      = data.getStringExtra("street")
                delivery    = data.getFloatExtra("delivery", 0f)

                builder     = data.getStringExtra("builder")
                floor       = data.getStringExtra("floor")
                apertment   = data.getStringExtra("apertment")

                addAddress()
            }
        }
    }


    fun selectAddress(position: Int) {

        postion = position
        adapter = AddressAdapter(addressList, this, position)

        delivery    = addressList[position].delivery
        lat         = addressList[position].lat
        lng         = addressList[position].lng
        street      = addressList[position].street
        builder     = addressList[position].building
        floor       = addressList[position].floor
        apertment   = addressList[position].apartment

        viewPager.adapter       = adapter
        viewPager.currentItem   = position

        calculatePrice(switch_wallet.isChecked)

    }


    private fun addAddress()
    {

        addressList.add(Address(getUserId, street, builder, floor, apertment, lat, lng, delivery))

        postion = addressList.size - 1
        adapter = AddressAdapter(addressList, this, postion)

        delivery    = addressList[postion].delivery
        lat         = addressList[postion].lat
        lng         = addressList[postion].lng
        street      = addressList[postion].street
        builder     = addressList[postion].building
        floor       = addressList[postion].floor
        apertment   = addressList[postion].apartment

        viewPager.adapter       = adapter
        viewPager.currentItem   = postion

        calculatePrice(switch_wallet.isChecked)
    }

    fun removeAddress(position: Int)
    {
        if (postion == position) {

            postion     = 0

            delivery    = 0f
            lat         = ""
            lng         = ""
            street      = ""
            builder     = ""
            floor       = ""
            apertment   = ""

        } else if (postion > position) {

            postion -= 1

        }

        addressList.removeAt(position)

        adapter = AddressAdapter(addressList, this, postion)

        viewPager.adapter       = adapter
        viewPager.currentItem   = postion

        calculatePrice(switch_wallet.isChecked)

    }
}