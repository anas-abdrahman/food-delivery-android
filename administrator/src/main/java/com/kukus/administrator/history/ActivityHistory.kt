package com.kukus.administrator.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
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
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.FirebaseUtils.Companion.getWaiter
import com.kukus.library.FirebaseUtils.Companion.getWaiterRef
import com.kukus.library.model.History
import com.kukus.library.model.Ship
import com.kukus.library.model.Statistic
import com.kukus.library.model.Waiter
import kotlinx.android.synthetic.main.activity_history.*
import java.util.*


class ActivityHistory : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        getDataFirebase()

    }

    @SuppressLint("CheckResult")
    fun getDataFirebase() {


        getShip(getDate).data().subscribe { shipRef ->

            if (shipRef.exists()) {

                var trip = 0
                var amount = 0f

                shipRef.children.forEach {data ->

                    val ship = data.getValue(Ship::class.java)

                    if(ship != null){

                        if (ship.status == Constant.Companion.STATUS.COMPLETE && ship.deliveryId == getUserId) {

                            amount += ship.delivery
                            trip++

                        }

                    }
                }

                txt_trip.text = trip.toString()
                txt_total.text = ("LE " + amount.toInt().toString())
            }
        }

        getShipRef.data().subscribe { shipRef ->

            if (shipRef.exists()) {

                val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
                val listHistory = arrayListOf<History>()

                val orderAdapter = HistoryAdapter()
                recyclerView.adapter = orderAdapter
                recyclerView.layoutManager = linearLayoutManager
                recyclerView.setItemViewCacheSize(30)

                shipRef.children.forEach { ships ->

                    var trip = 0
                    var date = ""
                    var amount = 0f

                    ships.children.forEach { data ->

                        val ship = data.getValue(Ship::class.java)

                        if (ship != null) {

                            if (ship.status == Constant.Companion.STATUS.COMPLETE && ship.deliveryId == getUserId) {

                                date = ship.date
                                amount += ship.delivery
                                trip++

                            }

                        }

                    }

                    if(trip != 0 && amount != 0f) {
                        listHistory.add(History(trip, getDateConvert(date, "/"), amount))
                    }
                }

                orderAdapter.swapAdapter(listHistory)

                Handler().postDelayed({ progress.visibility = View.GONE }, 1000)

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
