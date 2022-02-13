package com.kukus.administrator.order.tab

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.androidhuman.rxfirebase2.database.*
import com.cipolat.superstateview.SuperStateView
import com.kukus.administrator.R
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.model.Ship
import com.kukus.library.Constant.Companion.EXTRA_STATUS
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getDateConvert
import com.kukus.library.FirebaseUtils.Companion.getShip
import org.jetbrains.anko.find
import java.lang.StringBuilder

class OrderList : Fragment() {

    private var statusOrder: STATUS = STATUS.WAITING

    lateinit var textList: TextView
    lateinit var empty: SuperStateView
    lateinit var progress: LinearLayout
    lateinit var orderAdapter: OrderAdapter
    lateinit var recyclerView: RecyclerView

    companion object {

        fun newInstance(status: STATUS): OrderList {

            val bundle = Bundle()
            val fragment = OrderList()

            bundle.putSerializable(EXTRA_STATUS, status)

            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        statusOrder = arguments?.get(EXTRA_STATUS) as STATUS

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val view = inflater.inflate(R.layout.fragment_order, container, false)

        orderAdapter = OrderAdapter()
        recyclerView = view.find(R.id.recyclerView)
        progress = view.find(R.id.progress)
        textList = view.find(R.id.txt_info)
        empty = view.find(R.id.empty)


        recyclerView.adapter = orderAdapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setItemViewCacheSize(30)

        setDataList()

        return view
    }

    @SuppressLint("CheckResult")
    fun setDataList(date: String = getDate) {

        val ref = getShip(date)

        ref.dataChanges().subscribe {

            if (it.exists()) {

                val listOrders = arrayListOf<Ship>()

                it.children.forEach { data ->

                    val ship = data.getValue(Ship::class.java)

                    if (ship != null) {

                        if (ship.status == statusOrder
                                || (statusOrder == STATUS.PENDING && ship.status == STATUS.PREPARING)
                                || (statusOrder == STATUS.PENDING && ship.status == STATUS.DISPATCHED)
                        ) {

                            listOrders.add( ship )

                        }
                    }
                }

                isVisible = if (listOrders.size > 0) {

                    orderAdapter.swapAdapter(listOrders)

                    true

                } else {

                    false

                }

                textList.text = StringBuilder("${getDateConvert(date)} (" + listOrders.size.toString() + ")")

            } else {

                textList.text = StringBuilder("${getDateConvert(date)} (0)")

                isVisible = false

            }

            progress.visibility = View.GONE

        }

    }

    fun setVisible(isVisible : Boolean){

        if(isVisible){
            recyclerView.visibility = View.VISIBLE
            empty.visibility = View.GONE
        }else{
            recyclerView.visibility = View.GONE
            empty.visibility = View.VISIBLE
        }
    }

}

