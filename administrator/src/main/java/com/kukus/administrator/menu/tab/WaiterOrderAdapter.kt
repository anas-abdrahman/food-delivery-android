package com.kukus.administrator.menu.tab

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.kukus.administrator.R
import com.kukus.library.model.Order

class WaiterOrderAdapter(var list: List<Order>) : RecyclerView.Adapter<WaiterOrderAdapter.RecyclerHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        context = parent.context
        return RecyclerHolder.buildFor(parent)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {

        holder.menu_name.text = list[position].title

        when {
            list[position].note == "sambal rempah" -> holder.radio_chili_rempah.isChecked = true
            list[position].note == "sambal kukus" -> holder.radio_chili_kukus.isChecked = true
            else -> holder.radio_radio_empty.isChecked = true
        }

        holder.radioGroup.setOnCheckedChangeListener { compoundButton, checkedId ->

            when (checkedId) {
                R.id.radio_chili_kukus -> list[position].note = "sambal kukus"
                R.id.radio_chili_rempah -> list[position].note = "sambal rempah"
                else -> list[position].note = ""
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var menu_name: TextView = itemView.findViewById(R.id.txt_menu)
        var radioGroup: RadioGroup = itemView.findViewById(R.id.radio_group_chili)
        var radio_chili_rempah: RadioButton = itemView.findViewById(R.id.radio_chili_rempah)
        var radio_chili_kukus: RadioButton = itemView.findViewById(R.id.radio_chili_kukus)
        var radio_radio_empty: RadioButton = itemView.findViewById(R.id.radio_empty)


        companion object {

            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_order_waiter, viewGroup, false))
            }
        }
    }
}
