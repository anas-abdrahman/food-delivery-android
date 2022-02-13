package com.kukus.administrator.staff.tab

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.androidhuman.rxfirebase2.database.rxUpdateChildren
import com.kukus.administrator.R
import com.kukus.administrator.dialog.DialogPayWallet
import com.kukus.administrator.staff.ActivityEditStaff
import com.kukus.administrator.staff.ActivityStaff
import com.kukus.administrator.user.ActivityUser
import com.kukus.library.FirebaseUtils
import com.kukus.library.model.Staff
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

class StaffAdapter : RecyclerView.Adapter<StaffAdapter.RecyclerHolder>() {

    private var list: ArrayList<Staff> = arrayListOf()
    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        context = parent.context
        return RecyclerHolder.buildFor(parent)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {

        if(list[position].login_at != 0.toLong()){
            holder.txt_last_login.text = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", list[position].login_at)
        }

        holder.txt_user_name.text = list[position].name
        holder.txt_user_email.text = list[position].email

        if(list[position].block){
            holder.txt_user_block.text = ("BLOCK")
            holder.txt_user_block.setTextColor(Color.parseColor("#ef554a"))
        }else{
            holder.txt_user_block.text = ("ACTIVE")
            holder.txt_user_block.setTextColor(Color.parseColor("#32c360"))
        }


        holder.txt_user_detail.setOnClickListener {

            val menus = arrayOf("Edit", "Block", "Pay salary")

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Staff")
            builder.setItems(menus) { _, which ->
                when (which)
                {
                    0 -> {

                        val intent = Intent(context, ActivityEditStaff::class.java)
                        intent.putExtra("title", list[position].name)
                        intent.putExtra("email", list[position].email)
                        intent.putExtra("tel", list[position].mobile)
                        intent.putExtra("pass", list[position].pass)
                        context.startActivity(intent)

                    }
                    1 -> {

                        val user = FirebaseUtils.getStaff(list[position].id)
                        val update = mapOf("block" to !list[position].block)

                        user.rxUpdateChildren(update).subscribe {

                            Handler().postDelayed({

                                context.toast(if(!list[position].block) "block" else "active")

                            }, 500)
                        }

                    }
                    2 -> {

                        val fragmentManager = (context as FragmentActivity).supportFragmentManager
                        val newFragment = DialogPayWallet()
                        val bundle = Bundle()

                        bundle.putString("staffID", list[position].id)

                        newFragment.arguments = bundle

                        val transaction = fragmentManager?.beginTransaction()

                        if (transaction != null) {
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()
                        }
                    }
                }
            }

            builder.show()

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun swapAdapter(list: ArrayList<Staff>) {
        this.list = list
    }

    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txt_last_login   = itemView.find<TextView>(R.id.txt_order_id)
        val txt_user_name = itemView.find<TextView>(R.id.txt_table)
        val txt_user_email = itemView.find<TextView>(R.id.txt_date)
        val txt_user_detail  = itemView.find<TextView>(R.id.txt_user_detail)
        val txt_user_block  = itemView.find<TextView>(R.id.txt_block)

        companion object {
            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_staff, viewGroup, false))
            }
        }
    }
}
