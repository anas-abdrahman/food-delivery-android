package com.kukus.administrator.order.tab

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.androidhuman.rxfirebase2.database.dataChanges
import com.androidhuman.rxfirebase2.database.rxUpdateChildren
import com.github.florent37.expansionpanel.ExpansionLayout
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import com.kukus.administrator.R
import com.kukus.library.model.Ship
import com.kukus.library.Constant.Companion.STATUS
import java.lang.StringBuilder
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.kukus.administrator.delivery.ActivityOrderMap
import com.kukus.administrator.printer.Printer
import com.kukus.administrator.waiter.tab.OrderAdapterCount
import com.kukus.library.Constant
import com.kukus.library.Constant.Companion.isValidContextForGlide
import com.kukus.library.FirebaseUtils.Companion.getPrice
import com.kukus.library.FirebaseUtils.Companion.getPricePromo
import com.kukus.library.FirebaseUtils.Companion.getPromotion
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.library.ExpandableHeightListView
import com.kukus.library.model.Order
import com.kukus.library.model.User
import org.jetbrains.anko.toast

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.RecyclerHolder>() {

    private val expansionsCollection = ExpansionLayoutCollection()
    private var list: ArrayList<Ship> = arrayListOf()
    private lateinit var context: Context
    private var message = ""
    lateinit var print : Printer

    init {
        expansionsCollection.openOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        context = parent.context
        print = Printer(context)

        return RecyclerHolder.buildFor(parent)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {

        val ship = getItem(position)

        holder.menu_id.text = ship.ship_id
        holder.menu_time.text = DateFormat.format("HH:mm:ss", ship.timestamp)
        holder.menu_status.text = ship.status.name
        holder.menu_counter.text = (position + 1).toString()
        holder.menu_address.text = if (ship.address.street != "") ship.address.street else "---"
        holder.txt_delivery.text = ("LE " + ship.delivery)
        holder.list_order.adapter = OrderAdapterCount(context, ship.menu, false)
        holder.txt_note.text = ship.note

        if(ship.note.isNotEmpty()){

            holder.label_note.visibility = View.VISIBLE
            holder.txt_note.visibility = View.VISIBLE

        }else{

            holder.label_note.visibility = View.GONE
            holder.txt_note.visibility = View.GONE

        }

        setupListener(holder, ship)
        setupStatus(holder, ship)
        setPrice(holder, ship)

    }


    private fun setupStatus(holder: RecyclerHolder, ship: Ship) {

        when (ship.status) {

            STATUS.WAITING -> {

                holder.img_status.setImageResource(R.drawable.ic_waiting)
                holder.menu_status.setTextColor(Color.parseColor("#DDDDDD"))

            }
            STATUS.PENDING -> {

                holder.btn_placed.visibility = View.GONE
                holder.btn_cancel.visibility = View.GONE

                holder.img_status.setImageResource(R.drawable.ic_placed)
                holder.menu_status.setTextColor(Color.parseColor("#2196F3"))


            }
            STATUS.DISPATCHED -> {

                holder.btn_placed.visibility = View.GONE
                holder.btn_cancel.visibility = View.GONE

                holder.img_status.setImageResource(R.drawable.ic_dispach)
                holder.menu_status.setTextColor(Color.parseColor("#FFC107"))

            }
            STATUS.COMPLETE -> {

                holder.btn_placed.visibility = View.GONE
                holder.btn_cancel.visibility = View.GONE

                holder.img_status.setImageResource(R.drawable.ic_completed)
                holder.menu_status.setTextColor(Color.parseColor("#32c360"))
            }
            STATUS.CANCEL -> {

                holder.btn_cancel.visibility = View.GONE
                holder.btn_placed.visibility = View.GONE

                holder.img_status.setImageResource(R.drawable.ic_cancle)
                holder.menu_status.setTextColor(Color.parseColor("#F44336"))
                holder.menu_id.paint.flags = (Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
            }
            else -> {


            }
        }
    }

    private fun setPrice(holder: RecyclerHolder, ship: Ship) {

        val priceAll = getPriceAll(ship.menu)
        var totalPrice = 0f

        if (ship.wallet > 0) {

            holder.txt_wallet.text = ("- LE ${ship.wallet}")
            holder.layout_wallet.visibility = View.VISIBLE

        } else {

            holder.layout_wallet.visibility = View.GONE

        }

        if (ship.promoAmount > 0 && ship.promo != "") {

            val promotion = getPromotion(priceAll, ship.promoAmount)
            totalPrice = getPricePromo(priceAll, ship.delivery, promotion, ship.wallet)

            holder.txt_promo.text = StringBuilder("- LE $promotion")
            holder.label_promo.text = StringBuilder("Promotion Code (${ship.promo}) - ${ship.promoAmount}%")
            holder.layout_promo.visibility = View.VISIBLE

        } else {

            totalPrice = getPrice(priceAll, ship.delivery, ship.wallet)

            holder.layout_promo.visibility = View.GONE

        }

        holder.txt_price.text = StringBuilder("LE $totalPrice")

    }

    private fun getItem(position: Int): Ship {
        return list[position]
    }

    private fun getPriceAll(menus: ArrayList<Order>): Float {

        var price = 0f

        menus.forEach {
            price += (it.price1 + it.price2) * (it.quantity)
        }

        return price
    }

    var user = User()

    @SuppressLint("CheckResult")
    private fun setupListener(holder: RecyclerHolder, ship: Ship) {

        val users = getUser(ship.user_id)
        val ships = getShip(ship.date).child(ship.id)

        expansionsCollection.add(holder.expansionLayout)

        users.dataChanges().subscribe {
            if (it.exists()) {

                user = it.getValue(User::class.java)!!

                holder.menu_name.text = user.name

            }
        }

        holder.btn_placed.setOnClickListener {

            val update = mapOf("status" to STATUS.PENDING)

            message = "Order : #${ship.ship_id}\n"

            SweetAlertDialog(context)
                    .setTitleText("Are you sure?")
                    .setContentText(message)
                    .setConfirmText("Yes, place it!")
                    .setConfirmClickListener { sDialog ->

                        ships.rxUpdateChildren(update).subscribe {

                            holder.expansionLayout.collapse(true)

                            sDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)

                            sDialog.titleText = "Want print receipt?"
                            sDialog.showContentText(false)
                            sDialog.confirmText = "Yes, Print it!"
                            sDialog.cancelText = "No"

                            sDialog.setConfirmClickListener {


                                print.printReceiptDelivery(ship, user)

                                Handler().postDelayed({

                                    context.toast("print!")

                                    sDialog.dismissWithAnimation()

                                    message = ""

                                }, 1000)

                            }

                        }
                    }.show()
        }

        holder.btn_cancel.setOnClickListener {

            message = "Order : #${ship.ship_id}"

            SweetAlertDialog(context)
                    .setTitleText("Are you sure?")
                    .setContentText(message)
                    .setConfirmText("Yes, Cancel it!")
                    .setConfirmClickListener { sDialog ->

                        dialogCancel(sDialog, ships, ship)

                    }.show()
        }

        holder.btn_details.text = "Print receipt"
        holder.btn_details.setOnClickListener {

//            val intent = Intent(context, ActivityDetails::class.java)
//            context.startActivity(intent)

            //print.printReceiptDelivery(ship, user)
           print.printReceiptDelivery(ship, user)


        }

        if (isValidContextForGlide(context)) {

            val latLng = LatLng(ship.address.lat.toDouble(), ship.address.lng.toDouble())

            Glide.with(context).load(Constant.getMapStatic(latLng)).into(holder.img_map)

            holder.img_map.setOnClickListener {

                val intent = Intent(context, ActivityOrderMap::class.java)

                intent.putExtra("lat", ship.address.lat)
                intent.putExtra("lng", ship.address.lng)

                context.startActivity(intent)
            }
        }
    }


    private fun dialogCancel(sDialog: SweetAlertDialog, ships: DatabaseReference, ship: Ship) {

        val builder =  AlertDialog.Builder(context)
        builder.setTitle("Add Note")
        builder.setCancelable(false)


        val container = LinearLayout(context)
        container.orientation = LinearLayout.VERTICAL
        val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParam.setMargins(40, 0 , 40, 0)

        val input = EditText(context)
        input.layoutParams = layoutParam
        input.hint = "Add note why cancel order"

        container.addView(input, layoutParam)

        builder.setView(container)

        builder.setPositiveButton("OK") { dialog, _ ->

            val note = input.text.toString()

            val update = mapOf("note" to note, "status" to STATUS.CANCEL)

            ships.rxUpdateChildren(update).subscribe {

                Handler().postDelayed({

                    sDialog.dismissWithAnimation()

                }, 500)
            }

        }

        builder.setNegativeButton("Cancel"){ dialog, _ ->

            sDialog.dismissWithAnimation()
        }

        builder.show()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun swapAdapter(list: ArrayList<Ship>) {
        this.list = list
        notifyDataSetChanged()
    }

    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var expansionLayout: ExpansionLayout = itemView.findViewById(R.id.expansionLayout)
        var img_status: ImageView = itemView.findViewById(R.id.img_status)
        var menu_id: TextView = itemView.findViewById(R.id.txt_order_id)
        var menu_time: TextView = itemView.findViewById(R.id.txt_order_time)
        var menu_counter: TextView = itemView.findViewById(R.id.txt_order_counter)
        var menu_name: TextView = itemView.findViewById(R.id.txt_table)
        var menu_status: TextView = itemView.findViewById(R.id.txt_order_status)
        var menu_address: TextView = itemView.findViewById(R.id.txt_date)
        var img_map = itemView.findViewById<ImageView>(R.id.img_map)

        var list_order: ExpandableHeightListView = itemView.findViewById(R.id.list_order)

        var layout_delivery: LinearLayout = itemView.findViewById(R.id.layout_delivery)
        var label_delivery: TextView = itemView.findViewById(R.id.label_delivery)
        var txt_delivery: TextView = itemView.findViewById(R.id.txt_delivery)

        var layout_price: LinearLayout = itemView.findViewById(R.id.layout_price)
        var label_price: TextView = itemView.findViewById(R.id.label_price)
        var txt_price: TextView = itemView.findViewById(R.id.txt_price)

        var layout_wallet: LinearLayout = itemView.findViewById(R.id.layout_wallet)
        var label_wallet: TextView = itemView.findViewById(R.id.label_wallet)
        var txt_wallet: TextView = itemView.findViewById(R.id.txt_wallet)

        var layout_promo: LinearLayout = itemView.findViewById(R.id.layout_promo)
        var label_promo: TextView = itemView.findViewById(R.id.label_promo)
        var txt_promo: TextView = itemView.findViewById(R.id.txt_promo)

        var btn_placed: Button = itemView.findViewById(R.id.btn_order_placed)
        var btn_cancel: Button = itemView.findViewById(R.id.btn_order_cancel)
        //var btn_print: Button = itemView.findViewById(R.id.btn_order_print)
        var btn_details: TextView = itemView.findViewById(R.id.btn_details)

        var label_note: TextView = itemView.findViewById(R.id.label_note)
        var txt_note: TextView = itemView.findViewById(R.id.txt_note)

        companion object {

            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_order, viewGroup, false))
            }
        }
    }
}
