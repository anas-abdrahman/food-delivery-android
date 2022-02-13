package com.kukus.customer.home.tab

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.view.RxView
import com.kukus.customer.home.ActivityHomeDetails
import com.kukus.customer.R
import com.kukus.library.model.CountOrder
import com.kukus.customer.home.ActivityHome
import com.kukus.library.interfaces.CountOrderListener
import com.kukus.library.model.Menu
import com.kukus.library.model.Order
import io.reactivex.android.schedulers.AndroidSchedulers
import com.kukus.library.Constant.Companion.isValidContextForGlide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


/**
 * Created by anas on 28/09/2018.
 */

class FoodMenuAdapter(val context: Context) : RecyclerView.Adapter<FoodMenuAdapter.MyViewHolder>() {

    private var mCheckOrderList = ArrayList<String>()
    private var mOrderList = arrayListOf<Order>()
    private var list: ArrayList<Menu> = arrayListOf()
    lateinit var countOrderListener: CountOrderListener
    private var holderListener: MyViewHolder? = null

    var mCountAllOrder = CountOrder()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        countOrderListener = context as ActivityHome

        val mInflater = LayoutInflater.from(context)

        val view = mInflater.inflate(R.layout.list_menu_food, parent, false)

        return MyViewHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val id = list[position].id

        var countOrder = 0

        val countLimit = if(list[position].limit > 0){
            list[position].limit - list[position].currentLimit
        }else{
            30
        }

        holder.name.text = list[position].name
        holder.description.text = list[position].description
        holder.price.text = list[position].price1.toString()

        if (list[position].image != "") {

            if (isValidContextForGlide(context)) {

                Glide
                        .with(context)
                        .load(list[position].image)
                        .apply(RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                        )
                        .into(holder.image)

                holder.progressImage.visibility = View.GONE

            }

        } else {

            holder.progressImage.visibility = View.GONE

        }

        if (countLimit > 30 && list[position].limit != 0) {

            holder.textLimit.visibility = View.GONE
            holder.textLimit.text = ""

        }else if (countLimit in 1..30 && list[position].limit != 0) {

            holder.textLimit.visibility = View.VISIBLE
            holder.textLimit.text = "$countLimit left"

        } else if (list[position].limit == 0) {

            holder.textLimit.visibility = View.GONE
            holder.textLimit.text = ""

        } else {

            list[position].available = false

        }

        if (!list[position].available) {

            holder.label_not_available.visibility = View.VISIBLE
            holder.view.setBackgroundResource(R.drawable.shape_radius_overlay_dark)

            holder.down.isEnabled = false
            holder.up.isEnabled = false

            holder.down.setBackgroundResource(R.drawable.shape_radius_grey_5)
            holder.up.setBackgroundResource(R.drawable.shape_radius_grey_5)

            holder.rite.setTextColor(Color.parseColor("#DDDDDD"))
            holder.price.setTextColor(Color.parseColor("#DDDDDD"))

            holder.count.setTextColor(Color.parseColor("#DDDDDD"))

        } else {

            holder.label_not_available.visibility = View.GONE
            holder.view.setBackgroundResource(R.drawable.shape_radius_overlay_light)

        }

        holder.image.setOnClickListener {

            holderListener = holder

            val intent = Intent(context, ActivityHomeDetails::class.java)

            intent.putExtra("title", holder.name.text.toString())
            intent.putExtra("description", holder.description.text.toString())
            intent.putExtra("label1", "Price")
            intent.putExtra("price1", holder.price.text.toString())
            intent.putExtra("image", list[position].image)

            context.startActivity(intent)

        }

        if(!isOpen){

            holder.textLimit.visibility = View.GONE

            holder.view.setBackgroundResource(R.drawable.shape_radius_overlay_dark)

            holder.down.isEnabled = false
            holder.up.isEnabled = false

            holder.down.setBackgroundResource(R.drawable.shape_radius_grey_5)
            holder.up.setBackgroundResource(R.drawable.shape_radius_grey_5)

            holder.rite.setTextColor(Color.parseColor("#DDDDDD"))
            holder.price.setTextColor(Color.parseColor("#DDDDDD"))
            holder.count.setTextColor(Color.parseColor("#DDDDDD"))
        }

