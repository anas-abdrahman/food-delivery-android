package com.kukus.administrator.waiter.tab

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.kukus.administrator.R
import com.kukus.library.model.Order


class ListOrderAdapter(context: Context, val list: ArrayList<Order>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.list_order_count_waiter1, parent, false)

        val titleTextView = rowView.findViewById(R.id.title) as TextView
        val countTextView = rowView.findViewById(R.id.count1) as TextView
        val priceTextView = rowView.findViewById(R.id.price) as TextView
        val notedTextView = rowView.findViewById(R.id.note) as TextView

        val order = getItem(position)

        countTextView.text = ("${order.quantity} x")
        titleTextView.text = (order.title.toUpperCase())
        priceTextView.text = ("LE ${(order.price1 * order.quantity)}")

        if (order.note != "")
        {
            notedTextView.visibility = View.VISIBLE
            notedTextView.text = (order.note)
        }

        return rowView
    }

    override fun getItem(position: Int): Order {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}