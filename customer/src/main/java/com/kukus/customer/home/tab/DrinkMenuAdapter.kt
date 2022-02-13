package com.kukus.customer.home.tab

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jakewharton.rxbinding2.view.RxView
import com.kukus.customer.R
import com.kukus.customer.home.ActivityHome
import com.kukus.customer.home.ActivityHomeDetails
import com.kukus.library.Constant.Companion.isValidContextForGlide
import com.kukus.library.interfaces.CountOrderListener
import com.kukus.library.model.CountOrder
import com.kukus.library.model.Menu
import com.kukus.library.model.Order
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by anas on 28/09/2018.
 */

class DrinkMenuAdapter(val context: Context) : RecyclerView.Adapter<DrinkMenuAdapter.MyViewHolder>() {

    private var mOrderList = arrayListOf<Order>()
    private var mCheckOrderList = ArrayList<String>()
    private var list: ArrayList<Menu> = arrayListOf()
    private lateinit var countOrderListener: CountOrderListener

    private var holderListener: MyViewHolder? = null
    private var mCountAllOrder = CountOrder()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        countOrderListener = context as ActivityHome

        val mInflater = LayoutInflater.from(context)

        val view = mInflater.inflate(R.layout.list_menu_drink, parent, false)

        return MyViewHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val id = list[position].id

        var countOrder_1 = Integer.parseInt(holder.count_1.text.toString())
        var countOrder_2 = Integer.parseInt(holder.count_2.text.toString())

        holder.name.text = list[position].name
        holder.description.text = list[position].description

        holder.label_price_1.text = list[position].label_price1
        holder.label_price_2.text = list[position].label_price2

        holder.price_1.text = list[position].price1.toString()
        holder.price_2.text = list[position].price2.toString()

        if (list[position].image != "") {

            if (isValidContextForGlide(context)) {

                Glide
                        .with(context)
                        .load(list[position].image)
                        .apply(RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                        )
                        .into(holder.image)

                holder.progressImage.visibility = View.GONE

            }

        }else{

            holder.progressImage.visibility = View.GONE

        }

        if (!list[position].available) {

            holder.label_not_available.visibility = View.VISIBLE
            holder.view.setBackgroundResource(R.drawable.shape_radius_overlay_dark)

            holder.down_1.isEnabled = false
            holder.up_1.isEnabled = false

            holder.down_1.setBackgroundResource(R.drawable.shape_radius_grey_5)
            holder.up_1.setBackgroundResource(R.drawable.shape_radius_grey_5)

            holder.down_2.isEnabled = false
            holder.up_2.isEnabled = false

            holder.down_2.setBackgroundResource(R.drawable.shape_radius_grey_5)
            holder.up_2.setBackgroundResource(R.drawable.shape_radius_grey_5)

            holder.rite_1.setTextColor(Color.parseColor("#DDDDDD"))
            holder.price_1.setTextColor(Color.parseColor("#DDDDDD"))
            holder.price_2.setTextColor(Color.parseColor("#DDDDDD"))

            holder.rite_1.setTextColor(Color.parseColor("#DDDDDD"))
            holder.rite_2.setTextColor(Color.parseColor("#DDDDDD"))

            holder.count_1.setTextColor(Color.parseColor("#DDDDDD"))
            holder.count_2.setTextColor(Color.parseColor("#DDDDDD"))

        }

        if (list[position].price1 == 0f) {

            holder.layout_price_1.visibility = View.GONE
            holder.layout_count_1.visibility = View.GONE
            holder.divider.visibility = View.GONE

        }

        if (list[position].price2 == 0f) {

            holder.layout_price_2.visibility = View.GONE
            holder.layout_count_2.visibility = View.GONE
            holder.divider.visibility = View.GONE

        }

