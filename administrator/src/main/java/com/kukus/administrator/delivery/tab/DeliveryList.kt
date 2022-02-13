package com.kukus.administrator.delivery.tab

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.androidhuman.rxfirebase2.database.*
import com.cipolat.superstateview.SuperStateView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kukus.administrator.R
import com.kukus.administrator.delivery.drag.OnCustomerListChangedListener
import com.kukus.administrator.delivery.drag.OnStartDragListener
import com.kukus.administrator.delivery.drag.ItemTouchHelperCallback
import com.kukus.library.Constant.Companion.EXTRA_STATUS
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.Constant.Companion.TRIP
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Ship
import org.jetbrains.anko.find
import java.lang.StringBuilder
import kotlin.collections.ArrayList

class DeliveryList : Fragment(), OnCustomerListChangedListener, OnStartDragListener {

    var statusDelivery = TRIP.NEW
    var date = getDate
    var list = arrayListOf<Ship>()

    lateinit var adapter: DeliveryAdapter
    lateinit var mItemTouchHelper: ItemTouchHelper
    lateinit var mSharedPreferences: SharedPreferences
    lateinit var recyclerView: RecyclerView
    lateinit var empty: SuperStateView
    lateinit var txt_info: TextView
    lateinit var progress: LinearLayout
    var isOnline = false

    companion object {

        fun newInstance(status: TRIP): DeliveryList {

            val bundle = Bundle()
            val fragment = DeliveryList()

            bundle.putSerializable(EXTRA_STATUS, status)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        statusDelivery = arguments?.get(EXTRA_STATUS) as TRIP

        val view = inflater.inflate(R.layout.fragment_delivery, container, false)

        recyclerView = view.find(R.id.recyclerView)
        empty = view.find(R.id.empty)
        txt_info = view.find(R.id.txt_info)
        progress = view.find(R.id.progress)

        mSharedPreferences = context!!.getSharedPreferences("$statusDelivery-preference-file", Context.MODE_PRIVATE)

        setupRecyclerView()
        setupAdapterList()



        return view
    }

    @SuppressLint("CheckResult")
    fun setupAdapterList(date: String = getDate) {

        this.date = date

        getShip(this.date).dataChanges().subscribe {

            if (it.exists()) {

                list.clear()

                it.children.forEach { data ->

                    val ship = data.getValue(Ship::class.java)

                    if (ship != null) {

                        if (statusDelivery == TRIP.ACTIVE && ship.status == STATUS.PENDING && ship.deliveryId == "" && isOnline) {

                            list.add(ship)

                        } else if (statusDelivery == TRIP.UPCOMING && (ship.status == STATUS.PENDING || ship.status == STATUS.DISPATCHED) && ship.deliveryId == getUserId) {

                            list.add(ship)

                        } else if (statusDelivery == TRIP.PAST && ship.status == STATUS.COMPLETE && ship.deliveryId == getUserId) {

                            list.add(ship)
                        }
                    }
                }

                if (list.size > 0) {

                    list = getSampleData(list)
                    adapter.swapAdapter(list)
                    adapter.notifyDataSetChanged()

                    recyclerView.adapter = adapter
                    recyclerView.visibility = View.VISIBLE
                    empty.visibility = View.GONE

                } else {

                    recyclerView.visibility = View.GONE
                    empty.visibility = View.VISIBLE

                }

                txt_info.text = StringBuilder("$date (" + list.size.toString() + ")")

            } else {

                txt_info.text = StringBuilder("$date (0)")
                recyclerView.visibility = View.GONE
                empty.visibility = View.VISIBLE

            }

            progress.visibility = View.GONE

        }

    }


    override fun onNoteListChanged(ships: ArrayList<Ship>) {

        val listOfSortedCustomerId = ArrayList<String>()

        for (ship in ships) {
            listOfSortedCustomerId.add(ship.id)
        }

        val gson = Gson()
        val jsonListOfSortedCustomerIds = gson.toJson(listOfSortedCustomerId)

        //save to SharedPreference
        mSharedPreferences.edit().putString("$statusDelivery-list-$date", jsonListOfSortedCustomerIds).apply()
        mSharedPreferences.edit().apply()

    }

    private fun getSampleData(list: ArrayList<Ship>): ArrayList<Ship> {

        val customerList = list
        val sortedCustomers = ArrayList<Ship>()
        val jsonListOfSortedCustomerId = mSharedPreferences.getString("$statusDelivery-list-$date", "")

        if (jsonListOfSortedCustomerId!!.isNotEmpty()) {

            val gson = Gson()
            val listOfSortedCustomersId = gson.fromJson<List<String>>(jsonListOfSortedCustomerId, object : TypeToken<List<String>>() {}.type)

            if (listOfSortedCustomersId != null && listOfSortedCustomersId.isNotEmpty()) {
                for (id in listOfSortedCustomersId) {
                    for (customer in customerList) {
                        if (customer.id == id) {
                            sortedCustomers.add(customer)
                            customerList.remove(customer)
                            break
                        }
                    }
                }
            }

            if (customerList.size > 0) {
                sortedCustomers.addAll(customerList)
            }

            return sortedCustomers

        } else {
            return customerList
        }
    }


    private fun setupRecyclerView() {

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager

        adapter = DeliveryAdapter(list, this, this)
        mItemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
        mItemTouchHelper.attachToRecyclerView(recyclerView)

    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        mItemTouchHelper.startDrag(viewHolder)
    }


}
