package com.kukus.administrator.orderWaiter.tab

import android.annotation.SuppressLint
import android.app.FragmentTransaction
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.administrator.R
import com.kukus.administrator.dialog.DialogCheckout
import com.kukus.administrator.printer.Printer
import com.kukus.administrator.waiter.ActivityAddOrder
import com.kukus.library.Constant
import com.kukus.library.Constant.Companion.EXTRA_STATUS
import com.kukus.library.Constant.Companion.STATUS
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.model.Table
import com.kukus.library.model.Waiter
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.util.*

class OrderWaiterDash : Fragment() {

    private  var statusOrder = STATUS.WAITING

    var statusTable1 = Constant.Companion.TABLE.EMPTY
    var statusTable2 = Constant.Companion.TABLE.EMPTY
    var statusTable3 = Constant.Companion.TABLE.EMPTY
    var statusTable4 = Constant.Companion.TABLE.EMPTY
    var statusTable5 = Constant.Companion.TABLE.EMPTY
    var statusTable6 = Constant.Companion.TABLE.EMPTY
    var statusTable7 = Constant.Companion.TABLE.EMPTY
    var statusTable8 = Constant.Companion.TABLE.EMPTY
    var statusTable9 = Constant.Companion.TABLE.EMPTY
    var statusTable10 = Constant.Companion.TABLE.EMPTY

    lateinit var table_1 : CardView
    lateinit var txt_table_1 : TextView

    lateinit var table_2 : CardView
    lateinit var txt_table_2 : TextView

    lateinit var table_3 : CardView
    lateinit var txt_table_3 : TextView

    lateinit var table_4 : CardView
    lateinit var txt_table_4 : TextView

    lateinit var table_5 : CardView
    lateinit var txt_table_5 : TextView

    lateinit var table_6 : CardView
    lateinit var txt_table_6 : TextView

    lateinit var table_7 : CardView
    lateinit var txt_table_7 : TextView

    lateinit var table_8 : CardView
    lateinit var txt_table_8 : TextView

    lateinit var table_9 : CardView
    lateinit var txt_table_9 : TextView

    lateinit var table_10 : CardView
    lateinit var txt_table_10 : TextView

    private val emptyTable = arrayListOf<String>()

    private lateinit var print : Printer
    companion object {

        fun newInstance(status: STATUS): OrderWaiterDash {

            val bundle = Bundle()
            val fragment = OrderWaiterDash()

            bundle.putSerializable(EXTRA_STATUS, status)

            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        statusOrder = arguments?.get(EXTRA_STATUS) as STATUS

        print = Printer(context!!)

        val view = inflater.inflate(R.layout.activity_waiter, container, false)

        table_1 = view.find(R.id.table_1)
        txt_table_1 = view.find(R.id.txt_table_1)
        table_2 = view.find(R.id.table_2)
        txt_table_2 = view.find(R.id.txt_table_2)
        table_3 = view.find(R.id.table_3)
        txt_table_3 = view.find(R.id.txt_table_3)
        table_4 = view.find(R.id.table_4)
        txt_table_4 = view.find(R.id.txt_table_4)
        table_5 = view.find(R.id.table_5)
        txt_table_5 = view.find(R.id.txt_table_5)
        table_6 = view.find(R.id.table_6)
        txt_table_6 = view.find(R.id.txt_table_6)
        table_7 = view.find(R.id.table_7)
        txt_table_7 = view.find(R.id.txt_table_7)
        table_8 = view.find(R.id.table_8)
        txt_table_8 = view.find(R.id.txt_table_8)
        table_9 = view.find(R.id.table_9)
        txt_table_9 = view.find(R.id.txt_table_9)
        table_10 = view.find(R.id.table_10)
        txt_table_10 = view.find(R.id.txt_table_10)

        initTableStatus()
        initClickListener()

        return view
    }

    @SuppressLint("CheckResult")
    private fun initTableStatus() {

        FirebaseUtils.getTableRef.dataChanges().subscribe { data ->

            if (data.exists()) {

                val table = data.getValue(Table::class.java)

                if (table != null) {

                    if (getDay(FirebaseUtils.getTimestamp) == getDay(table.date)) {

                        emptyTable.clear()

                        statusTable1  = setTableStatus(table_1, txt_table_1, table.table_1, table.order_1, 1)
                        statusTable2  = setTableStatus(table_2, txt_table_2, table.table_2, table.order_2, 2)
                        statusTable3  = setTableStatus(table_3, txt_table_3, table.table_3, table.order_3, 3)
                        statusTable4  = setTableStatus(table_4, txt_table_4, table.table_4, table.order_4, 4)
                        statusTable5  = setTableStatus(table_5, txt_table_5, table.table_5, table.order_5, 5)
                        statusTable6  = setTableStatus(table_6, txt_table_6, table.table_6, table.order_6, 6)
                        statusTable7  = setTableStatus(table_7, txt_table_7, table.table_7, table.order_7, 7)
                        statusTable8  = setTableStatus(table_8, txt_table_8, table.table_8, table.order_8, 8)
                        statusTable9  = setTableStatus(table_9, txt_table_9, table.table_9, table.order_9, 9)
                        statusTable10 = setTableStatus(table_10, txt_table_10, table.table_10, table.order_10, 10)

                    }else{

                        FirebaseUtils.getTableRef.setValue(Table())

                    }

                }

            } else {

                FirebaseUtils.getTableRef.setValue(Table())

            }
        }
    }

    private fun getDay(timestamp : Long) : String {

        val date = Date(timestamp)
        val calendar = Calendar.getInstance(); calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH).toString()

    }