        RxView.clicks(holder.down)
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    if (countOrder > 0) {
                        if (countOrder == 1) holder.down.setBackgroundResource(R.drawable.shape_radius_grey_5)
                        true

                    } else {
                        false
                    }
                }
                .subscribe {

                    if (countOrder == countLimit) holder.up.setBackgroundResource(R.drawable.shape_radius_red_5)

                    countOrder--

                    holder.count.text = countOrder.toString()

                    mCountAllOrder.countAllOrder--
                    mCountAllOrder.countAllPrice -= list[position].price1

                    countOrderListener.countAllOrder(mCountAllOrder.countAllOrder, list[position].type)
                    countOrderListener.countAllPrice(mCountAllOrder.countAllPrice, list[position].type)

                    listOrder(id, position, countOrder)

                }

        RxView.clicks(holder.up)
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    if (countOrder < countLimit) {
                        if (countOrder == countLimit - 1) holder.up.setBackgroundResource(R.drawable.shape_radius_grey_5)
                        true

                    } else {
                        false
                    }
                }
                .subscribe {

                    if (countOrder == 0) holder.down.setBackgroundResource(R.drawable.shape_radius_red_5)

                    countOrder++

                    holder.count.text = countOrder.toString()

                    mCountAllOrder.countAllOrder++
                    mCountAllOrder.countAllPrice += list[position].price1

                    countOrderListener.countAllOrder(mCountAllOrder.countAllOrder, list[position].type)
                    countOrderListener.countAllPrice(mCountAllOrder.countAllPrice, list[position].type)

                    listOrder(id, position, countOrder)

                }

    }

    fun swapAdapter(list: ArrayList<Menu>) {
        this.list = list
    }

    private var isOpen = false

    fun isOpen(value: Boolean){
        isOpen = value
    }

    private fun listOrder(id: String, position: Int, countOrder: Int) {

        if (countOrder != 0) {

            if (!mCheckOrderList.contains(id)) {

                mCheckOrderList.add(id)

                val order = Order()
                order.menu_id = id
                order.title = list[position].name
                order.price1 = list[position].price1
                order.quantity = countOrder
                order.type = list[position].type.name

                mOrderList.add(order)

            } else {

                for (i in 0 until mOrderList.size) {
                    if (mOrderList[i].menu_id == id) {
                        mOrderList[i].quantity = countOrder
                        break
                    }
                }
            }

        } else {

            for (i in 0 until mOrderList.size) {
                if (mOrderList[i].menu_id == id) {
                    mOrderList.removeAt(i)
                    mCheckOrderList.remove(id)
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

        internal var layout: ConstraintLayout = itemView.findViewById(R.id.layout) as ConstraintLayout
        internal var progressImage: ProgressBar = itemView.findViewById(R.id.progressImage) as ProgressBar
        internal var name: TextView = itemView.findViewById(R.id.text_name) as TextView
        internal var description: TextView = itemView.findViewById(R.id.text_description) as TextView
        internal var price: TextView = itemView.findViewById(R.id.txt_price_1) as TextView
        internal var image: ImageView = itemView.findViewById(R.id.image_menu) as ImageView
        internal var rite: TextView = itemView.findViewById(R.id.txt_rite_1) as TextView
        internal var textLimit: TextView = itemView.findViewById(R.id.label_limit) as TextView

        internal var count: TextView = itemView.findViewById(R.id.txt_count_1) as TextView
        internal var down: ImageView = itemView.findViewById(R.id.iv_down_1) as ImageView
        internal var up: ImageView = itemView.findViewById(R.id.iv_up_1) as ImageView

        internal var label_not_available: TextView = itemView.findViewById(R.id.label_not_available) as TextView
        internal var view: View = itemView.findViewById(R.id.view) as View

    }
}