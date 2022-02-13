package com.kukus.customer.point.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kukus.customer.R
import com.kukus.library.model.Point
import com.kukus.library.model.Wallet
import java.lang.StringBuilder
import java.util.*

/**
 * Created by anas on 28/01/2018.
 */

class PointAdapter : RecyclerView.Adapter<PointAdapter.MyViewHolder>() {

    private var list: ArrayList<Point> = arrayListOf()
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        context = parent.context


        val mInflater = LayoutInflater.from(context)
        val view = mInflater.inflate(R.layout.list_point, parent, false)

        return MyViewHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.date.text = DateFormat.format("dd MMMM yyyy - HH:mm:ss a",  list[position].date)

        if(list[position].inOut == "in"){
            holder.point.setTextColor(Color.parseColor("#32c360"))
            holder.point.text = ("+ " + list[position].point.toString())

        }else{
            holder.point.setTextColor(Color.RED)
            holder.point.text = ("- " + list[position].point.toString())
        }

        holder.title.text = (list[position].note)
    }


    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var title: TextView = itemView.findViewById(R.id.title) as TextView
        internal var point: TextView = itemView.findViewById(R.id.point) as TextView
        internal var date: TextView = itemView.findViewById(R.id.date) as TextView

    }

    fun swapAdapter(list: ArrayList<Point>) {
        this.list = list
    }

}