    private fun initClickListener() {

        table_1.setOnClickListener {

            if (statusTable1 == Constant.Companion.TABLE.EMPTY) {
                createOrder(1)
            } else {
                updateOrder(1)
            }

        }
        table_2.setOnClickListener {
            if (statusTable2 == Constant.Companion.TABLE.EMPTY) {
                createOrder(2)
            } else {
                updateOrder(2)
            }
        }
        table_3.setOnClickListener {
            if (statusTable3 == Constant.Companion.TABLE.EMPTY) {
                createOrder(3)
            } else {
                updateOrder(3)
            }
        }
        table_4.setOnClickListener {
            if (statusTable4 == Constant.Companion.TABLE.EMPTY) {
                createOrder(4)
            } else {
                updateOrder(4)
            }
        }
        table_5.setOnClickListener {
            if (statusTable5 == Constant.Companion.TABLE.EMPTY) {
                createOrder(5)
            } else {
                updateOrder(5)
            }
        }
        table_6.setOnClickListener {
            if (statusTable6 == Constant.Companion.TABLE.EMPTY) {
                createOrder(6)
            } else {
                updateOrder(6)
            }
        }
        table_7.setOnClickListener {
            if (statusTable7 == Constant.Companion.TABLE.EMPTY) {
                createOrder(7)
            } else {
                updateOrder(7)
            }
        }
        table_8.setOnClickListener {
            if (statusTable8 == Constant.Companion.TABLE.EMPTY) {
                createOrder(8)
            } else {
                updateOrder(8)
            }
        }
        table_9.setOnClickListener {
            if (statusTable9 == Constant.Companion.TABLE.EMPTY) {
                createOrder(9)
            } else {
                updateOrder(9)
            }
        }
        table_10.setOnClickListener {
            if (statusTable10 == Constant.Companion.TABLE.EMPTY) {
                createOrder(10)
            } else {
                updateOrder(10)
            }
        }
    }

    private fun updateOrder(table: Int) {

        val menus = arrayOf("Edit Order", "Cancel Order", "Move Table", "Combine Table", "Print Kitchen", "Print Order", "Checkout")

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Edit Table")
        builder.setItems(menus) { _, which ->
            when (which)
            {
                0 -> editOrder(table)
                1 -> cancelOrder(table)
                2 -> moveTable(table)
                3 -> combineTable(table)
                4 -> printKitchen(table)
                5 -> printResiept(table)
                6 -> checkoutOrder(table)
            }
        }

        builder.show()
    }

    @SuppressLint("CheckResult")
    private fun printResiept(table: Int) {

        val shipId = getShipId(table)

        getWaiter(getDate).data().subscribe { value ->

            if (value.exists()) {

                value.children.forEach { data ->

                    val waiter = data.getValue(Waiter::class.java)

                    if (waiter != null) {

                        if (waiter.ship_id == shipId) {
                            print.printReceiptCustomer(waiter)
                        }
                    }
                }
            }
        }

    }

    @SuppressLint("CheckResult")
    private fun printKitchen(table: Int) {

        val shipId = getShipId(table)

        getWaiter(getDate).data().subscribe { value ->

            if (value.exists()) {

                value.children.forEach { data ->

                    val waiter = data.getValue(Waiter::class.java)

                    if (waiter != null) {

                        if (waiter.ship_id == shipId) {
                            print.printReceiptKitchen(waiter)
                            FirebaseUtils.getTableRef.child("table_$table").setValue(Constant.Companion.TABLE.PLACED)
                        }
                    }
                }
            }
        }

    }