        if(!isOpen){

            holder.view.setBackgroundResource(R.drawable.shape_radius_overlay_dark)

            holder.down_1.isEnabled = false
            holder.up_1.isEnabled = false

            holder.down_1.setBackgroundResource(R.drawable.shape_radius_grey_5)
            holder.up_1.setBackgroundResource(R.drawable.shape_radius_grey_5)

            holder.rite_1.setTextColor(Color.parseColor("#DDDDDD"))
            holder.price_1.setTextColor(Color.parseColor("#DDDDDD"))
            holder.count_1.setTextColor(Color.parseColor("#DDDDDD"))


            holder.down_2.isEnabled = false
            holder.up_2.isEnabled = false

            holder.down_2.setBackgroundResource(R.drawable.shape_radius_grey_5)
            holder.up_2.setBackgroundResource(R.drawable.shape_radius_grey_5)

            holder.rite_2.setTextColor(Color.parseColor("#DDDDDD"))
            holder.price_2.setTextColor(Color.parseColor("#DDDDDD"))
            holder.count_2.setTextColor(Color.parseColor("#DDDDDD"))
        }

        holder.image.setOnClickListener {

            holderListener = holder

            val intent = Intent(context, ActivityHomeDetails::class.java)

            intent.putExtra("title", holder.name.text.toString())
            intent.putExtra("description", holder.description.text.toString())
            intent.putExtra("label1", holder.label_price_1.text.toString())
            intent.putExtra("label2", holder.label_price_2.text.toString())
            intent.putExtra("price1", holder.price_1.text.toString())
            intent.putExtra("price2", holder.price_2.text.toString())
            intent.putExtra("image", list[position].image)

            context.startActivity(intent)

        }

