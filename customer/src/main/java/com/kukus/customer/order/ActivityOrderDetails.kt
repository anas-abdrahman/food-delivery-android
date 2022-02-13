package com.kukus.customer.order

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.content.res.AppCompatResources
import android.view.View
import com.androidhuman.rxfirebase2.database.dataChanges
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.kukus.customer.R
import com.kukus.customer.checkout.ListOrderAdapter
import com.kukus.library.Constant
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.FirebaseUtils.Companion.getPrice
import com.kukus.library.FirebaseUtils.Companion.getPricePromo
import com.kukus.library.FirebaseUtils.Companion.getPromotion
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.github.stepView.models.Step
import com.kukus.library.model.Ship
import kotlinx.android.synthetic.main.activity_order_details.*
import java.text.SimpleDateFormat
import java.util.*

class ActivityOrderDetails : AppCompatActivity() {

    @SuppressLint("CheckResult") override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        val id = intent.getStringExtra("id")
        val date = intent.getStringExtra("date")

        getShip(date).child(id).dataChanges().subscribe { shipRef ->

            if (shipRef.exists()) {

                val ship = shipRef.getValue(Ship::class.java)

                if (ship != null) {

                    txt_order_id.text = ("Order ID : " + ship.ship_id)
                    txt_order_date.text = getDay(ship.timestamp)
                    txt_order_month.text = getMonth(ship.timestamp)
                    txt_delivery_charge.text = StringBuilder("LE ${ship.delivery}")

                    list_order.adapter = ListOrderAdapter(this, ship.menu)

                    if (Constant.isValidContextForGlide(this)) {

                        Glide.with(this).load(Constant.getMapStatic(LatLng(ship.address.lat.toDouble(),ship.address.lng.toDouble()), 400, 200)).into(map)

                    }

                    /** StepView **/
                    setupStepView(ship)

                    /** Wallet **/
                    setWallet(ship.wallet)

                    /** Promo **/
                    setPriceAndPrice(ship)

                    /** View **/
                    setViewVisible(true)

                }
            }
        }
    }

    private fun setPriceAndPrice(ship: Ship) {

        var priceAll = 0f

        ship.menu.forEach { priceAll += (it.price1) * it.quantity }

        if (ship.promoAmount > 0) {

            val promotion = getPromotion(priceAll, ship.promoAmount)
            val totalPrice = getPricePromo(priceAll, ship.delivery, promotion, ship.wallet)

            txt_order_price.text = StringBuilder("LE $totalPrice")
            txt_promo.text = StringBuilder("- LE $promotion")
            label_promo.text = StringBuilder("- Promotion Code (${ship.promo}) - ${ship.promoAmount}%")

            layout_promo_gift.visibility = View.VISIBLE

        } else {

            val totalPrice = getPrice(priceAll, ship.delivery, ship.wallet)
            txt_order_price.text = StringBuilder("LE $totalPrice")
            layout_promo_gift.visibility = View.GONE

        }

    }

    private fun setWallet(wallet: Float) {

        if (wallet > 0) {
            txt_menu_wallet.visibility = View.VISIBLE
            txt_wallet.visibility = View.VISIBLE
            txt_wallet.text = StringBuilder("- LE $wallet")
        } else {
            txt_menu_wallet.visibility = View.GONE
            txt_wallet.visibility = View.GONE
        }

    }

    private fun getDay(timestamp: Long): String {

        val date = Date(timestamp)
        val calendar = Calendar.getInstance(); calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH).toString()

    }

    private fun getMonth(timestamp: Long): String {

        val date = Date(timestamp)
        val calendar = Calendar.getInstance(); calendar.time = date

        return SimpleDateFormat("MMM").format(calendar.time).toUpperCase()
    }

    private fun setupStepView(ship: Ship) {

        val stepList = arrayListOf<Step>()

        stepList.add(Step("RECEIVED", Step.State.NOT_COMPLETED, setIcon(R.drawable.ic_not_completed), setIcon(R.drawable.ic_completed), setIcon(R.drawable.ic_cancle)))
        stepList.add(Step("DISPATCHED", Step.State.NOT_COMPLETED, setIcon(R.drawable.ic_not_completed), setIcon(R.drawable.ic_completed), setIcon(R.drawable.ic_cancle)))
        stepList.add(Step("DELIVERING", Step.State.NOT_COMPLETED, setIcon(R.drawable.ic_not_completed), setIcon(R.drawable.ic_completed), setIcon(R.drawable.ic_cancle)))
        stepList.add(Step("COMPLETE", Step.State.NOT_COMPLETED, setIcon(R.drawable.ic_not_completed), setIcon(R.drawable.ic_completed), setIcon(R.drawable.ic_cancle)))

        step_view.setCircleRadius(18f).setLineLength(30f)
        step_view.steps = stepList

        when (ship.status) {

            STATUS.WAITING -> {

                txt_order_status.text = "WAITING"

            }

            STATUS.PENDING -> {

                if (ship.deliveryId == "") {

                    step_view.setStepState(Step.State.COMPLETED, 0)
                    txt_order_status.setBackgroundResource(R.drawable.shape_radius_color1) //343e5f
                    txt_order_status.setTextColor(Color.parseColor("#ffffff"))
                    txt_order_status.text = "PENDING"

                } else {

                    step_view.setStepState(Step.State.COMPLETED, 0)
                    step_view.setStepState(Step.State.COMPLETED, 1)
                    txt_order_status.setBackgroundResource(R.drawable.shape_radius_color2)
                    txt_order_status.setTextColor(Color.parseColor("#ffffff"))
                    txt_order_status.text = "DISPATCHED"
                }

            }

            STATUS.DISPATCHED -> {

                step_view.setStepState(Step.State.COMPLETED, 0)
                step_view.setStepState(Step.State.COMPLETED, 1)
                step_view.setStepState(Step.State.COMPLETED, 2)
                txt_order_status.setBackgroundResource(R.drawable.shape_radius_color2)
                txt_order_status.setTextColor(Color.parseColor("#ffffff"))
                txt_order_status.text = "DELIVERING"

            }

            STATUS.COMPLETE -> {

                step_view.setStepState(Step.State.COMPLETED, 0)
                step_view.setStepState(Step.State.COMPLETED, 1)
                step_view.setStepState(Step.State.COMPLETED, 2)
                step_view.setStepState(Step.State.COMPLETED, 3)
                txt_order_status.setBackgroundResource(R.drawable.shape_radius_color3)
                txt_order_status.setTextColor(Color.parseColor("#ffffff"))
                txt_order_status.text = "COMPLETE"

            }

            STATUS.CANCEL -> {

                step_view.setStepState(Step.State.CURRENT, 0)
                step_view.setStepState(Step.State.CURRENT, 1)
                step_view.setStepState(Step.State.CURRENT, 2)
                step_view.setStepState(Step.State.CURRENT, 3)
                txt_order_status.setBackgroundResource(R.drawable.shape_radius_color4)
                txt_order_status.setTextColor(Color.parseColor("#ffffff"))
                txt_order_status.text = "CANCEL"
            }

        }

    }

    private fun setViewVisible(isShow : Boolean) {

        Handler().postDelayed({

            if(isShow) {

                progress.visibility = View.GONE
                content.visibility = View.VISIBLE

            }else{

                progress.visibility = View.VISIBLE
                content.visibility = View.GONE

            }

        }, 1000)

    }

    private fun setIcon(icon: Int): Drawable {
        return AppCompatResources.getDrawable(this, icon)!!
    }
}
