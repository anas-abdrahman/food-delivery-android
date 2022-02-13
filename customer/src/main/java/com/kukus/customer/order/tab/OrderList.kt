package com.kukus.customer.order.tab

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.androidhuman.rxfirebase2.database.dataChanges
import com.cipolat.superstateview.SuperStateView
import com.kukus.customer.R
import com.kukus.library.Constant.Companion.EXTRA_STATUS
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.FirebaseUtils.Companion.getShipRef
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Ship
import org.jetbrains.anko.find


class OrderList : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var empty: SuperStateView
    private lateinit var progress: LinearLayout
    private lateinit var textList: TextView

    var statusOrder = STATUS.WAITING
    var menuAdapter = OrderAdapter()

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

        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.find(R.id.recyclerView)
        progress = view.find(R.id.progress)
        empty = view.find(R.id.empty)
        textList = view.find(R.id.txt_info)

        initRecyclerView()
        getFirebase()

    }

    private fun initRecyclerView() {

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        recyclerView.adapter = menuAdapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setItemViewCacheSize(50)

    }

    private fun getFirebase() = getShipRef.dataChanges().subscribe { shipRef ->

        if (shipRef.exists()) {

            val listMenu = arrayListOf<Ship>()

            shipRef.children.forEach { date ->

                date.children.forEach { child ->

                    val ship = child.getValue(Ship::class.java)

                    if (ship != null) {

                        if (ship.user_id == getUserId) {

                            if (ship.status == statusOrder) {

                                listMenu.add(ship)

                            } else if (statusOrder == STATUS.WAITING) { // if order dispatched or delivery

                                if (ship.status == STATUS.PENDING || ship.status == STATUS.DISPATCHED) {

                                    listMenu.add(ship)

                                }
                            }
                        }
                    }
                }
            }

            if (listMenu.size > 0) {

                menuAdapter.swapAdapter(listMenu)
                menuAdapter.notifyDataSetChanged()

                setViewVisible(true)

            } else {

                setViewVisible(false)

            }

            textList.text = StringBuilder("Total List (" + listMenu.size.toString() + ")")

        } else {

            setViewVisible(false)

        }

    }

    private fun setViewVisible(isVisible: Boolean) {

        Handler().postDelayed({

            if (!isVisible) {

                progress.visibility = View.GONE
                recyclerView.visibility = View.GONE
                empty.visibility = View.VISIBLE

            } else {

                progress.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                empty.visibility = View.GONE

            }

        }, 1500)

    }
}
