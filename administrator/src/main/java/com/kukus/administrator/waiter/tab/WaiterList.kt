package com.kukus.administrator.waiter.tab

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.cipolat.superstateview.SuperStateView
import com.kukus.administrator.R
import com.kukus.library.Constant.Companion.EXTRA_STATUS
import com.kukus.library.FirebaseUtils.Companion.getMenuRef
import com.kukus.library.model.Menu
import org.jetbrains.anko.find
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.androidhuman.rxfirebase2.database.data
import com.kukus.library.Constant.Companion.TYPE_ORDER
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.model.Order

class WaiterList : Fragment() {

    private lateinit var statusMenu: TYPE_ORDER

    lateinit var menuFoodAdapter: BaseAdapterMenu
    lateinit var recyclerView: RecyclerView
    lateinit var progress: LinearLayout
    lateinit var empty: SuperStateView
    private var isEdit = false
    private var orderId = ""

    companion object {

        fun newInstance(status: TYPE_ORDER, orderId: String, isEdit: Boolean): WaiterList {

            val bundle = Bundle()
            val fragment = WaiterList()

            bundle.putSerializable(EXTRA_STATUS, status)
            bundle.putSerializable("orderId", orderId)
            bundle.putSerializable("isEdit", isEdit)

            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        statusMenu = arguments?.getSerializable(EXTRA_STATUS) as TYPE_ORDER
        orderId = arguments?.getString("orderId", "") ?: ""
        isEdit = arguments?.getBoolean("isEdit", false) ?: false

        return inflater.inflate(R.layout.fragment_menu_waiter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        empty = view.find(R.id.empty)
        progress = view.find(R.id.progress)
        recyclerView = view.find(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(30)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        menuFoodAdapter = BaseAdapterMenu(statusMenu)
        recyclerView.adapter = menuFoodAdapter

        setAdapterMenu()
        super.onViewCreated(view, savedInstanceState)

    }

    @SuppressLint("CheckResult") private fun setAdapterMenu() {

        getMenuRef.data().subscribe { it, _ ->

            var isShowList = false

            if (it.exists()) {

                val listMenu = arrayListOf<Menu>()

                it.children.forEach { data ->

                    val menu = data.getValue(Menu::class.java)

                    if (menu != null) {

                        if (menu.type == statusMenu) {

                            listMenu.add(menu)
                        }
                    }
                }

                if (listMenu.size > 0) {

                   menuFoodAdapter.swapAdapter(listMenu)

                    if (isEdit) {
                        getEditAdapter()
                    }

                    isShowList = true

                }
            }

            Handler().postDelayed({

                if (isShowList) {

                    recyclerView.visibility = View.VISIBLE
                    empty.visibility = View.GONE

                } else {

                    recyclerView.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                }

                progress.visibility = View.GONE

            }, 500)

        }
    }

    @SuppressLint("CheckResult") fun getEditAdapter() {

        getWaiter(getDate).child(orderId).child("menu").data().subscribe { orderRef, _ ->

            if (orderRef.exists()) {

                val listMenu = arrayListOf<Order>()

                orderRef.children.forEach { data ->

                    val order = data.getValue(Order::class.java)

                    if (order != null && statusMenu.name == order.type) {

                        listMenu.add(order)

                    }
                }

                if (listMenu.size > 0) {

                    when (statusMenu) {

                        TYPE_ORDER.FOOD, TYPE_ORDER.EXTRA -> {

                            getMenuList(listMenu)

                        }

                        TYPE_ORDER.DRINK -> {

                            getDrinkList(listMenu)

                        }
                    }

                    menuFoodAdapter.notifyDataSetChanged()

                }
            }
        }
    }

    private fun getMenuList(listMenu: ArrayList<Order>) {

        for (i in 0 until menuFoodAdapter.list.size) {

            val menu = menuFoodAdapter.list[i]

            var countOrder = 1

            listMenu.forEach { order ->

                if (order.menu_id == menu.id) {

                    menuFoodAdapter.updateOrder(i, order, countOrder)

                    countOrder++

                }
            }
        }
    }

    private fun getDrinkList(listMenu: ArrayList<Order>) {

        for (i in 0 until menuFoodAdapter.list.size) {

            val menu = menuFoodAdapter.list[i]

            var countOrder1 = 1
            var countOrder2 = 1

            listMenu.forEach { order ->

                if (order.menu_id == menu.id + menu.label_price1 && menu.label_price1 != "") {

                    menuFoodAdapter.updateOrder(i, order, countOrder1,menu.label_price1)

                    countOrder1++

                } else if (order.menu_id == menu.id + menu.label_price2 && menu.label_price2 != "") {

                    menuFoodAdapter.updateOrder(i, order, countOrder2, menu.label_price2)

                    countOrder2++
                }
            }
        }
    }
}
