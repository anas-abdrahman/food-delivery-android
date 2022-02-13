package com.kukus.administrator.waiter.tab

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import com.kukus.administrator.R
import com.kukus.administrator.waiter.ActivityAddOrder
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils
import com.kukus.library.interfaces.CountOrderListener
import com.kukus.library.model.CountOrder
import com.kukus.library.model.Menu
import com.kukus.library.model.Order
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.find

open class BaseAdapterMenu(private val statusOrder: Constant.Companion.TYPE_ORDER) : RecyclerView.Adapter<BaseRecyclerAdapter>() {

    var list: ArrayList<Menu> = arrayListOf()
    private var mCountAllOrder = CountOrder()
    private var finalOrder = arrayListOf<Order>()
    lateinit var context: Context
    private lateinit var countOrderListener: CountOrderListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerAdapter {
        context = parent.context
        countOrderListener = context as ActivityAddOrder
        return BaseRecyclerAdapter.buildFor(parent, statusOrder)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: BaseRecyclerAdapter, position: Int) {

        val itemView = holder.getView()
        val menuName = itemView.find<TextView>(R.id.text_title)

        menuName.text = list[position].name

        val count1: TextView = itemView.find(R.id.text_count1) as TextView
        val down1: ImageView = itemView.find(R.id.iv_down1) as ImageView
        val up1: ImageView = itemView.find(R.id.iv_up1) as ImageView

        count1.text = list[position].count1.toString()

        if (list[position].count1 > 0) {
            down1.setBackgroundResource(R.drawable.shape_radius_red_5)
        }

        var countButton1 = count1.text.toString().toInt()

        RxView.clicks(down1)
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    if (countButton1 > 0) {
                        if (countButton1 == 1) down1.setBackgroundResource(R.drawable.shape_radius_grey_5)
                        true
                    } else {
                        false
                    }
                }
                .subscribe {

                    if (countButton1 == 20) up1.setBackgroundResource(R.drawable.shape_radius_red_5)

                    countButton1--

                    count1.text = countButton1.toString()

                    if (statusOrder == Constant.Companion.TYPE_ORDER.DRINK) {
                        removeOrder(position, list[position].label_price1)
                    } else {
                        removeOrder(position)
                    }
                }

