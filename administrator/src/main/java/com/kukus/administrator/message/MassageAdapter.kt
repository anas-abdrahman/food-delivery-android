package com.kukus.administrator.message

import android.annotation.SuppressLint
import android.app.FragmentTransaction
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kukus.administrator.R
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getContact
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Message
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by anas on 28/01/2018.
 */

class MassageAdapter : RecyclerView.Adapter<MassageAdapter.MyViewHolder>() {

    private var list: ArrayList<Message> = arrayListOf()
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        context = parent.context

        val mInflater = LayoutInflater.from(context)
        val view = mInflater.inflate(R.layout.list_message, parent, false)



        return MyViewHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.title.text = list[position].title
        holder.date.text = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(list[position].time)
        holder.message.text = list[position].text


        if(list[position].user_id == "admin"){
            holder.read.setText("SEND")
            holder.read.setTextColor(Color.parseColor("#444444"))
        }else{
            holder.read.setText("INBOX")
            holder.read.setTextColor(Color.parseColor("#2E87E6"))
        }

        if(!list[position].read && list[position].user_to == "admin")
        {
            holder.read.setText("NEW")
            holder.read.setTextColor(Color.parseColor("#32c360"))
        }


        holder.layout.setOnClickListener {

            val fragmentManager = (context as ActivityMessage).supportFragmentManager
            val newFragment = DialogMessage()

            val bundle = Bundle()
            bundle.putString("title", list[position].title)
            bundle.putString("userId", list[position].user_id)
            bundle.putString("email", list[position].email)
            bundle.putString("message", list[position].text)
            bundle.putLong("date", list[position].time)
            bundle.putBoolean("show", true)

            newFragment.arguments = bundle

            val transaction = fragmentManager?.beginTransaction()

            if (transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

                if(!list[position].read && list[position].user_to == "admin"){
                    getContact(list[position].user_id).child(list[position].id).child("read").setValue(true)
                    holder.read.visibility = View.GONE
                }

            }

        }
    }


    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var title: TextView = itemView.findViewById(R.id.title) as TextView
        internal var message: TextView = itemView.findViewById(R.id.btn_message) as TextView
        internal var date: TextView = itemView.findViewById(R.id.txt_date) as TextView
        internal var read: TextView = itemView.findViewById(R.id.read) as TextView
        internal var layout: ConstraintLayout = itemView.findViewById(R.id.layout) as ConstraintLayout


    }

    fun swapAdapter(list: ArrayList<Message>) {
        this.list = list
    }

}