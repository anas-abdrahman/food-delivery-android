package com.kukus.administrator.orderWaiter.tab

import android.content.Context
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
import com.androidhuman.rxfirebase2.database.rxUpdateChildren
import com.github.florent37.expansionpanel.ExpansionLayout
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import com.kukus.administrator.R
import com.kukus.library.Constant.Companion.STATUS
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.database.DatabaseReference
import com.kukus.administrator.printer.Printer
import com.kukus.administrator.waiter.tab.OrderAdapterCount
import com.kukus.library.Constant.Companion.TABLE
import com.kukus.library.Constant.Companion.getOrderList
import com.kukus.library.FirebaseUtils.Companion.getTable
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.library.ExpandableHeightListView
import com.kukus.library.model.Order
import com.kukus.library.model.Waiter
import org.jetbrains.anko.toast

class OrderWaiterAdapter : RecyclerView.Adapter<OrderWaiterAdapter.RecyclerHolder>() {

    private val expansionsCollection = ExpansionLayoutCollection()
    private var list: ArrayList<Waiter> = arrayListOf()
    private lateinit var context: Context
    private var message = ""

    private lateinit var printer: Printer

    init {
        expansionsCollection.openOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        context = parent.context

        printer = Printer(context)

        return RecyclerHolder.buildFor(parent)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {

        holder.menu_id.text = list[position].ship_id
        holder.menu_name.text = ("Table " + list[position].table)
        holder.menu_date.text = DateFormat.format("HH:mm:ss", list[position].timestamp)
        holder.menu_status.text = list[position].status.name
        holder.list_order.adapter = OrderAdapterCount(context, getOrderList(list[position].menu), false)
        holder.price.text = ("LE ${getPriceAll(list[position].menu)}")
        holder.menu_counter.text = (position + 1).toString()

        if(list[position].discount > 0){

            holder.label_promo.visibility = View.VISIBLE
            holder.txt_promo.visibility = View.VISIBLE

            holder.txt_promo.text = "LE ${list[position].discount}"

            holder.price.text = ("LE ${getPriceAll(list[position].menu) - list[position].discount}")


        }else{
            holder.label_promo.visibility = View.GONE
            holder.txt_promo.visibility = View.GONE
        }

        setupListener(holder, position)
        setupStatus(holder, position)
    }

    private fun getPriceAll(menus : ArrayList<Order>) : Float{

        var price = 0f

        menus.forEach {
            price += (it.price1)
        }

        return price
    }

    private fun setupStatus(holder: RecyclerHolder, position: Int) {

        when (list[position].status) {

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

            else -> {}
        }
    }

    private fun setupListener(holder: RecyclerHolder, position: Int) {

        val waiter = getWaiter(list[position].date).child(list[position].id)

        expansionsCollection.add(holder.expansionLayout)

        holder.btn_placed.setOnClickListener {

            val update = mapOf("status" to STATUS.PENDING)

            message = "placed order : ${list[position].ship_id}"

            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText(message)
                    .setConfirmText("Yes, place it!")
                    .setConfirmClickListener { sDialog ->

                        waiter.rxUpdateChildren(update).subscribe {

                            getTable("table_${list[position].table}").setValue(TABLE.PLACED)

                            holder.expansionLayout.collapse(true)

                            sDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)

                            sDialog.titleText = "Want print receipt?"
                            sDialog.showContentText(false)
                            sDialog.confirmText = "Yes, Print it!"
                            sDialog.cancelText = "No"

                            sDialog.setConfirmClickListener {

                                //val print = Print(context, context as ActivityOrders)

                                //print.runPrintReceiptSequence(ship, user)

                                Handler().postDelayed({

                                    context.toast("print!")

                                    sDialog.dismissWithAnimation()

                                    message = ""

                                }, 500)

                            }

                        }
                    }.show()
        }

        holder.btn_cancel.setOnClickListener {

            val update = mapOf("status" to STATUS.CANCEL)

            message = "cancel order : ${list[position].ship_id}"

            SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText(message)
                    .setConfirmText("Yes, cancel it!")
                    .setConfirmClickListener { sDialog ->

                        dialogCancel(sDialog, waiter)

                        /*waiter.rxUpdateChildren(update).subscribe {

                            holder.expansionLayout.collapse(true)

                            Handler().postDelayed({

                                sDialog
                                        .setTitleText("Canceled!")
                                        .setContentText(message)
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)

                                message = ""

                            }, 1000)
                        }*/
                    }.show()
        }

        holder.btn_details.setOnClickListener {

            //val intent = Intent(context, ActivityDetails::class.java)
            //context.startActivity(intent)

            printer.printReceiptCustomer(list[position])

        }

        holder.switch_details.setOnCheckedChangeListener{ component, _ ->

            if(component.isChecked){

                holder.list_order.adapter = OrderAdapterCount(context, getOrderList(list[position].menu, true), true)

            }else{

                holder.list_order.adapter = OrderAdapterCount(context, getOrderList(list[position].menu, false), false)

            }

        }

    }

    private fun dialogCancel(sDialog: SweetAlertDialog, waiter: DatabaseReference) {

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

            waiter.rxUpdateChildren(update).subscribe {

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

    fun swapAdapter(list: ArrayList<Waiter>) {
        this.list = list
        notifyDataSetChanged()
    }

    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var expansionLayout: ExpansionLayout = itemView.findViewById(R.id.expansionLayout)
        var img_status: ImageView = itemView.findViewById(R.id.img_status)
        var menu_id: TextView = itemView.findViewById(R.id.txt_order_id)
        var menu_date: TextView = itemView.findViewById(R.id.txt_date)
        var menu_counter: TextView = itemView.findViewById(R.id.txt_order_counter)
        var menu_name: TextView = itemView.findViewById(R.id.txt_table)
        var menu_status: TextView = itemView.findViewById(R.id.txt_order_status)
        var price: TextView = itemView.findViewById(R.id.txt_total)
        var txt_promo: TextView = itemView.findViewById(R.id.txt_promo)
        var label_promo: TextView = itemView.findViewById(R.id.label_promo)
        var list_order: ExpandableHeightListView = itemView.findViewById(R.id.list_order)
        var btn_placed: Button = itemView.findViewById(R.id.btn_order_placed)
        var btn_cancel: Button = itemView.findViewById(R.id.btn_order_cancel)
        var btn_details: TextView = itemView.findViewById(R.id.btn_details)
        var switch_details: Switch = itemView.findViewById(R.id.switch_details)

        companion object {

            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_order_waiter_adapter, viewGroup, false))
            }
        }
    }
}
