package com.kukus.administrator.statistic

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.administrator.R
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getDateConvert
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getShipRef
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.FirebaseUtils.Companion.getWaiterRef
import com.kukus.library.model.Ship
import com.kukus.library.model.Statistic
import com.kukus.library.model.Waiter
import kotlinx.android.synthetic.main.activity_statistic.*
import java.util.*


class ActivityStatistic : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        getDataFirebase()

    }

    @SuppressLint("CheckResult")
    fun getDataFirebase() {

        var order_in = 0
        var order_out = 0
        var amount = 0f

        getShip(getDate).data().subscribe { shipRef->

            if (shipRef.exists()) {

                shipRef.children.forEach { data ->

                    val ship = data.getValue(Ship::class.java)

                    if (ship != null) {

                        if (ship.status == Constant.Companion.STATUS.COMPLETE) {

                            amount += ship.price
                            order_out++

                        }

                    }

                }

//                txt_order_in.text = order_in.toString()
//                txt_order_out.text = order_out.toString()
//                txt_amount.text = ("LE " + amount.toInt().toString())

            }
        }

        getWaiter(getDate).dataChanges().subscribe { waiterRef ->

            if (waiterRef.exists()) {

                waiterRef.children.forEach { data ->

                    val waiter = data.getValue(Waiter::class.java)

                    if (waiter != null) {

                        if (waiter.status == Constant.Companion.STATUS.COMPLETE) {

                            amount += waiter.price
                            order_in++

                        }

                    }

                }

//                txt_order_in.text = order_in.toString()
//                txt_order_out.text = order_out.toString()
//                txt_amount.text = ("LE " + amount.toInt().toString())

            }
        }



        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val orderAdapter = StatisticAdapter()
        recyclerView.adapter = orderAdapter
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setItemViewCacheSize(30)

        getShipRef.dataChanges().subscribe { shipRef ->

            if (shipRef.exists()) {

                val listOrders = mutableListOf<Ship>()

                val hashMap = HashMap<String, MutableList<Ship>>()

                shipRef.children.forEach { data ->

                    val date = data.key

                    if (date != null) {

                        data.children.forEach { child ->

                            val ship = child.getValue(Ship::class.java)

                            if (ship != null) {

                                listOrders.add(ship)

                            }
                        }

                        hashMap[date] = listOrders

                    }
                }

                getWaiterRef.dataChanges().subscribe { waiterRef ->

                    if (waiterRef.exists()) {

                        val listWaiter = mutableListOf<Waiter>()

                        val hashMapWaiter = HashMap<String, MutableList<Waiter>>()

                        waiterRef.children.forEach { data ->

                            val date = data.key

                            if (date != null) {

                                data.children.forEach { child ->

                                    val waiter = child.getValue(Waiter::class.java)

                                    if (waiter != null) {

                                        listWaiter.add(waiter)

                                    }
                                }

                                hashMapWaiter[date] = listWaiter

                            }
                        }

                        val listStatistic = arrayListOf<Statistic>()

                        for ((key, value) in hashMap) {

                            var total = 0f
                            var orderOut = 0

                            value.forEach {
                                if (key == it.date && it.status == Constant.Companion.STATUS.COMPLETE) {
                                    total += it.price
                                    orderOut += 1
                                }
                            }

                            listStatistic.add(Statistic(orderOut,0 ,getDateConvert(key), total.toInt()))

                        }

                        for ((key, value) in hashMapWaiter) {

                            var total = 0f
                            var orderIn = 0

                            value.forEach {

                                if (key == it.date && it.status == Constant.Companion.STATUS.COMPLETE) {

                                    total = it.price
                                    orderIn += 1

                                    listStatistic.forEach {orderOut->

                                        if(orderOut.date == getDateConvert(key)){

                                            orderOut.orderIn = orderIn
                                            orderOut.total += total.toInt()

                                        }
                                    }
                                }
                            }
                        }

                        progress.visibility = View.GONE

                        //orderAdapter.swapAdapter(listStatistic)

                    }else{

                        progress.visibility = View.GONE

                    }

                }

            }else{

                progress.visibility = View.GONE

            }
        }
    }

    /*private fun setupChart(data : ArrayList<Statistic>) {

        val entry = arrayListOf<Entry>()

        for (value in data.takeLast(7)){
            entry.add(Entry(value.count.toFloat(), value.total.toFloat()))
        }

        val lds = LineDataSet(entry, "count")
        lds.setCircleColor(Color.RED)
        lds.circleRadius = 8f
        lds.circleHoleRadius = 5f
        lds.setCircleColorHole(Color.WHITE)
        lds.color = Color.RED
        lds.lineWidth = 3f
        val ld = LineData(lds)
        ld.setDrawValues(false)
        chart.data = ld

        val textSize = 12f
        val xa = chart.xAxis
        xa.granularity = 1f
        xa.isGranularityEnabled = true
        xa.valueFormatter = StickyDateAxisValueFormatter(chart, label_1)
        xa.position = XAxis.XAxisPosition.BOTTOM
        xa.textSize = textSize
        xa.setDrawGridLines(true)

        chart.axisLeft.setDrawAxisLine(false)
        chart.axisLeft.gridColor = Color.parseColor("#dddddd")
        chart.axisLeft.textColor = Color.parseColor("#bbbbbb")
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.setDrawAxisLine(false)
        chart.xAxis.axisLineColor = Color.parseColor("#aaaaaa")
        chart.xAxis.textColor = Color.parseColor("#bbbbbb")

        val max = chart.data.yMax.toInt()
        chart.axisLeft.labelCount = max

        chart.axisLeft.setValueFormatter { value, axis ->

            return@setValueFormatter Math.floor(value.toDouble()).toInt().toString()
        }


        chart.setVisibleXRangeMaximum(7f)
        chart.axisLeft.textSize = textSize
        //chart.extraBottomOffset = 10f

        chart.axisRight.isEnabled = false
        val desc = Description()
        desc.setText("")
        chart.description = desc
        chart.legend.isEnabled = false
        chart.axisLeft.setDrawGridLines(true)


    }
*/

    /*inner class StickyDateAxisValueFormatter internal constructor(private val chart: LineChart, private val sticky: TextView) : IAxisValueFormatter {

        private val c: Calendar
        private var lastFormattedValue = 1e9f
        private var lastMonth = 0
        private var lastYear = 0
        private var stickyMonth = -1
        private var stickyYear = -1
        private val monthFormatter = SimpleDateFormat("MMM", Locale.getDefault())


        init {
            c = GregorianCalendar()
        }

        override fun getFormattedValue(value: Float, axis: AxisBase): String {

            // Sometimes this gets called on values much lower than the visible range
            // Catch that here to prevent messing up the sticky text logic
            if (value < chart.lowestVisibleX) {
                return ""
            }

            // NOTE: I assume for this example that all count is plotted in days
            // since Jan 1, 2018. Update for your scheme accordingly.

            val days = value.toInt()

            val isFirstValue = value < lastFormattedValue

            if (isFirstValue) {
                // starting over formatting sequence
                lastMonth = 50
                lastYear = 5000

                c.set(2019, 4, 12)
                c.add(Calendar.DATE, chart.lowestVisibleX.toInt())

                stickyMonth = c.get(Calendar.MONTH)
                stickyYear = c.get(Calendar.YEAR)

                val stickyText = monthFormatter.format(c.getTime()) + " " + stickyYear
                sticky.setText(stickyText)
            }

            c.set(2019, 4, 12)
            c.add(Calendar.DATE, days)
            val d = c.getTime()

            val dayOfMonth = c.get(Calendar.DAY_OF_MONTH)
            val month = c.get(Calendar.MONTH)
            val year = c.get(Calendar.YEAR)

            val monthStr = monthFormatter.format(d)

            if ((month > stickyMonth || year > stickyYear) && isFirstValue) {
                stickyMonth = month
                stickyYear = year
                val stickyText = monthStr + " " + year
                sticky.setText(stickyText)
            }

            val ret: String

            if ((month > lastMonth || year > lastYear) && !isFirstValue) {
                ret = monthStr
            } else {
                ret = Integer.toString(dayOfMonth)
            }

            lastMonth = month
            lastYear = year
            lastFormattedValue = value

            return ret
        }
    }*/


}