        RxView.clicks(up1)
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    if (countButton1 < 20) {
                        if (countButton1 == 19) up1.setBackgroundResource(R.drawable.shape_radius_grey_5)
                        true
                    } else {
                        false
                    }
                }
                .subscribe {

                    if (countButton1 == 0) down1.setBackgroundResource(R.drawable.shape_radius_red_5)

                    countButton1++

                    count1.text = countButton1.toString()

                    if (statusOrder == Constant.Companion.TYPE_ORDER.DRINK) {
                        addOrder(position, list[position].label_price1)
                    } else {
                        addOrder(position)
                    }

                }

        if (statusOrder == Constant.Companion.TYPE_ORDER.DRINK) {

            val txt_label_1: TextView = itemView.findViewById(R.id.txt_label_1) as TextView
            val layout_count1 = itemView.find<LinearLayout>(R.id.count1)

            val txt_label_2: TextView = itemView.findViewById(R.id.txt_label_2) as TextView
            val layout_count2 = itemView.find<LinearLayout>(R.id.count2)


            if (list[position].price1 == 0f) {

                layout_count1.visibility = View.GONE

            } else {
                txt_label_1.text = ("${list[position].label_price1} :  ")
            }

            if (list[position].price2 == 0f) {

                layout_count2.visibility = View.GONE

            } else {
                txt_label_2.text = ("${list[position].label_price2} :  ")
            }


            val count2: TextView = itemView.findViewById(R.id.text_count2) as TextView
            val down2: ImageView = itemView.findViewById(R.id.iv_down2) as ImageView
            val up2: ImageView = itemView.findViewById(R.id.iv_up2) as ImageView

            count2.text = list[position].count2.toString()

            if (list[position].count2 > 0) {
                down2.setBackgroundResource(R.drawable.shape_radius_red_5)
            }

            var countButton2 = count2.text.toString().toInt()

            RxView.clicks(down2)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter {
                        if (countButton2 > 0) {
                            if (countButton2 == 1) down2.setBackgroundResource(R.drawable.shape_radius_grey_5)
                            true

                        } else {
                            false
                        }
                    }
                    .subscribe {

                        if (countButton2 == 20) up2.setBackgroundResource(R.drawable.shape_radius_red_5)

                        countButton2--

                        count2.text = countButton2.toString()

                        removeOrder(position, list[position].label_price2)

                    }

            RxView.clicks(up2)
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter {
                        if (countButton2 < 20) {
                            if (countButton2 == 19) up2.setBackgroundResource(R.drawable.shape_radius_grey_5)
                            true
                        } else {
                            false
                        }
                    }
                    .subscribe {

                        if (countButton2 == 0) down2.setBackgroundResource(R.drawable.shape_radius_red_5)

                        countButton2++

                        count2.text = countButton2.toString()

                        addOrder(position, list[position].label_price2)

                    }

        }

    }

    private fun addOrder(position: Int, type: String = "") {

        when (statusOrder) {

            Constant.Companion.TYPE_ORDER.FOOD, Constant.Companion.TYPE_ORDER.EXTRA -> {

                val order = Order()

                order.id = FirebaseUtils.getTimestamp.toString()
                order.menu_id = list[position].id
                order.title = list[position].name
                order.price1 = list[position].price1
                order.type = list[position].type.name
                order.quantity = 1

                finalOrder.add(order)

            }

            Constant.Companion.TYPE_ORDER.DRINK -> {

                val order = Order()
                order.id = list[position].id
                order.menu_id = list[position].id + type
                order.title = list[position].name + " ($type)"
                order.type = list[position].type.name
                order.quantity = 1

                when (type) {
                    list[position].label_price1 -> {
                        order.price1 = list[position].price1
                    }
                    list[position].label_price2 -> {
                        order.price1 = list[position].price2
                    }
                }

                finalOrder.add(order)

            }

        }

        upCount(position, type)

    }

    fun updateOrder(position: Int, order: Order, countOrder: Int, type: String = "") {

        if( type != "" ){

            if(order.price1 != 0f){

                list[position].count1 = countOrder

            }else{

                list[position].count2 = countOrder

            }

        }else{

            list[position].count1 = countOrder

        }

        finalOrder.add(order)

        upCount(position, type)

    }

    private fun removeOrder(position: Int, type: String = "") {

        for (i in finalOrder.size - 1 downTo 0) {

            if (finalOrder[i].menu_id == list[position].id + type) {

                finalOrder.removeAt(i)

                break
            }
        }

        downCount(position, type)
    }

    private fun upCount(position: Int, type: String = "") {


        if (type != "") {

            when (type) {
                list[position].label_price1 -> {
                    mCountAllOrder.countAllPrice += list[position].price1
                }
                list[position].label_price2 -> {
                    mCountAllOrder.countAllPrice += list[position].price2
                }
            }

        } else {

            mCountAllOrder.countAllPrice += list[position].price1

        }

        mCountAllOrder.countAllOrder++

        updateCountOrder(position)

    }


    private fun downCount(position: Int, type: String = "") {

        if (type != "") {

            when (type) {
                list[position].label_price1 -> {
                    mCountAllOrder.countAllPrice -= list[position].price1
                }
                list[position].label_price2 -> {
                    mCountAllOrder.countAllPrice -= list[position].price2
                }
            }

        } else {

            mCountAllOrder.countAllPrice -= list[position].price1

        }

        mCountAllOrder.countAllOrder--


        updateCountOrder(position)

    }

    private fun updateCountOrder(position: Int) {
        countOrderListener.listAllOrder(finalOrder, list[position].type)
        countOrderListener.countAllOrder(mCountAllOrder.countAllOrder, list[position].type)
        countOrderListener.countAllPrice(mCountAllOrder.countAllPrice, list[position].type)
    }

    fun swapAdapter(list: ArrayList<Menu>) {
        this.list = list
        notifyDataSetChanged()
    }

}