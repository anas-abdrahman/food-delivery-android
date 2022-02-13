package com.kukus.administrator.menu.tab

import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kukus.administrator.R
import com.kukus.administrator.menu.ActivityFormDrink
import com.kukus.administrator.menu.ActivityFormMenu
import com.kukus.library.model.Menu
import org.jetbrains.anko.find
import com.kukus.library.Constant
import com.kukus.library.Constant.Companion.TYPE_ORDER
import com.kukus.library.Constant.Companion.isValidContextForGlide


class MenuAdapter : RecyclerView.Adapter<MenuAdapter.RecyclerHolder>() {

    private var list: ArrayList<Menu> = arrayListOf()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        context = parent.context
        return RecyclerHolder.buildFor(parent)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {

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

        if(list[position].type == Constant.Companion.TYPE_ORDER.DRINK){

            if(list[position].price1 > 0){

                holder.divider_3.visibility = View.VISIBLE

                holder.label_price.visibility = View.VISIBLE
                holder.label_price.text = list[position].label_price1

                holder.text_price.visibility = View.VISIBLE
                holder.text_price.text = ("LE " + list[position].price1.toInt().toString())

            }else{

                holder.divider_3.visibility = View.GONE
                holder.label_price.visibility = View.GONE
                holder.text_price.visibility = View.GONE

            }

            if(list[position].price2 > 0){

                holder.divider_4.visibility = View.VISIBLE

                holder.label_price1.visibility = View.VISIBLE
                holder.label_price1.text = list[position].label_price2

                holder.text_price1.visibility = View.VISIBLE
                holder.text_price1.text = ("LE " + list[position].price2.toInt().toString())

            }else{

                holder.divider_4.visibility = View.GONE
                holder.label_price1.visibility = View.GONE
                holder.text_price1.visibility = View.GONE

            }

        }else{


            holder.divider_3.visibility = View.VISIBLE

            holder.label_price.visibility = View.VISIBLE
            holder.label_price.text = "PRICE"

            holder.text_price.visibility = View.VISIBLE
            holder.text_price.text = ("LE " + list[position].price1.toInt().toString())

            holder.divider_4.visibility = View.GONE
            holder.label_price1.visibility = View.GONE
            holder.text_price1.visibility = View.GONE


        }


        holder.labe_show.text = if (list[position].show){"Show" }else{ "Not Show"}
        holder.labe_show.setBackgroundResource(if (list[position].show){R.color.colorGreen}else{R.color.colorRed})

        holder.menu_name.text = list[position].name
        holder.name_description.text = list[position].description
        holder.text_availible.text = list[position].available.toString()
        holder.text_limit.text = list[position].limit.toString()


        holder.layout.setOnClickListener {

            val intent = if (list[position].type == TYPE_ORDER.DRINK) {

                Intent(context, ActivityFormDrink::class.java)

            } else {

                Intent(context, ActivityFormMenu::class.java)

            }

            intent.putExtra("id", list[position].id)
            intent.putExtra("available", list[position].available)
            intent.putExtra("show", list[position].show)
            intent.putExtra("type", list[position].type)
            intent.putExtra("image", list[position].image)
            intent.putExtra("title", list[position].name)
            intent.putExtra("description", list[position].description)
            intent.putExtra("label_price1", list[position].label_price1)
            intent.putExtra("label_price2", list[position].label_price2)
            intent.putExtra("price2", list[position].price1)
            intent.putExtra("price2", list[position].price2)
            intent.putExtra("limit", list[position].limit)
            intent.putExtra("currentLimit", list[position].currentLimit)

            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun swapAdapter(list: ArrayList<Menu>) {
        this.list = list
    }

    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.find<ImageView>(R.id.image)
        val labe_show = itemView.find<TextView>(R.id.upload_Image)
        //val labe_price = itemView.find<TextView>(R.id.text_price)
        val menu_name = itemView.find<TextView>(R.id.text_title)
        val name_description = itemView.find<TextView>(R.id.text_description)
        val text_limit = itemView.find<TextView>(R.id.text_limit)
        val text_availible = itemView.find<TextView>(R.id.text_available)
        val layout = itemView.find<ConstraintLayout>(R.id.layout)

        val progressImage = itemView.find<ProgressBar>(R.id.progress)

        val divider_3 = itemView.find<View>(R.id.divider_3)
        val divider_4 = itemView.find<View>(R.id.divider_4)

        val label_price = itemView.find<TextView>(R.id.label_price)
        val label_price1 = itemView.find<TextView>(R.id.label_price1)

        val text_price = itemView.find<TextView>(R.id.text_price)
        val text_price1 = itemView.find<TextView>(R.id.text_price1)

        companion object {


            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_menu, viewGroup, false))
            }
        }
    }
}