        RxView.clicks(holder.down_1)
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    if (countOrder_1 > 0) {

                        if (countOrder_1 == 1) holder.down_1.setBackgroundResource(R.drawable.shape_radius_grey_5)
                        true

                    } else {

                        false
                    }
                }
                .subscribe {

                    if (countOrder_1 == 20) holder.up_1.setBackgroundResource(R.drawable.shape_radius_red_5)

                    countOrder_1--
                    holder.count_1.text = countOrder_1.toString()

                    mCountAllOrder.countAllOrder--
                    mCountAllOrder.countAllPrice -= list[position].price1

                    countOrderListener.countAllOrder(mCountAllOrder.countAllOrder, list[position].type)
                    countOrderListener.countAllPrice(mCountAllOrder.countAllPrice, list[position].type)

                    listOrder(id, list[position].label_price1, position, countOrder_1)

                }

        RxView.clicks(holder.down_2)
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    if (countOrder_2 > 0) {

                        if (countOrder_2 == 1) holder.down_2.setBackgroundResource(R.drawable.shape_radius_grey_5)
                        true

                    } else {

                        false
                    }
                }
                .subscribe {

                    if (countOrder_2 == 20) holder.up_2.setBackgroundResource(R.drawable.shape_radius_red_5)

                    countOrder_2--
                    holder.count_2.text = countOrder_2.toString()

                    mCountAllOrder.countAllOrder--
                    mCountAllOrder.countAllPrice -= list[position].price2

                    countOrderListener.countAllOrder(mCountAllOrder.countAllOrder, list[position].type)
                    countOrderListener.countAllPrice(mCountAllOrder.countAllPrice, list[position].type)

                    listOrder(id, list[position].label_price2, position, countOrder_2)

                }

        RxView.clicks(holder.up_1)
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    if (countOrder_1 < 20) {
                        if (countOrder_1 == 19) holder.up_1.setBackgroundResource(R.drawable.shape_radius_grey_5)
                        true

                    } else {
                        false
                    }
                }
                .subscribe {

                    if (countOrder_1 == 0) holder.down_1.setBackgroundResource(R.drawable.shape_radius_red_5)

                    countOrder_1++
                    holder.count_1.text = countOrder_1.toString()

                    mCountAllOrder.countAllOrder++
                    mCountAllOrder.countAllPrice += list[position].price1

                    countOrderListener.countAllOrder(mCountAllOrder.countAllOrder, list[position].type)
                    countOrderListener.countAllPrice(mCountAllOrder.countAllPrice, list[position].type)

                    listOrder(id, list[position].label_price1, position, countOrder_1)

                }

        RxView.clicks(holder.up_2)
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    if (countOrder_2 < 20) {
                        if (countOrder_2 == 19) holder.up_2.setBackgroundResource(R.drawable.shape_radius_grey_5)
                        true

                    } else {
                        false
                    }
                }
                .subscribe {

                    if (countOrder_2 == 0) holder.down_2.setBackgroundResource(R.drawable.shape_radius_red_5)

                    countOrder_2++
                    holder.count_2.text = countOrder_2.toString()

                    mCountAllOrder.countAllOrder++
                    mCountAllOrder.countAllPrice += list[position].price2

                    countOrderListener.countAllOrder(mCountAllOrder.countAllOrder, list[position].type)
                    countOrderListener.countAllPrice(mCountAllOrder.countAllPrice, list[position].type)

                    listOrder(id, list[position].label_price2, position, countOrder_2)

                }

    }

    fun swapAdapter(list: ArrayList<Menu>) {
        this.list = list
    }

    private var isOpen = false

    fun isOpen(value: Boolean){
        isOpen = value
    }

    private fun listOrder(id: String, drink: String, position: Int, countOrder: Int) {

        if (countOrder != 0) {

            if (!mCheckOrderList.contains(id + drink)) {

                mCheckOrderList.add(id + drink)

                val order = Order()
                order.menu_id = id + drink
                order.title = list[position].name + " ($drink)"
                order.type = list[position].type.name
                order.quantity = countOrder


                when (drink) {
                    list[position].label_price1 -> {
                        order.price1 = list[position].price1
                    }
                    list[position].label_price2 -> {
                        order.price1 = list[position].price2
                    }
                }

                //order.type = drink

                mOrderList.add(order)

            } else {

                for (i in 0 until mOrderList.size) {
                    if (mOrderList[i].menu_id == id + drink) {
                        mOrderList[i].quantity = countOrder
                        break
                    }
                }
            }

        } else {

            for (i in 0 until mOrderList.size) {
                if (mOrderList[i].menu_id == id + drink) {
                    mOrderList.removeAt(i)
                    mCheckOrderList.remove(id + drink)
                    break
                }
            }
        }

        countOrderListener.listAllOrder(mOrderList, list[position].type)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var progressImage: ProgressBar = itemView.findViewById(R.id.progressImage) as ProgressBar
        internal var name: TextView = itemView.findViewById(R.id.text_name) as TextView
        internal var description: TextView = itemView.findViewById(R.id.text_description) as TextView
        internal var image: ImageView = itemView.findViewById(R.id.image_menu) as ImageView

        internal var divider: View = itemView.findViewById(R.id.divider) as View

        internal var layout_count_1: LinearLayout = itemView.findViewById(R.id.count_1) as LinearLayout
        internal var layout_count_2: LinearLayout = itemView.findViewById(R.id.count_2) as LinearLayout

        internal var layout_price_1: LinearLayout = itemView.findViewById(R.id.price_1) as LinearLayout
        internal var layout_price_2: LinearLayout = itemView.findViewById(R.id.price_2) as LinearLayout

        internal var label_price_1: TextView = itemView.findViewById(R.id.label_price_1) as TextView
        internal var label_price_2: TextView = itemView.findViewById(R.id.label_price_2) as TextView

        internal var rite_1: TextView = itemView.findViewById(R.id.txt_rite_1) as TextView
        internal var rite_2: TextView = itemView.findViewById(R.id.txt_rite_2) as TextView

        internal var price_1: TextView = itemView.findViewById(R.id.txt_price_1) as TextView
        internal var price_2: TextView = itemView.findViewById(R.id.txt_price_2) as TextView

        internal var count_1: TextView = itemView.findViewById(R.id.txt_count_1) as TextView
        internal var count_2: TextView = itemView.findViewById(R.id.txt_count_2) as TextView

        internal var down_1: ImageView = itemView.findViewById(R.id.iv_down_1) as ImageView
        internal var down_2: ImageView = itemView.findViewById(R.id.iv_down_2) as ImageView

        internal var up_1: ImageView = itemView.findViewById(R.id.iv_up_1) as ImageView
        internal var up_2: ImageView = itemView.findViewById(R.id.iv_up_2) as ImageView


        internal var label_not_available: TextView = itemView.findViewById(R.id.label_not_available) as TextView
        internal var view: View = itemView.findViewById(R.id.view) as View

    }

}