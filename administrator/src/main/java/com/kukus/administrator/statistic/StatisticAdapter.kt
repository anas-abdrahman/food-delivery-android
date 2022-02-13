package com.kukus.administrator.statistic

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kukus.administrator.R
import com.kukus.library.model.Statistic
import com.kukus.library.model.StatisticNew

class StatisticAdapter : RecyclerView.Adapter<StatisticAdapter.RecyclerHolder>() {

    private var list: ArrayList<StatisticNew> = arrayListOf()
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        context = parent.context
        return RecyclerHolder.buildFor(parent)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {

        val statistic = getItem(position)

        holder.txt_date.text = statistic.date

        holder.txt_delivery.text = statistic.delivery.toString()
        holder.txt_makanan.text = statistic.makanan.toString()
        holder.txt_minuman.text = statistic.minuman.toString()
        holder.txt_total.text = statistic.total.toString()

    }

    private fun getItem(position: Int): StatisticNew {
        return list[position]
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun swapAdapter(list: ArrayList<StatisticNew>) {
        this.list = list
        notifyDataSetChanged()
    }

    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txt_date: TextView = itemView.findViewById(R.id.date)
        var txt_delivery: TextView = itemView.findViewById(R.id.txt_delivery)
        var txt_makanan: TextView = itemView.findViewById(R.id.txt_makanan)
        var txt_minuman: TextView = itemView.findViewById(R.id.txt_minuman)
        var txt_total: TextView = itemView.findViewById(R.id.txt_total)

        companion object {

            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_statistic_baru, viewGroup, false))
            }
        }
    }
}
