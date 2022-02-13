package com.kukus.administrator.waiter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.androidhuman.rxfirebase2.database.data
import com.kukus.administrator.R
import com.kukus.administrator.printer.Printer
import com.kukus.administrator.waiter.tab.OrderAdapterCount
import com.kukus.administrator.waiter.tab.WaiterAdapterTab
import com.kukus.library.Constant.Companion.TABLE
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.Constant.Companion.TYPE_ORDER
import com.kukus.library.Constant.Companion.getOrderList
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getTableRef
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.FirebaseUtils.Companion.getWaiterRef
import com.kukus.library.interfaces.CountOrderListener
import com.kukus.library.model.Order
import com.kukus.library.model.Waiter
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.nav_sheet.*
import org.jetbrains.anko.toast

class ActivityAddOrder : AppCompatActivity(), CountOrderListener {

    private lateinit var mSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var table: Int = 0
    private var orderId: String = ""
    private var editTable: Boolean = false

    private lateinit var print: Printer


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_waiter)

        table = intent.extras.getInt("table", 0)
        orderId = intent.extras.getString("shipId", "")
        editTable = intent.extras.getBoolean("edit_table", false)

        initView()
        initTable()
        initOnClickListener()

        print = Printer(this)

    }

    @SuppressLint("CheckResult")
    private fun initTable() {

        if (editTable) {

            getWaiter(getDate).data().subscribe { value ->

                if (value.exists()) {

                    value.children.forEach {

                        val waiter = it.getValue(Waiter::class.java)

                        if (waiter != null) {

                            if (waiter.ship_id == orderId && waiter.status != STATUS.CANCEL) {

                                setupAdapterTab(waiter.id)

                            }
                        }
                    }
                }
            }

        } else {

            setupAdapterTab()

        }

    }

    private fun initView() {

        mSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        mSheetBehavior.skipCollapsed = true
        mSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                if (slideOffset < 0.5f) {
                    btn_hide.visibility = View.GONE
                    btn_order_now.visibility = View.VISIBLE
                } else {
                    btn_hide.visibility = View.VISIBLE
                    btn_order_now.visibility = View.GONE
                }
            }
        })
    }

    private fun initOnClickListener() {

        btn_order_now.setOnClickListener {
            mSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        btn_hide.setOnClickListener {
            mSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        btn_confirm.setOnClickListener {

            btn_confirm.startAnimation()

            if (editTable) {

                getWaiter(getDate).data().subscribe { value ->

                    if (value.exists()) {

                        value.children.forEach { data ->

                            val waiter = data.getValue(Waiter::class.java)

                            if (waiter != null) {

                                if (waiter.ship_id == orderId) {

                                    toast("Order updated")

                                    getWaiterRef.child(getDate).child(waiter.id).child("menu").setValue(listOrder)
                                    getWaiterRef.child(getDate).child(waiter.id).child("price").setValue(priceAll)

                                    finish()
                                }
                            }
                        }
                    }
                }

            } else {

                var waiterID = "1".padStart(5, '0') // value : 00001

                getWaiter(getDate).data().subscribe { value ->

                    if (value.exists()) {

                        value.children.forEach { data ->

                            val waiter = data.getValue(Waiter::class.java)

                            if (waiter != null) {
                                waiterID = (Integer.parseInt(waiter.ship_id) + 1).toString().padStart(5, '0')
                            }
                        }
                    }

                    val waiter = Waiter()
                    waiter.id = getWaiterRef.push().key ?: getTimestamp.toString()
                    waiter.ship_id = waiterID
                    waiter.date = getDate
                    waiter.status = STATUS.WAITING
                    waiter.menu = listOrder
                    waiter.price = priceAll
                    waiter.timestamp = getTimestamp
                    waiter.table = table

                    getTableRef.child("order_$table").setValue(waiterID)
                    getTableRef.child("table_$table").setValue(TABLE.WAITING)
                    getWaiterRef.child(getDate).child(waiter.id).setValue(waiter)

                    SweetAlertDialog(this)
                            .setTitleText("print receipt?")
                            .showContentText(false)
                            .setConfirmText("Yes, Print it!")
                            .setCancelText("No")
                            .setConfirmClickListener {dialog ->

                                print.printReceiptKitchen(waiter)

                                getTableRef.child("table_$table").setValue(TABLE.PLACED)

                                finish()
                                dialog.dismiss()

                            }.setCancelClickListener {dialog ->

                                finish()
                                dialog.dismiss()

                            }.show()


                }
            }
        }

        switch_details.setOnCheckedChangeListener { component, _ ->

            if (component.isChecked) {

                list_order.adapter = OrderAdapterCount(this, getOrderList(listAll, true), true)

            } else {

                list_order.adapter = OrderAdapterCount(this, getOrderList(listAll, false), false)

            }

        }

        txt_reset.setOnClickListener {

            reset()

        }

    }

    private fun setupAdapterTab(orderId: String = "") {


        val mAdapter = if (orderId != "") {

            WaiterAdapterTab(supportFragmentManager, orderId,true)

        }else{

            WaiterAdapterTab(supportFragmentManager)

        }

        viewpager.offscreenPageLimit = mAdapter.count
        viewpager.adapter = mAdapter.SectionsPagerAdapter(supportFragmentManager)
        viewpager.currentItem = 0

        tabs.setupWithViewPager(viewpager)

    }

    var listFood = arrayListOf<Order>()
    var listDrink = arrayListOf<Order>()
    var listExtra = arrayListOf<Order>()
    var listOrder = arrayListOf<Order>()
    var listAll = arrayListOf<Order>()

    override fun listAllOrder(list: ArrayList<Order>, type: TYPE_ORDER) {

        var isHaveMenu = true
        listAll = arrayListOf()

        when (type) {

            TYPE_ORDER.FOOD -> {
                listFood = list
            }

            TYPE_ORDER.DRINK -> {
                listDrink = list
            }

            TYPE_ORDER.EXTRA -> {
                listExtra = list
            }

            TYPE_ORDER.RESET -> {
                listFood = arrayListOf()
                listDrink = arrayListOf()
                listExtra = arrayListOf()
            }

            TYPE_ORDER.UPDATE -> {

                isHaveMenu = false

            }

            else -> {
                isHaveMenu = false
            }

        }

        if (isHaveMenu) {

            listAll.addAll(listFood)
            listAll.addAll(listDrink)
            listAll.addAll(listExtra)

            listOrder = listAll

            list_order.adapter = OrderAdapterCount(this, getOrderList(listAll, switch_details.isChecked), switch_details.isChecked)

        }
    }

    private var countAll = 0
    private var countFood = 0
    private var countDrink = 0
    private var countExtra = 0

    override fun countAllOrder(count: Int, type: TYPE_ORDER) {

        var isHaveCount = true

        when (type) {

            TYPE_ORDER.FOOD -> {
                countFood = count
            }

            TYPE_ORDER.DRINK -> {
                countDrink = count
            }

            TYPE_ORDER.EXTRA -> {
                countExtra = count
            }

            TYPE_ORDER.RESET -> {
                countFood = 0
                countDrink = 0
                countExtra = 0
                countAll = 0
            }

            TYPE_ORDER.UPDATE -> {
                isHaveCount = false
            }

            else -> {
                isHaveCount = false
            }

        }

        if (isHaveCount) {

            countAll = countFood + countDrink + countExtra

            if (countAll > 0) setButtonConfirm(true) else setButtonConfirm(false)

            when {
                countAll == 0 -> txt_total_order.text = StringBuilder("No Order")
                countAll > 2 -> txt_total_order.text = StringBuilder("$countAll Orders")
                else -> txt_total_order.text = StringBuilder("$countAll Order")
            }

            if (countAll > 0) {

                list_empty.visibility = View.GONE
                list_order.visibility = View.VISIBLE
                layout_total.visibility = View.VISIBLE

            } else {

                list_empty.visibility = View.VISIBLE
                list_order.visibility = View.GONE
                layout_total.visibility = View.GONE

            }
        }

    }

    var priceAll = 0f
    var priceFood = 0f
    var priceDrink = 0f
    var priceExtra = 0f

    override fun countAllPrice(price: Float, type: TYPE_ORDER) {

        var isHaveCount = true


        when (type) {

            TYPE_ORDER.FOOD -> {
                priceFood = price
            }

            TYPE_ORDER.DRINK -> {
                priceDrink = price
            }

            TYPE_ORDER.EXTRA -> {
                priceExtra = price
            }

            TYPE_ORDER.RESET -> {
                priceFood = 0f
                priceDrink = 0f
                priceExtra = 0f
                priceAll = 0f
            }

            TYPE_ORDER.UPDATE -> {
                isHaveCount = false
            }

            else -> {
                isHaveCount = false
            }

        }

        if (isHaveCount) {

            priceAll = priceFood + priceDrink + priceExtra

            txt_total_price.text = StringBuilder("LE ${(priceAll)}")
            txt_total_price_list.text = StringBuilder("LE ${(priceAll)}")


        }

    }

    private fun reset() {

        if (btn_confirm.isAnimating) btn_confirm.revertAnimation()

        setupAdapterTab()

        countAllPrice(0f, TYPE_ORDER.RESET)
        countAllOrder(0, TYPE_ORDER.RESET)
        listAllOrder(arrayListOf(), TYPE_ORDER.RESET)

        if (mSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            mSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setButtonConfirm(isClick: Boolean) {

        btn_confirm.isEnabled = isClick

        if (isClick) {
            btn_confirm.setBackgroundColor(Color.parseColor("#FF4322"))
        } else {
            btn_confirm.setBackgroundColor(Color.parseColor("#EEEEEE"))
        }
    }


}
