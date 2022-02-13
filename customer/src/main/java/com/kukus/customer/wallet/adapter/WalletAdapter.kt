package com.kukus.customer.wallet.adapter

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
import com.kukus.library.model.Wallet
import java.lang.StringBuilder
import java.util.*

/**
 * Created by anas on 28/01/2018.
 */

class WalletAdapter : RecyclerView.Adapter<WalletAdapter.MyViewHolder>() {

    private var list: ArrayList<Wallet> = arrayListOf()
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        context = parent.context


        val mInflater = LayoutInflater.from(context)
        val view = mInflater.inflate(R.layout.list_wallet, parent, false)

        return MyViewHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.date.text = DateFormat.format("dd MMMM yyyy - HH:mm:ss a",  list[position].date)

        if(list[position].inOut == "in"){
            holder.amount.setTextColor(Color.parseColor("#32c360"))
            holder.amount.text = StringBuilder("EGP " + list[position].amount.toString())

        }else{
            holder.amount.setTextColor(Color.RED)
            holder.amount.text = StringBuilder("- EGP " + list[position].amount.toString())
        }

        holder.title.text = StringBuilder(list[position].note)


    }


    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var title: TextView = itemView.findViewById(R.id.title) as TextView
        internal var amount: TextView = itemView.findViewById(R.id.amount) as TextView
        internal var date: TextView = itemView.findViewById(R.id.date) as TextView


    }

    fun swapAdapter(list: ArrayList<Wallet>) {
        this.list = list
    }

}