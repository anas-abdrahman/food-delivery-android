package com.kukus.administrator.dialog


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.androidhuman.rxfirebase2.database.data
import com.kukus.administrator.R
import com.kukus.administrator.waiter.tab.OrderAdapterCount
import com.kukus.library.Constant
import com.kukus.library.FirebaseUtils.Companion.addPriceToPoint
import com.kukus.library.FirebaseUtils.Companion.addWalletToStaff
import com.kukus.library.FirebaseUtils.Companion.getPrice
import com.kukus.library.FirebaseUtils.Companion.getPricePromo
import com.kukus.library.FirebaseUtils.Companion.getPromotion
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.library.ExpandableHeightListView
import com.kukus.library.model.Ship
import org.jetbrains.anko.find
import java.util.*


class DialogPaymentSuccess : DialogFragment() {

    private var customerId = ""
    private var customerDate = ""
    private var customerUserId = ""

    @SuppressLint("CheckResult")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewRoot = inflater.inflate(R.layout.dialog_payment_success, container, false)

        customerId = arguments?.getString("id") ?: ""
        customerDate = arguments?.getString("date") ?: ""
        customerUserId = arguments?.getString("userId") ?: ""

        getOrderList(viewRoot, customerId, customerDate, viewRoot)

        return viewRoot

    }

    var userId = ""
    var shipId = ""

    var _priceAll = 0f
    var delivery = 0f
    var totalPrice = 0f

    var wallet = 0f
    var promoAmount = 0
    var promo = ""

    @SuppressLint("CheckResult")
    private fun getOrderList(viewRoot: View, id: String, date: String, view: View) {

        getShip(date).child(id).data().subscribe { shipRef ->

            if (shipRef.exists()) {

                val ship = shipRef.getValue(Ship::class.java)

                if (ship != null) {

                    setView(view, ship)

                    /* button pay */
                    viewRoot.find<Button>(R.id.btn_pay).setOnClickListener {

                        val update = HashMap<String, Any>()
                        update["status"] = Constant.Companion.STATUS.COMPLETE

                        viewRoot.find<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE

                        getShip(customerDate).child(customerId).updateChildren(update).addOnCompleteListener {

                            if (it.isSuccessful) {

                                addPriceToPoint(customerUserId, _priceAll, "order $shipId")
                                addWalletToStaff(getUserId, delivery, "credit delivery for order #$shipId")

                                Handler().postDelayed({

                                    viewRoot.find<ProgressBar>(R.id.progressBar).visibility = View.GONE
                                    viewRoot.find<ImageView>(R.id.success).visibility = View.VISIBLE

                                }, 1000)


                                Handler().postDelayed({

                                    dismiss()
                                    activity?.finish()

                                }, 2000)

                            }
                        }
                    }

                    /* button amount */
                    viewRoot.find<Button>(R.id.btn_amount).setOnClickListener {

                        val fragmentManager = (context as FragmentActivity).supportFragmentManager
                        val newFragment = DialogAddWallet()
                        val bundle = Bundle()

                        bundle.putString("user", userId)
                        bundle.putString("shipId", shipId)
                        bundle.putString("id", customerId)
                        bundle.putString("date", customerDate)
                        bundle.putFloat("price2", _priceAll)
                        bundle.putFloat("pricetotal", totalPrice)
                        bundle.putString("promo", promo)
                        bundle.putInt("promoAmount", promoAmount)
                        bundle.putFloat("wallet", wallet)
                        bundle.putFloat("delivery", delivery)

                        newFragment.arguments = bundle

                        val transaction = fragmentManager?.beginTransaction()

                        if (transaction != null) {
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()
                        }
                    }
                }
            }
        }
    }

    private fun setView(view: View, ship: Ship) {

        ship.menu.forEach {
            _priceAll += (it.price1 * it.quantity) + (it.price2 * it.quantity)
        }

        promo = ship.promo
        userId = ship.user_id
        wallet = ship.wallet
        shipId = ship.ship_id
        delivery = ship.delivery
        promoAmount = ship.promoAmount

        totalPrice = if (promo != "" && promoAmount != 0) {

            val promotion = getPromotion(_priceAll, ship.promoAmount)

            view.find<TextView>(R.id.txt_promo).visibility = View.VISIBLE
            view.find<TextView>(R.id.txt_menu_promo).visibility = View.VISIBLE
            view.find<TextView>(R.id.txt_promo).text = StringBuilder("- LE $promotion")
            view.find<TextView>(R.id.txt_promo).setTextColor(Color.RED)

            getPricePromo(_priceAll, delivery, promotion, ship.wallet)

        } else {

            view.find<TextView>(R.id.txt_promo).visibility = View.GONE
            view.find<TextView>(R.id.txt_menu_promo).visibility = View.GONE
            view.find<TextView>(R.id.txt_promo).setTextColor(Color.BLACK)

            getPrice(_priceAll, delivery, ship.wallet)

        }

        if (totalPrice <= 0) {

            view.find<TextView>(R.id.txt_amount).text = StringBuilder("EGP 0")
            view.find<Button>(R.id.btn_pay).text = StringBuilder("Confirm")
            view.find<Button>(R.id.btn_amount).setBackgroundResource(R.drawable.shape_radius_grey_5)
            view.find<Button>(R.id.btn_amount).isEnabled = false

        } else {

            view.find<TextView>(R.id.txt_amount).text = StringBuilder("EGP $totalPrice")
            view.find<Button>(R.id.btn_amount).isEnabled = true
            view.find<Button>(R.id.btn_pay).text = StringBuilder("Pay $totalPrice")

        }

        if (ship.wallet > 0) {

            view.find<TextView>(R.id.txt_menu_wallet).visibility = View.VISIBLE
            view.find<TextView>(R.id.txt_wallet).visibility = View.VISIBLE
            view.find<TextView>(R.id.txt_wallet).text = StringBuilder("- LE ${ship.wallet}")
            view.find<TextView>(R.id.txt_wallet).setTextColor(Color.RED)

        } else {

            view.find<TextView>(R.id.txt_menu_wallet).visibility = View.GONE
            view.find<TextView>(R.id.txt_wallet).visibility = View.GONE
            view.find<TextView>(R.id.txt_wallet).setTextColor(Color.BLACK)
        }


        view.find<ExpandableHeightListView>(R.id.list_order).adapter = OrderAdapterCount(context!!, ship.menu, false)
        view.find<TextView>(R.id.txt_total).text = StringBuilder("LE $totalPrice")
        view.find<TextView>(R.id.txt_delivery_charge).text = StringBuilder("LE ${ship.delivery}")
        view.find<LinearLayout>(R.id.layout).visibility = View.VISIBLE
        view.find<ProgressBar>(R.id.progressBar).visibility = View.GONE


    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        return dialog
    }

}