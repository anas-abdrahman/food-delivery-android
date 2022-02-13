package com.kukus.customer.order.tab

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.androidhuman.rxfirebase2.database.rxUpdateChildren
import com.github.florent37.expansionpanel.ExpansionLayout
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import com.kukus.customer.R
import com.kukus.customer.checkout.ListOrderAdapter
import com.kukus.customer.order.ActivityOrderDetails
import com.kukus.customer.tracking.ActivityTracking
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.FirebaseUtils.Companion.getPrice
import com.kukus.library.FirebaseUtils.Companion.getPricePromo
import com.kukus.library.FirebaseUtils.Companion.getPromotion
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.github.stepView.HorizontalStepView
import com.kukus.library.github.stepView.models.Step
import com.kukus.library.library.ExpandableHeightListView
import com.kukus.library.model.Ship
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.RecyclerHolder>() {

    private val expansionsCollection = ExpansionLayoutCollection()

    init {
        expansionsCollection.openOnlyOne(true)
    }

    private var list: ArrayList<Ship> = arrayListOf()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        context = parent.context
        return RecyclerHolder.buildFor(parent)
    }

    @SuppressLint("CheckResult", "SimpleDateFormat")
    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {

        holder.menu_id.text = StringBuilder("Order ID : " + list[position].ship_id)
        holder.menu_date.text = getDay(list[position].timestamp)
        holder.menu_month.text =  getMonth(list[position].timestamp)
        holder.txt_delivery_charge.text = StringBuilder("LE ${list[position].delivery}")

        holder.list_order.adapter = ListOrderAdapter(context, list[position].menu)

        /** StepView **/
        setupStepView(holder, list[position])

        /** Wallet **/
        setWallet(holder, list[position].wallet)

        /** Promo **/
        setPriceAndPrice(holder, list[position])

        /** onClickListener **/
        setOnClickListener(holder, position)

        /** expansionLayout **/
        if (position == lastIndex() && ( list[position].status == STATUS.WAITING || list[position].status == STATUS.PENDING || list[position].status == STATUS.DISPATCHED)) {
            holder.expansionLayout.expand(false)
        } else {
            holder.expansionLayout.collapse(false)
        }

        expansionsCollection.add(holder.expansionLayout)

    }

    private fun setPriceAndPrice(holder: RecyclerHolder, ship: Ship) {

        var priceAll    = 0f

        ship.menu.forEach { priceAll += (it.price1) * it.quantity }

        if (ship.promoAmount > 0) {

            val promotion  = getPromotion(priceAll, ship.promoAmount)
            val totalPrice = getPricePromo(priceAll, ship.delivery, promotion, ship.wallet)

            holder.menu_price.text = StringBuilder("LE $totalPrice")
            holder.txt_promo.text = StringBuilder("- LE $promotion")
            holder.label_promo.text = StringBuilder("- Promotion Code (${ship.promo}) - ${ship.promoAmount}%")

            holder.layout_promo.visibility = View.VISIBLE

        } else {

            val totalPrice = getPrice(priceAll, ship.delivery, ship.wallet)
            holder.menu_price.text = StringBuilder("LE $totalPrice")
            holder.layout_promo.visibility = View.GONE

        }

    }

    private fun setWallet(holder : RecyclerHolder, wallet: Float) {

        if (wallet > 0) {
            holder.txt_menu_wallet.visibility = View.VISIBLE
            holder.txt_wallet.visibility = View.VISIBLE
            holder.txt_wallet.text = StringBuilder("- LE $wallet")
        } else {
            holder.txt_menu_wallet.visibility = View.GONE
            holder.txt_wallet.visibility = View.GONE
        }

    }

    private fun getDay(timestamp : Long) : String {

        val date = Date(timestamp)
        val calendar = Calendar.getInstance(); calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH).toString()

    }

    private fun getMonth(timestamp : Long) : String {

        val date = Date(timestamp)
        val calendar = Calendar.getInstance() ; calendar.time = date

        return SimpleDateFormat("MMM").format(calendar.time).toUpperCase()
    }

    private fun setOnClickListener(holder: RecyclerHolder, position: Int) {

        holder.btnOrderCancel.setOnClickListener {

            val update = mapOf("status" to STATUS.CANCEL)

            val message = "cancel order : ${list[position].ship_id}"

            val ships = getShip(list[position].date).child(list[position].id)

            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText(message)
                    .setConfirmText("Yes, cancel it!")
                    .setConfirmClickListener { sDialog ->

                        ships.rxUpdateChildren(update).subscribe {

                            holder.expansionLayout.collapse(true)

                            Handler().postDelayed({

                                sDialog
                                        .setTitleText("Canceled!")
                                        .setContentText(message)
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)

                            }, 1000)
                        }
                    }.show()
        }

        holder.btn_order_track.setOnClickListener {

            val intent = Intent(context, ActivityTracking::class.java)
            intent.putExtra("lat", list[position].address.lat)
            intent.putExtra("lng", list[position].address.lng)
            intent.putExtra("deliveryId", list[position].deliveryId)
            intent.putExtra("id", list[position].id)
            intent.putExtra("date", list[position].date)
            intent.putExtra("isMessage", false)
            context.startActivity(intent)

        }

        holder.btn_order_detail.setOnClickListener {

            val intent = Intent(context, ActivityOrderDetails::class.java)
            intent.putExtra("id", list[position].id)
            intent.putExtra("date", list[position].date)

            context.startActivity(intent)

        }

    }

    private fun setupStepView(holder: RecyclerHolder, ship: Ship) {

        val stepList = arrayListOf<Step>()

        stepList.add(Step("PLACE", Step.State.NOT_COMPLETED, setIcon(R.drawable.ic_not_completed), setIcon(R.drawable.ic_completed), setIcon(R.drawable.ic_cancle)))
        stepList.add(Step("PREPARING", Step.State.NOT_COMPLETED, setIcon(R.drawable.ic_not_completed), setIcon(R.drawable.ic_completed), setIcon(R.drawable.ic_cancle)))
        stepList.add(Step("DELIVERING", Step.State.NOT_COMPLETED, setIcon(R.drawable.ic_not_completed), setIcon(R.drawable.ic_completed), setIcon(R.drawable.ic_cancle)))
        stepList.add(Step("COMPLETE", Step.State.NOT_COMPLETED, setIcon(R.drawable.ic_not_completed), setIcon(R.drawable.ic_completed), setIcon(R.drawable.ic_cancle)))

        holder.horizontal_step_view.setCircleRadius(18f).setLineLength(30f)
        holder.horizontal_step_view.steps = stepList

        when (ship.status) {

            STATUS.WAITING -> {

                holder.btn_order_track.visibility = View.GONE
                holder.menu_status.text = "WAITING"


                holder.btnOrderCancel.visibility = View.VISIBLE
                holder.btn_order_track.visibility = View.GONE

            }

            STATUS.PENDING -> {

                if (ship.deliveryId == "") {

                    holder.btn_order_track.visibility = View.GONE
                    holder.btnOrderCancel.visibility = View.GONE

                    holder.horizontal_step_view.setStepState(Step.State.COMPLETED, 0)
                    holder.menu_status.setBackgroundResource(R.drawable.shape_radius_color1) //343e5f
                    holder.menu_status.setTextColor(Color.parseColor("#ffffff"))
                    holder.menu_status.text = "PENDING"

                } else {

                    holder.btn_order_track.visibility = View.GONE
                    holder.btnOrderCancel.visibility = View.GONE

                    holder.horizontal_step_view.setStepState(Step.State.COMPLETED, 0)
                    holder.horizontal_step_view.setStepState(Step.State.COMPLETED, 1)
                    holder.menu_status.setBackgroundResource(R.drawable.shape_radius_color2)
                    holder.menu_status.setTextColor(Color.parseColor("#ffffff"))
                    holder.menu_status.text = "DISPATCHED"
                }

                holder.btn_order_track.visibility = View.GONE
                holder.btnOrderCancel.visibility = View.GONE

            }

            STATUS.DISPATCHED -> {

                holder.btn_order_track.visibility = View.VISIBLE
                holder.btnOrderCancel.visibility = View.GONE

                holder.horizontal_step_view.setStepState(Step.State.COMPLETED, 0)
                holder.horizontal_step_view.setStepState(Step.State.COMPLETED, 1)
                holder.horizontal_step_view.setStepState(Step.State.COMPLETED, 2)
                holder.menu_status.setBackgroundResource(R.drawable.shape_radius_color2)
                holder.menu_status.setTextColor(Color.parseColor("#ffffff"))
                holder.menu_status.text = "DELIVERING"

                holder.btn_order_track.visibility = View.VISIBLE
                holder.btnOrderCancel.visibility = View.GONE

            }

            STATUS.COMPLETE -> {

                holder.btn_order_track.visibility = View.GONE
                holder.btnOrderCancel.visibility = View.GONE

                holder.horizontal_step_view.setStepState(Step.State.COMPLETED, 0)
                holder.horizontal_step_view.setStepState(Step.State.COMPLETED, 1)
                holder.horizontal_step_view.setStepState(Step.State.COMPLETED, 2)
                holder.horizontal_step_view.setStepState(Step.State.COMPLETED, 3)
                holder.menu_status.setBackgroundResource(R.drawable.shape_radius_color3)
                holder.menu_status.setTextColor(Color.parseColor("#ffffff"))
                holder.menu_status.text = "COMPLETE"

                holder.btn_order_track.visibility = View.GONE
                holder.btnOrderCancel.visibility = View.GONE
            }

            STATUS.CANCEL -> {

                holder.btn_order_track.visibility = View.GONE
                holder.btnOrderCancel.visibility = View.GONE

                holder.horizontal_step_view.setStepState(Step.State.CURRENT, 0)
                holder.horizontal_step_view.setStepState(Step.State.CURRENT, 1)
                holder.horizontal_step_view.setStepState(Step.State.CURRENT, 2)
                holder.horizontal_step_view.setStepState(Step.State.CURRENT, 3)
                holder.menu_status.setBackgroundResource(R.drawable.shape_radius_color4)
                holder.menu_status.setTextColor(Color.parseColor("#ffffff"))
                holder.menu_status.text = "CANCEL"
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun swapAdapter(list: ArrayList<Ship>) {
        this.list = list
    }

    private fun setIcon(icon: Int): Drawable {
        return AppCompatResources.getDrawable(context, icon)!!
    }


    private fun lastIndex() = list.lastIndex


    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var expansionLayout: ExpansionLayout = itemView.findViewById(R.id.expansionLayout)
        var horizontal_step_view: HorizontalStepView = itemView.findViewById(R.id.step_view)
        var menu_id: TextView = itemView.findViewById(R.id.txt_order_id)
        var menu_price: TextView = itemView.findViewById(R.id.txt_order_price)

        var list_order: ExpandableHeightListView = itemView.findViewById(R.id.list_order)

        var menu_month: TextView = itemView.findViewById(R.id.txt_order_month)
        var menu_date: TextView = itemView.findViewById(R.id.txt_order_date)
        var menu_status: TextView = itemView.findViewById(R.id.txt_order_status)
        var btn_order_track: TextView = itemView.findViewById(R.id.btn_order_track)
        var btn_order_detail: TextView = itemView.findViewById(R.id.btn_order_detail)
        var btnOrderCancel: TextView = itemView.findViewById(R.id.btOrder_cancel)
        var txt_promo: TextView = itemView.findViewById(R.id.txt_promo)
        var label_promo: TextView = itemView.findViewById(R.id.label_promo)
        var layout_promo: LinearLayout = itemView.findViewById(R.id.layout_promo_gift)
        var txt_delivery_charge: TextView = itemView.findViewById(R.id.txt_delivery_charge)
        var txt_menu_wallet: TextView = itemView.findViewById(R.id.txt_menu_wallet)
        var txt_wallet: TextView = itemView.findViewById(R.id.txt_wallet)

        companion object {

            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_order, viewGroup, false))
            }
        }
    }
}
