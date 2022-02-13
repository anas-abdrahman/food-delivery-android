package com.kukus.administrator.history

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kukus.administrator.R
import com.kukus.library.model.History
import com.kukus.library.model.Statistic

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.RecyclerHolder>() {

    private var list: ArrayList<History> = arrayListOf()
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        context = parent.context
        return RecyclerHolder.buildFor(parent)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {

        val history = getItem(position)

        holder.txt_trip.text = history.trip.toString()
        holder.txt_total.text = ("LE " + history.total.toString())
        holder.date.text = history.date

    }

    private fun getItem(position: Int): History {
        return list[position]
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun swapAdapter(list: ArrayList<History>) {
        this.list = list
        notifyDataSetChanged()
    }

    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txt_trip: TextView = itemView.findViewById(R.id.txt_trip)
        var txt_total: TextView = itemView.findViewById(R.id.txt_total)
        var date: TextView = itemView.findViewById(R.id.date)

        companion object {

            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_history, viewGroup, false))
            }
        }
    }
}
