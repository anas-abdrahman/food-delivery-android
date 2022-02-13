package com.kukus.customer.reward

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.customer.R
import com.kukus.library.FirebaseUtils.Companion.addRewardWallet
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.User
import kotlinx.android.synthetic.main.activity_reward.*
import org.jetbrains.anko.toast
import android.os.Handler


class ActivityReward : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward)

        getUser(getUserId).dataChanges().subscribe { userData ->
            if (userData.exists()) {

                val user = userData.getValue(User::class.java)

                if (user != null) {

                    point.text = ("Your Points : " + user.point)

                    setUpRecyclerView()
                    populateRecyclerView(user.point, user.wallet)

                }
            }
        }


    }

    private fun setUpRecyclerView() {

        val numGrid = 2
        val gridLayoutManager = GridLayoutManager(this, numGrid)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    0  -> 2
                    else -> 1
                }
            }
        }

        item_recycler_view.layoutManager = gridLayoutManager
        item_recycler_view.setHasFixedSize(true)
    }

    private fun populateRecyclerView(point: Int, wallet: Float) {

        val itemArrayList = arrayListOf<pointReward>()

        itemArrayList.add(pointReward(5, 25))
        itemArrayList.add(pointReward(10, 50))
        itemArrayList.add(pointReward(20, 100))
        itemArrayList.add(pointReward(30, 150))
        itemArrayList.add(pointReward(40, 200))
        itemArrayList.add(pointReward(50, 250))
        itemArrayList.add(pointReward(60, 300))

        item_recycler_view.adapter = ItemRecyclerViewAdapter(this, itemArrayList, point, wallet)
    }

    internal class pointReward(val amount: Int, val point: Int)

    internal inner class ItemRecyclerViewAdapter(private val context: Context, private val arrayList: ArrayList<pointReward>, private var point: Int, private var wallet: Float) : RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemViewHolder>() {

        internal inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cardViewPoint: CardView = itemView.findViewById<View>(R.id.cardView_point) as CardView
            val amount: TextView = itemView.findViewById<View>(R.id.amount) as TextView
            //val description: TextView = itemView.findViewById<View>(R.id.description) as TextView
            val point: TextView = itemView.findViewById<View>(R.id.point) as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_custom_row_layout, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

            holder.amount.text = ("LE ${arrayList[position].amount}")
            holder.point.text = ("${arrayList[position].point} points")

            if (point >= arrayList[position].point) {

                holder.cardViewPoint.setCardBackgroundColor(Color.parseColor("#F44336"))

                holder.cardViewPoint.setOnClickListener {

                    AlertDialog.Builder(context)
                            .setTitle("Redeem ${arrayList[position].point} points")
                            .setMessage("Redeeming this reward will add LE ${arrayList[position].amount} credit to your wallet to use on any future order")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Redeem") { _, _ ->

                                val mAmount = arrayList[position].amount.toFloat()
                                val mPoint = arrayList[position].point

                                addRewardWallet(getUserId, mAmount, mPoint,"Redeem ${arrayList[position].point} point")

                                context.toast("successfully")

                            }
                            .create()
                            .show()
                }

            } else {

                holder.cardViewPoint.setCardBackgroundColor(Color.parseColor("#DDDDDD"))
                holder.point.setTextColor(Color.parseColor("#DDDDDD"))

            }


        }


        override fun getItemCount(): Int {
            return arrayList.size
        }

    }

}
