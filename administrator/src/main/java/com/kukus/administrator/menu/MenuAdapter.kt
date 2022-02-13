package com.kukus.administrator.menu

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.collections.ArrayList
import android.widget.*
import com.kukus.administrator.R
import com.kukus.library.Constant
import com.kukus.library.model.Menu


class MenuAdapter(val context : Context, val list : ArrayList<Menu>) : RecyclerView.Adapter<MenuAdapter.RecyclerHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        return RecyclerHolder.buildFor(parent)
    }

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {

        holder.edit.setOnClickListener {

            val action = arrayOf(Constant.UPDATE, Constant.DELETE)


            val dialogBuild = AlertDialog.Builder(context)

            dialogBuild.setTitle("Select the Action")

            dialogBuild.setItems(action) { _, which ->

                when(which) {

                    0 -> { // update
                        Toast.makeText(context, "update", Toast.LENGTH_SHORT).show()
                    }

                    1 -> { // delete
                        Toast.makeText(context, "deleate", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val dialog = dialogBuild.create()
            dialog.show()

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class RecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var name           : TextView = itemView.findViewById(R.id.text_title)
        internal var description    : TextView = itemView.findViewById(R.id.text_description)
        internal var price          : TextView = itemView.findViewById(R.id.text_price)
        internal var image          : ImageView = itemView.findViewById(R.id.image)
        internal var edit           : ImageButton = itemView.findViewById(R.id.btn_edit)

        companion object {

            private const val LAYOUT = R.layout.list_menu

            fun buildFor(viewGroup: ViewGroup): RecyclerHolder {
                return RecyclerHolder(LayoutInflater.from(viewGroup.context).inflate(LAYOUT, viewGroup, false))
            }
        }
    }

}
