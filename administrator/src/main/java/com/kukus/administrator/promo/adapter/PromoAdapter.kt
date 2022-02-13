package com.kukus.administrator.promo.adapter

import android.annotation.SuppressLint
import android.app.FragmentTransaction
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kukus.administrator.R
import com.kukus.administrator.promo.DialogAddPromo
import com.kukus.library.model.Promo
import java.util.*

/**
 * Created by anas on 28/01/2018.
 */

class PromoAdapter : RecyclerView.Adapter<PromoAdapter.MyViewHolder>() {

    private var list: ArrayList<Promo> = arrayListOf()
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        context = parent.context


        val mInflater = LayoutInflater.from(context)
        val view = mInflater.inflate(R.layout.list_promo, parent, false)

        return MyViewHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.title.text = list[position].code
        holder.amount.text = (list[position].amount.toString() + "%")
        holder.limit.text = "LIMIT (${list[position].currentLimit}/${list[position].limit})"

        holder.date.text = "Create : " + DateFormat.format("MMMM dd yyyy",  list[position].createAt)
        holder.datex.text = "Expire  : "  +  DateFormat.format("MMMM dd yyyy",  list[position].expireAt)


        holder.layout.setOnClickListener {


            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            val newFragment = DialogAddPromo()
            newFragment.isCancelable = true

            val bundle = Bundle()
            bundle.putString("id", list[position].id)
            bundle.putString("code", list[position].code)
            bundle.putInt("percent", list[position].amount)
            bundle.putBoolean("active", list[position].active)
            bundle.putLong("create", list[position].createAt)
            bundle.putLong("expire", list[position].expireAt)
            bundle.putInt("limit", list[position].limit)
            bundle.putInt("currentLimit", list[position].currentLimit)

            newFragment.arguments = bundle


            val transaction = fragmentManager?.beginTransaction()

            if(transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

            }

        }

    }


    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var title: TextView = itemView.findViewById(R.id.title) as TextView
        internal var date: TextView = itemView.findViewById(R.id.txt_date) as TextView
        internal var amount: TextView = itemView.findViewById(R.id.amount) as TextView
        internal var datex: TextView = itemView.findViewById(R.id.txt_amount) as TextView
        internal var limit: TextView = itemView.findViewById(R.id.limit) as TextView
        internal var layout: ConstraintLayout = itemView.findViewById(R.id.layout_list) as ConstraintLayout


    }

    fun swapAdapter(list: ArrayList<Promo>) {
        this.list = list
    }

}