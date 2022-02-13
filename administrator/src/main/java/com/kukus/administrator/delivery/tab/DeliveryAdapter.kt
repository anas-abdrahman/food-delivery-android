package com.kukus.administrator.delivery.tab

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.androidhuman.rxfirebase2.database.rxUpdateChildren
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kukus.administrator.R
import com.kukus.administrator.delivery.ActivityTracking
import com.kukus.administrator.delivery.drag.ItemTouchHelperAdapter
import com.kukus.administrator.delivery.drag.ItemTouchHelperViewHolder
import com.kukus.administrator.delivery.drag.OnCustomerListChangedListener
import com.kukus.administrator.delivery.ActivityOrderMap
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.Constant.Companion.getMapStatic
import com.kukus.library.Constant.Companion.isValidContextForGlide
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Ship
import com.kukus.library.model.User
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList

class DeliveryAdapter(var list: ArrayList<Ship>, var mactivity: DeliveryList, var listChangedListener: OnCustomerListChangedListener) : RecyclerView.Adapter<DeliveryAdapter.RecyclerHolder>(), ItemTouchHelperAdapter {

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {

        getUser(list[position].user_id).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {

                val user = data.getValue(User::class.java)
                if (user != null) holder.txt_name.text = user.name

            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        if (isValidContextForGlide(context)) {

            Glide.with(context).load(getMapStatic(LatLng(list[position].address.lat.toDouble(), list[position].address.lng.toDouble()))).into(holder.img_map)

            holder.img_map.setOnClickListener {

                val intent = Intent(context, ActivityOrderMap::class.java)

                intent.putExtra("lat", list[position].address.lat)
                intent.putExtra("lng", list[position].address.lng)

                context.startActivity(intent)
            }

        }

        holder.txt_address.text = list[position].address.street

        if (list[position].status == STATUS.DISPATCHED) {
            holder.txt_date.text = "DELIVERY NOW "
            holder.txt_date.setTextColor(Color.GREEN)
        } else {
            holder.txt_date.text = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", list[position].timestamp)
            holder.txt_date.setTextColor(Color.BLACK)
        }

        if (list[position].status == STATUS.PENDING && list[position].deliveryId == "") {

            holder.txt_accept.visibility = View.VISIBLE
            holder.txt_detail.visibility = View.INVISIBLE
            holder.txt_complate.visibility = View.INVISIBLE

            holder.iv_delete.visibility = View.GONE


        } else if (list[position].status == STATUS.PENDING && list[position].deliveryId != "") {

            holder.txt_accept.visibility = View.INVISIBLE
            holder.txt_detail.visibility = View.VISIBLE
            holder.txt_complate.visibility = View.INVISIBLE

            holder.iv_delete.visibility = View.VISIBLE


        } else if (list[position].status == STATUS.COMPLETE) {

            holder.txt_accept.visibility = View.INVISIBLE
            holder.txt_detail.visibility = View.INVISIBLE
            holder.txt_complate.visibility = View.VISIBLE

            holder.iv_delete.visibility = View.GONE

        }

        holder.txt_order_id.text = list[position].ship_id

        holder.txt_accept.setOnClickListener {

            getShip(mactivity.date).child(list[position].id).child("deliveryId").setValue(getUserId).addOnCanceledListener {

                holder.txt_accept.setBackgroundResource(R.drawable.ic_check_green_24dp)

            }
        }

        holder.txt_detail.setOnClickListener {

            val intent = Intent(context, ActivityTracking::class.java)
            intent.putExtra("order", list[position].id)
            intent.putExtra("date", list[position].date)
            intent.putExtra("lat", list[position].address.lat)
            intent.putExtra("lng", list[position].address.lng)
            intent.putExtra("userId", list[position].user_id)
            intent.putExtra("status", list[position].status)

            context.startActivity(intent)

        }

        holder.iv_delete.setOnClickListener {

            val ships = getShip(list[position].date).child(list[position].id)

            val update = mapOf("deliveryId" to "")

            ships.rxUpdateChildren(update).subscribe {
                context.toast("removed!")
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun swapAdapter(list: ArrayList<Ship>) {
        this.list = list
    }

    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.parseColor("#DDDDDD"))
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }

        var txt_name = itemView.findViewById<TextView>(R.id.txt_table)
        var txt_order_id = itemView.findViewById<TextView>(R.id.txt_order_id)
        var txt_date = itemView.findViewById<TextView>(R.id.txt_order_time)
        var txt_detail = itemView.findViewById<TextView>(R.id.txt_order_counter)
        var txt_accept = itemView.findViewById<TextView>(R.id.txt_order_accept)
        var txt_complate = itemView.findViewById<TextView>(R.id.txt_order_complate)
        var txt_address = itemView.findViewById<TextView>(R.id.txt_date)
        var iv_delete = itemView.findViewById<ImageView>(R.id.iv_delete)
        var list_item = itemView.findViewById<ConstraintLayout>(R.id.list_item)
        var img_map = itemView.findViewById<ImageView>(R.id.img_map)

        companion object {

            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_delivery, viewGroup, false))
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(list, fromPosition, toPosition)
        listChangedListener.onNoteListChanged(list)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {}

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        context = parent.context
        return RecyclerHolder.buildFor(parent)
    }

}
