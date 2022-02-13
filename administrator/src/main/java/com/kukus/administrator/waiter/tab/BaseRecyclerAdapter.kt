package com.kukus.administrator.waiter.tab

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kukus.administrator.R
import com.kukus.library.Constant
import org.jetbrains.anko.find


class BaseRecyclerAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun getView() : View{
        return itemView
    }

    companion object {

        fun buildFor(viewGroup: ViewGroup, statusOrder : Constant.Companion.TYPE_ORDER): BaseRecyclerAdapter {

            when(statusOrder){
                Constant.Companion.TYPE_ORDER.FOOD , Constant.Companion.TYPE_ORDER.EXTRA->{

                    val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_menu_food_resturant, viewGroup, false)

                    return BaseRecyclerAdapter(itemView)
                }
                Constant.Companion.TYPE_ORDER.DRINK->{
                    val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_menu_drink_resturant, viewGroup, false)

                    return BaseRecyclerAdapter(itemView)

                }
                else->{
                    val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_menu_food_resturant, viewGroup, false)
                    return BaseRecyclerAdapter(itemView)
                }
            }
        }
    }
}