    private fun setTableStatus(cardView: CardView, textView: TextView, status: Constant.Companion.TABLE, order: String, table: Int = 0): Constant.Companion.TABLE {

        when (status) {
            Constant.Companion.TABLE.WAITING -> {
                cardView.setCardBackgroundColor(Color.parseColor("#d2cdd1"))
                textView.text = order
            }
            Constant.Companion.TABLE.COMBINE -> {
                cardView.setCardBackgroundColor(Color.parseColor("#c0c9e3"))
                textView.text = order
            }
            Constant.Companion.TABLE.PLACED -> {
                cardView.setCardBackgroundColor(Color.parseColor("#32c360"))
                textView.text = order
            }
            else -> {
                emptyTable.add(table.toString())
                cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                textView.text = ("Empty")
            }
        }

        return status
    }

    private fun createOrder(table: Int) {

        val intent = Intent(context, ActivityAddOrder::class.java)
        intent.putExtra("table", table)
        startActivity(intent)

    }

    private fun getShipId(table: Int): String {

        val id = resources.getIdentifier("txt_table_$table", "id", activity?.packageName)



        return view?.find<TextView>(id)?.text.toString()
    }

    private fun editOrder(table: Int) {

        val shipId = getShipId(table)
        val intent = Intent(context, ActivityAddOrder::class.java)

        intent.putExtra("table", table)
        intent.putExtra("shipId", shipId)
        intent.putExtra("edit_table", true)
        startActivity(intent)

    }

    private fun combineTable(table: Int) {

        val shipId = getShipId(table)
        val emptyTable = emptyTable.toTypedArray()
        val dialogEmptyTable = AlertDialog.Builder(context!!)

        dialogEmptyTable.setTitle("Select Table :")
        dialogEmptyTable.setItems(emptyTable) { _, position ->

            val selectedTable = emptyTable[position].toInt()

            getWaiter(getDate).data().subscribe{ value ->

                if (value.exists()) {

                    value.children.forEach { data ->

                        val waiter = data.getValue(Waiter::class.java)

                        if (waiter != null) {

                            if (waiter.ship_id == shipId && waiter.status != STATUS.CANCEL) {

                                FirebaseUtils.getTableRef.child("table_$selectedTable").setValue(Constant.Companion.TABLE.COMBINE)
                                FirebaseUtils.getTableRef.child("order_$selectedTable").setValue(waiter.ship_id)

                            }
                        }
                    }
                }
            }
        }

        dialogEmptyTable.show()
    }

    private fun moveTable(table: Int) {

        val shipId = getShipId(table)
        val emptyTable = emptyTable.toTypedArray()
        val dialogEmptyTable = AlertDialog.Builder(context!!)

        dialogEmptyTable.setTitle("Select Table :")
        dialogEmptyTable.setItems(emptyTable) { _, position ->

            val selectedTable = emptyTable[position].toInt()

            getWaiter(getDate).data().subscribe { value ->

                if (value.exists()) {

                    value.children.forEach { data ->

                        val waiter = data.getValue(Waiter::class.java)

                        if (waiter != null) {

                            if (waiter.ship_id == shipId && waiter.status != STATUS.CANCEL) { // KENA CHECK SINI BILA DAH ORDER TAK MASUK SINI

                                if (waiter.table == table) {

                                    FirebaseUtils.getWaiterRef.child(getDate).child(waiter.id).child("table").setValue(selectedTable)
                                    FirebaseUtils.getTableRef.child("table_$selectedTable").setValue(getTableStatus(table))

                                } else {

                                    FirebaseUtils.getTableRef.child("table_$selectedTable").setValue(Constant.Companion.TABLE.COMBINE)

                                }

                                FirebaseUtils.getTableRef.child("order_$selectedTable").setValue(waiter.ship_id)
                                FirebaseUtils.getTableRef.child("table_$table").setValue(Constant.Companion.TABLE.EMPTY)
                                FirebaseUtils.getTableRef.child("order_$table").setValue("")
                            }
                        }
                    }
                }
            }
        }

        dialogEmptyTable.show()
    }

    private fun getTableStatus(table : Int) : Constant.Companion.TABLE {

        return when(table){
            1-> statusTable1
            2-> statusTable2
            3-> statusTable3
            4-> statusTable4
            5-> statusTable5
            6-> statusTable6
            7-> statusTable7
            8-> statusTable8
            9-> statusTable9
            10-> statusTable10
            else -> Constant.Companion.TABLE.EMPTY
        }

    }

