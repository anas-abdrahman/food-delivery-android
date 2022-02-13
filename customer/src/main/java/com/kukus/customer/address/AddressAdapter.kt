package com.kukus.customer.address


import android.content.Context
import android.content.Intent
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kukus.customer.R
import com.kukus.customer.checkout.ActivityCheckout
import com.kukus.customer.location.ActivityLocation
import com.kukus.customer.profile.ActivityProfile
import com.kukus.library.Constant.Companion.RESULT_MAP
import com.kukus.library.model.Address
import org.jetbrains.anko.find

class AddressAdapter(var models: List<Address>, private val context: Context, private val positionSelect: Int) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null

    override fun getCount(): Int {
        return models.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        layoutInflater = LayoutInflater.from(context)

        val view = layoutInflater!!.inflate(R.layout.list_address, container, false)

        val address = view.find<TextView>(R.id.txt_address)
        val select = view.find<TextView>(R.id.select)
        val layout_empty = view.find<CardView>(R.id.layout_empty)
        val layout_location = view.find<CardView>(R.id.layout_location)
        val delivery = view.find<TextView>(R.id.delivery)
        val remove = view.find<ImageView>(R.id.remove)

        if (positionSelect == position) {
            select.visibility = View.VISIBLE
        } else {
            select.visibility = View.INVISIBLE
        }

        if (models[position].user_id == "") {

            layout_empty.visibility = View.VISIBLE
            layout_location.visibility = View.GONE
            remove.visibility = View.GONE

            layout_empty.setOnClickListener {

                val intent = Intent(context, ActivityLocation::class.java)

                if (context is ActivityCheckout) {
                    (context).startActivityForResult(intent, RESULT_MAP)
                }

                if (context is ActivityProfile) {
                    (context).startActivityForResult(intent, RESULT_MAP)
                }
            }

        } else {

            layout_empty.visibility = View.GONE
            layout_location.visibility = View.VISIBLE

            val mAddress = StringBuilder()

            mAddress.append("No building ${models[position].building}, ")
            mAddress.append("Floor ${models[position].floor}, ")
            mAddress.append("Apartments ${models[position].apartment}, ")
            mAddress.append(models[position].street)

            address.text = mAddress
            delivery.text = ("Delivery : LE ${models[position].delivery}")

            layout_location.setOnClickListener {

                if (context is ActivityCheckout) {
                    (context).selectAddress(position)
                }
                if (context is ActivityProfile) {
                    (context).selectAddress(position)
                }

            }

            remove.setOnClickListener {

                AlertDialog.Builder(context)
                        .setTitle("Delete?")
                        .setMessage("Are you sure you want to remove?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes) { _, _ ->

                            if (context is ActivityCheckout) {
                                (context).removeAddress(position)
                            }

                            if (context is ActivityProfile) {
                                (context).removeAddress(position)
                            }
                        }
                        .create()
                        .show()

            }

        }

        container.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

}