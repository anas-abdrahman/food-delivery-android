package com.kukus.administrator.user

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kukus.administrator.R
import com.kukus.library.model.User
import java.lang.StringBuilder
import java.util.*

/**
 * Created by anas on 28/01/2018.
 */

class UserAdapter : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    private var list: ArrayList<User> = arrayListOf()
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        context = parent.context

        val mInflater = LayoutInflater.from(context)
        val view = mInflater.inflate(R.layout.list_contact, parent, false)

        return MyViewHolder(view)
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.name.text = StringBuilder(list[position].name.toUpperCase())
        holder.email.text = StringBuilder(list[position].email.toUpperCase())
        holder.date.text = "last Login" + DateFormat.format("dd-MM-yyyy (HH:mm:ss)", list[position].login_at)


    }


    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var name: TextView = itemView.findViewById(R.id.name) as TextView
        internal var email: TextView = itemView.findViewById(R.id.email) as TextView
        internal var date: TextView = itemView.findViewById(R.id.txt_date) as TextView

    }

    fun swapAdapter(list: ArrayList<User>) {
        this.list = list
    }

}