    @SuppressLint("CheckResult")
    private fun cancelOrder(table: Int) {

        val shipId = getShipId(table)

        getWaiter(getDate).data().subscribe { value ->

            if (value.exists()) {

                value.children.forEach { data ->

                    val waiter = data.getValue(Waiter::class.java)

                    if (waiter != null) {

                        if (waiter.ship_id == shipId && waiter.status != STATUS.CANCEL) {

                            if (waiter.table == table) {

                                FirebaseUtils.getWaiterRef
                                        .child(getDate)
                                        .child(waiter.id)
                                        .child("status")
                                        .setValue(STATUS.CANCEL)

                                removeTable(shipId)

                            } else {

                                FirebaseUtils.getTableRef.child("table_$table").setValue(Constant.Companion.TABLE.EMPTY)
                                FirebaseUtils.getTableRef.child("order_$table").setValue("")

                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun removeTable(shipId: String) {

        FirebaseUtils.getTableRef.data().subscribe { checkTable ->

            if (checkTable.exists()) {

                val myTable = checkTable.getValue(Table::class.java)

                if (myTable != null) {

                    if (myTable.order_1 == shipId) {
                        FirebaseUtils.getTableRef.child("table_1").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_1").setValue("")
                    }
                    if (myTable.order_2 == shipId) {
                        FirebaseUtils.getTableRef.child("table_2").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_2").setValue("")
                    }
                    if (myTable.order_3 == shipId) {
                        FirebaseUtils.getTableRef.child("table_3").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_3").setValue("")
                    }
                    if (myTable.order_4 == shipId) {
                        FirebaseUtils.getTableRef.child("table_4").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_4").setValue("")
                    }
                    if (myTable.order_5 == shipId) {
                        FirebaseUtils.getTableRef.child("table_5").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_5").setValue("")
                    }
                    if (myTable.order_6 == shipId) {
                        FirebaseUtils.getTableRef.child("table_6").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_6").setValue("")
                    }
                    if (myTable.order_7 == shipId) {
                        FirebaseUtils.getTableRef.child("table_7").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_7").setValue("")
                    }
                    if (myTable.order_8 == shipId) {
                        FirebaseUtils.getTableRef.child("table_8").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_8").setValue("")
                    }
                    if (myTable.order_9 == shipId) {
                        FirebaseUtils.getTableRef.child("table_9").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_9").setValue("")
                    }
                    if (myTable.order_10 == shipId) {
                        FirebaseUtils.getTableRef.child("table_10").setValue(Constant.Companion.TABLE.EMPTY)
                        FirebaseUtils.getTableRef.child("order_10").setValue("")
                    }
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun checkoutOrder(table: Int) {

        val shipId = getShipId(table)
//        val dialogBuilder = AlertDialog.Builder(context!!)
//        val dialogView = layoutInflater.inflate(R.layout.dialog_menu_checkout, null)
//
//        val layout = dialogView.findViewById(R.id.layout_price) as LinearLayout
//        val price = dialogView.findViewById(R.id.price) as TextView
//        val progressBar = dialogView.findViewById(R.id.progressBar) as ProgressBar

//        dialogBuilder.setView(dialogView)

        getWaiter(getDate).data().subscribe { value ->

            if (value.exists()) {

//                progressBar.visibility = View.GONE
//                layout.visibility = View.VISIBLE

                value.children.forEach { data ->

                    val waiter = data.getValue(Waiter::class.java)

                    if (waiter != null) {

                        if (waiter.ship_id == shipId && waiter.status != STATUS.CANCEL) {

                            val fragmentManager = activity?.supportFragmentManager
                            val newFragment = DialogCheckout()
                            val transaction = fragmentManager?.beginTransaction()

                            val bundle = Bundle()

                            bundle.putString("id", waiter.id)
                            bundle.putString("date", waiter.date)

                            newFragment.arguments = bundle

                            if (transaction != null) {
                                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

                            }
                            /*price.text = waiter.price.toString()

                            dialogBuilder.setPositiveButton("Pay") { _, _ ->

                                FirebaseUtils.getWaiterRef
                                        .child(waiter.date)
                                        .child(waiter.id)
                                        .child("status")
                                        .setValue(STATUS.COMPLETE)


                                print.printReceiptCustomer(waiter)

                                removeTable(shipId)

                            }

                            dialogBuilder.setNegativeButton("Cancel") { _, _ ->

                            }

                            dialogBuilder.create().show()*/
                        }
                    }
                }
            }
        }
    }

}

