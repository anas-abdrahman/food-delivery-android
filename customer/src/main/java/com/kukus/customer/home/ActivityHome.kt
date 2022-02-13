package com.kukus.customer.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.ShareCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.dataChanges
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding2.view.RxView
import com.kukus.customer.R
import com.kukus.customer.about.ActivityAbout
import com.kukus.customer.chat.ActivityChat
import com.kukus.customer.checkout.ActivityCheckout
import com.kukus.customer.checkout.ListOrderAdapter
import com.kukus.customer.dialog.DialogPromo
import com.kukus.customer.dialog.DialogVoucher
import com.kukus.customer.home.tab.HomeTab
import com.kukus.customer.login.ActivityLogin
import com.kukus.customer.message.ActivityMessage
import com.kukus.customer.news.ActivityNews
import com.kukus.customer.news.ActivityNewsDetails
import com.kukus.customer.order.ActivityListOrder
import com.kukus.customer.order.ActivityOrderDetails
import com.kukus.customer.point.ActivityPoint
import com.kukus.customer.profile.ActivityProfile
import com.kukus.customer.reward.ActivityReward
import com.kukus.customer.servis.ServiceOrderNotification
import com.kukus.customer.tracking.ActivityTracking
import com.kukus.customer.wallet.ActivityWallet
import com.kukus.library.Constant
import com.kukus.library.Constant.Companion.RESULT_CHECKOUT
import com.kukus.library.Constant.Companion.TYPE_ORDER
import com.kukus.library.Constant.Companion.setDataUser
import com.kukus.library.FirebaseUtils
import com.kukus.library.FirebaseUtils.Companion.addWalletToUser
import com.kukus.library.FirebaseUtils.Companion.getContact
import com.kukus.library.FirebaseUtils.Companion.getDate
import com.kukus.library.FirebaseUtils.Companion.getNews
import com.kukus.library.FirebaseUtils.Companion.getNewsRef
import com.kukus.library.FirebaseUtils.Companion.getPromoRef
import com.kukus.library.FirebaseUtils.Companion.getSetting
import com.kukus.library.FirebaseUtils.Companion.getShip
import com.kukus.library.FirebaseUtils.Companion.getTimestamp
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserAuth
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.FirebaseUtils.Companion.getUserRef
import com.kukus.library.FirebaseUtils.Companion.getVoucherRef
import com.kukus.library.interfaces.CountOrderListener
import com.kukus.library.library.DrawerBadge
import com.kukus.library.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.layout_point
import kotlinx.android.synthetic.main.nav_sheet.*
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.util.*
import java.util.concurrent.TimeUnit

class ActivityHome : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, CountOrderListener {

    private lateinit var mSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var newPromo: String = ""
    private var newVoucher: String = ""
    private var referralId: String = ""
    private var referral: String = ""
    private var userWallet: Float = 0f
    private var userPoint: Int = 0
    private var userPromo = Promo()
    private var isOpen    = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        initView()
        onListener()
        dataFirebase()
        setupAdapterTab()

    }

    private fun initView() {

        /**
         * Start Service intent for notification
         * */
        startService(Intent(this, ServiceOrderNotification::class.java))

        /**
         * init drawer nav side
         * */
        val toggle = ActionBarDrawerToggle(this, drawer_layout, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        nav_view.setNavigationItemSelectedListener(this)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


        /**
         * init bottomSheet
         * */
        mSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        mSheetBehavior.skipCollapsed = true
        mSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                if (slideOffset < 0.1f) {
                    fab_contact.show()
                    btn_hide.visibility = View.GONE
                    btn_checkout.visibility = View.VISIBLE
                } else {
                    fab_contact.hide()
                    btn_hide.visibility = View.VISIBLE
                    btn_checkout.visibility = View.GONE
                }
            }
        })

        /**
         * init view pager
         * */
        viewpager.setOnTouchListener { _, motionEvent ->
            swipe.isEnabled = false
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    swipe.isEnabled = true
                }
            }
            false

        }

        /**
         * init swipe refresh
         * */
        swipe.setOnRefreshListener {

            reset(viewpager.currentItem)

        }
    }

    @SuppressLint("CheckResult")
    private fun onListener() {

        RxView.clicks(btn_confirm)
                .observeOn(AndroidSchedulers.mainThread())
                .filter {

                    if (listOrder.size > 0) {
                        btn_confirm.startAnimation()
                        true
                    } else {
                        false
                    }

                }
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribe {

                    val intent = Intent(this, ActivityCheckout::class.java)

                    intent.putExtra("userPromoCode", userPromo.code)
                    intent.putExtra("userPromoAmount", userPromo.amount)
                    intent.putExtra("userWallet", userWallet)
                    intent.putParcelableArrayListExtra("list", listOrder)

                    startActivityForResult(intent, RESULT_CHECKOUT)

                }

        layout_promo_available.setOnClickListener {

            val fragmentManager = supportFragmentManager
            val newFragment = DialogPromo()

            val bundle = Bundle()
            bundle.putString("code", userPromo.code)
            bundle.putLong("date", userPromo.expireAt)
            newFragment.arguments = bundle

            val transaction = fragmentManager?.beginTransaction()

            if (transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

            }
        }

        layout_promo.setOnClickListener {

            val fragmentManager = supportFragmentManager
            val newFragment = DialogPromo()

            val bundle = Bundle()
            bundle.putString("code", userPromo.code)
            bundle.putLong("date", userPromo.expireAt)
            newFragment.arguments = bundle

            val transaction = fragmentManager?.beginTransaction()

            if (transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

            }

        }

        layout_promo_gift.setOnClickListener {

            val fragmentManager = supportFragmentManager
            val newFragment = DialogPromo()

            val bundle = Bundle()
            bundle.putString("code", newPromo)

            newFragment.arguments = bundle

            val transaction = fragmentManager?.beginTransaction()

            if (transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()

            }
        }

        layout_voucher_gift.setOnClickListener {

            val fragmentManager = supportFragmentManager
            val newFragment = DialogVoucher()

            val bundle = Bundle()
            bundle.putString("code", newVoucher)

            newFragment.arguments = bundle

            val transaction = fragmentManager?.beginTransaction()

            if (transaction != null) {
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit()
            }
        }

        voucher_close.setOnClickListener {
            layout_voucher_gift.visibility = View.GONE
        }

        promo_close.setOnClickListener {
            layout_promo_gift.visibility = View.GONE
        }

        btn_checkout.setOnClickListener {
            mSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        btn_hide.setOnClickListener {
            mSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        txt_reset.setOnClickListener {
            reset()
        }

        fab_contact.setOnClickListener {
            val intent = Intent(this, ActivityMessage::class.java)
            startActivity(intent)
        }
    }

    private fun setupAdapterTab(page: Int = 0) {

        setButtonConfirm(false)

        val mAdapter = HomeTab(supportFragmentManager)

        viewpager.offscreenPageLimit = mAdapter.count
        viewpager.adapter = mAdapter.SectionsPagerAdapter(supportFragmentManager)
        viewpager.currentItem = page

        tabs.setupWithViewPager(viewpager)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent = Intent(this, ActivityProfile::class.java)
                startActivity(intent)
            }
            R.id.nav_order -> {
                val intent = Intent(this, ActivityListOrder::class.java)
                startActivity(intent)
            }
            R.id.nav_wallet -> {
                val intent = Intent(this, ActivityWallet::class.java)
                startActivity(intent)
            }
            R.id.nav_chat -> {
                val intent = Intent(this, ActivityChat::class.java)
                startActivity(intent)
            }
            R.id.nav_news -> {
                val intent = Intent(this, ActivityNews::class.java)
                startActivity(intent)
            }
            R.id.nav_message -> {
                val intent = Intent(this, ActivityMessage::class.java)
                startActivity(intent)
            }
            R.id.nav_redeem -> {
                getRedeem()
            }
            R.id.nav_invite -> {
                getInvite()
            }
            R.id.nav_about -> {
                val intent = Intent(this, ActivityAbout::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                getUserAuth.signOut()
                startActivity(Intent(this, ActivityLogin::class.java))
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CHECKOUT && resultCode == Activity.RESULT_OK) {

            val intent = Intent(this, ActivityOrderDetails::class.java)

            if(data != null) {

                intent.putExtra("id", data.getStringExtra("ship_id"))
                intent.putExtra("date", data.getStringExtra("date"))
                startActivity(intent)

            }

            reset()

        }

        btn_confirm.revertAnimation()

    }

    @SuppressLint("CheckResult")
    private fun dataFirebase() {

        getContact(getUserId).dataChanges().subscribe {

            var count = 0

            it?.children?.forEach { data ->

                val contact = data.getValue(Message::class.java)

                if (contact != null) {

                    if (!contact.read && contact.user_to == getUserId) {
                        count += 1
                    }
                }
            }

            if (count > 0) {
                DrawerBadge(applicationContext, nav_view, R.id.nav_message, "new $count", "#FFFFFF", "#FF0000", "#FF0000")
            }
        }

        getShip(getDate).dataChanges().subscribe {

            it?.children?.forEach { data ->

                val ship = data.getValue(Ship::class.java)

                if (ship != null) {


                    var intent = Intent(this, ActivityListOrder::class.java)


                    if(ship.user_id == getUserId && ship.status == Constant.Companion.STATUS.PENDING){

                        layout_order_available.visibility = View.VISIBLE

                        intent = Intent(this, ActivityOrderDetails::class.java)
                        intent.putExtra("id", ship.id)
                        intent.putExtra("date", ship.date)

                        label_order_available.text = ("Your Order is Preparing")
                        txt_order_available.text = ("Details")


                    }else if(ship.user_id == getUserId && ship.status == Constant.Companion.STATUS.DISPATCHED){

                        layout_order_available.visibility = View.VISIBLE

                        intent = Intent(this, ActivityTracking::class.java)
                        intent.putExtra("lat", ship.address.lat)
                        intent.putExtra("lng", ship.address.lng)
                        intent.putExtra("deliveryId", ship.deliveryId)
                        intent.putExtra("id", ship.id)
                        intent.putExtra("date", ship.date)
                        intent.putExtra("isMessage", false)

                        label_order_available.text = ("Your Order is Delivering")
                        txt_order_available.text = ("Track Order")

                    }else{

                        layout_order_available.visibility = View.GONE

                    }


                    layout_order_available.setOnClickListener {

                        startActivity(intent)

                    }

                }
            }

        }

        getSetting("time").data().subscribe { data ->

            if (data.exists()) {

                val dataTime = data.getValue(Time::class.java)

                if (dataTime != null) {

                    isOpen = dataTime.open

                }
            }
        }


        getNewsRef.data().subscribe { dataRef ->

            var count = 0
            var first = true

            dataRef?.children?.forEach { data ->

                val news = data.getValue(News::class.java)

                if (news != null) {

                    val twoDay = 172800000
                    val countDay = getTimestamp - news.date

                    if (!news.reads.contains(getUserId) && countDay < twoDay) {

                        count++

                        if (first) {

                            first = false

                            getNewsAds(news)

                            news.reads.add(getUserId)
                            getNews(news.id).child("reads").setValue(news.reads)

                        }

                    }
                }
            }

            if (count > 0) {
                DrawerBadge(applicationContext, nav_view, R.id.nav_news, "new $count", "#FFFFFF", "#FF0000", "#FF0000")
            }
        }

        getUser(getUserId).dataChanges().subscribe { userData ->

            if (userData.exists()) {

                val user = userData.getValue(User::class.java)

                if (user != null) {

                    if (user.referral == "") {
                        showMenu(R.id.nav_redeem, true)
                    } else {
                        showMenu(R.id.nav_redeem, false)
                    }

                    progressDrawer.visibility = View.GONE

                    nav_name.visibility = View.VISIBLE
                    nav_email.visibility = View.VISIBLE

                    setDataUser(this, user)
                    DrawerBadge(applicationContext, nav_view, R.id.nav_wallet, "EGP ${user.wallet}", "#FFFFFF", "#FF0000", "#FF0000")

                    userWallet = user.wallet
                    userPoint = user.point
                    referralId = user.refId
                    referral = user.referral

                    nav_name.text = StringBuilder(user.name.toUpperCase())
                    nav_email.text = StringBuilder(user.email.toUpperCase())
                    nav_point.text = StringBuilder("$userPoint points")

                    layout_point.setOnClickListener {

                        val intent = Intent(baseContext, ActivityPoint::class.java)
                        startActivity(intent)

                    }
                }
            }
        }

            // Promo Code
            getPromoRef.dataChanges().subscribe { promoRef ->

                if (promoRef.exists()) {

                    var isHavesPromo = false
                    var isValidPromo = false

                    promoRef.children.forEach { promoChild ->

                        val promoCheck = promoChild.getValue(Promo::class.java)

                        if (promoCheck != null) {

                            val promoUser = promoCheck.user.contains(getUserId)

                            if (promoUser && !isValidPromo) {
                                isValidPromo = (checkPromoFormUser(promoCheck))

                            } else if (!promoUser && promoCheck.active && (System.currentTimeMillis() < promoCheck.expireAt && promoCheck.currentLimit < promoCheck.limit)) {
                                isHavesPromo = true
                                newPromo = promoCheck.code
                            }

                        }
                    }

                    if (isValidPromo) {

                        txt_promo_available.text = java.lang.StringBuilder("${userPromo.amount}%")

                        layout_promo_available.visibility = View.VISIBLE
                        layout_promo_gift.visibility = View.GONE

                        isHavesPromo = false

                    } else {

                        layout_promo_available.visibility = View.GONE

                    }

                    if (isHavesPromo && newVoucher == "") {

                        layout_promo_gift.visibility = View.VISIBLE

                    } else {

                        layout_promo_gift.visibility = View.GONE
                        newPromo = ""

                    }
                }
            }

            // Voucher Code
            getVoucherRef.dataChanges().subscribe { voucherRef ->

                if (voucherRef.exists()) {

                    var isHaveVoucher = false

                    voucherRef.children.forEach { voucherChild ->

                        val voucherCheck = voucherChild.getValue(Voucher::class.java)

                        if (voucherCheck != null) {

                            val voucherUser = voucherCheck.user.contains(getUserId)

                            if (!voucherUser && voucherCheck.active && (System.currentTimeMillis() < voucherCheck.expireAt && voucherCheck.currentLimit < voucherCheck.limit)) {
                                isHaveVoucher = true
                                newVoucher = voucherCheck.code
                            }
                        }
                    }

                    if (isHaveVoucher && newPromo == "") {

                        layout_voucher_gift.visibility = View.VISIBLE

                    } else {

                        layout_voucher_gift.visibility = View.GONE
                        newVoucher = ""

                    }
                }
            }



    }


    var listFoods = arrayListOf<Order>()
    var listDrink = arrayListOf<Order>()
    var listExtra = arrayListOf<Order>()
    var listOrder = arrayListOf<Order>()

    override fun listAllOrder(list: ArrayList<Order>, type: TYPE_ORDER) {

        var isHaveMenu = true

        val listAll = arrayListOf<Order>()

        when (type) {

            TYPE_ORDER.FOOD -> {
                listFoods = list
            }

            TYPE_ORDER.DRINK -> {
                listDrink = list
            }

            TYPE_ORDER.EXTRA -> {
                listExtra = list
            }

            TYPE_ORDER.RESET -> {
                listFoods.clear()
                listDrink.clear()
                listExtra.clear()
            }

            TYPE_ORDER.UPDATE -> {
                isHaveMenu = false
            }

            else -> {
                isHaveMenu = false
            }

        }

        if (isHaveMenu) {

            listAll.addAll(listFoods)
            listAll.addAll(listDrink)
            listAll.addAll(listExtra)

            listOrder = listAll

            list_order.adapter = ListOrderAdapter(this, listOrder)
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
                txt_reset.visibility = View.VISIBLE
                layout_total.visibility = View.VISIBLE

                if (userPromo.code != "") {
                    layout_promo.visibility = View.VISIBLE
                } else {
                    layout_promo.visibility = View.GONE
                }

            } else {

                list_empty.visibility = View.VISIBLE
                list_order.visibility = View.GONE
                txt_reset.visibility = View.GONE
                layout_total.visibility = View.GONE
                layout_promo.visibility = View.GONE

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

            if (userPromo.code != "") {

                val promotion = FirebaseUtils.getPromotion(priceAll, userPromo.amount)

                label_promo.text = StringBuilder("Promotion Code (${userPromo.code}) - ${userPromo.amount}%")
                txt_promo.text = StringBuilder("- LE $promotion")

                txt_total_price.text = StringBuilder("LE ${(priceAll) - promotion}")
                txt_total_price_list.text = StringBuilder("LE ${(priceAll) - promotion}")

            } else {

                txt_promo.text = StringBuilder("- LE 0")

                txt_total_price.text = StringBuilder("LE ${(priceAll)}")
                txt_total_price_list.text = StringBuilder("LE ${(priceAll)}")

            }

        }

    }

    private fun reset(page: Int = 0) {

        btn_confirm.revertAnimation()

        setupAdapterTab(page)

        countAllPrice(0f, TYPE_ORDER.RESET)
        countAllOrder(0, TYPE_ORDER.RESET)
        listAllOrder(arrayListOf(), TYPE_ORDER.RESET)

        if (mSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            mSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        Handler().postDelayed({ swipe.isRefreshing = false }, 2000)

    }

    private fun setButtonConfirm(isClick: Boolean) {

        btn_confirm.isEnabled = isClick

        if (isClick) {
            btn_confirm.setBackgroundColor(Color.parseColor("#FF4322"))
        } else {
            btn_confirm.setBackgroundColor(Color.parseColor("#EEEEEE"))
        }
    }

    private fun getInvite() {

        val dialogBuilder = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.dialog_invite, null)

        val code = dialogView.find<TextView>(R.id.code)
        val submit = dialogView.find<CircularProgressButton>(R.id.submit)
        val close = dialogView.find<ImageView>(R.id.close)

        submit.setOnClickListener {

            ShareCompat.IntentBuilder
                    .from(this)
                    .setText("Get free LE 5 from kukus Cairo app when enter code ($referralId)\n\ndownload :\nhttps://play.google.com/store/apps/details?id=com.mynasmah.mykamus")
                    .setType("text/plain")
                    .setChooserTitle("Share with your friends")
                    .startChooser()
        }

        close.setOnClickListener {
            dialogBuilder.dismiss()
        }

        code.text = ("Your Code : $referralId")


        dialogBuilder.setView(dialogView)

        if (!(this as Activity).isFinishing) {
            dialogBuilder.show()
        }

    }

    private fun showMenu(id: Int, show: Boolean) {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.menu.findItem(id).isVisible = show
    }

    private fun getNewsAds(news: News) {

        val dialogBuilder = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.list_news, null)

        val close = dialogView.find<TextView>(R.id.close)
        val image = dialogView.find<ImageView>(R.id.image)

        close.setOnClickListener {
            dialogBuilder.dismiss()
        }

        if (news.image != "" && Constant.isValidContextForGlide(applicationContext)) {

            Glide
                    .with(applicationContext)
                    .load(news.image)
                    .into(image)

        }

        image.setOnClickListener {
            val intent = Intent(this, ActivityNewsDetails::class.java)
            intent.putExtra("id", news.id)
            intent.putExtra("image", news.image)
            intent.putExtra("date", news.date)
            startActivity(intent)
        }

        dialogBuilder.setView(dialogView)

        if (!(this as Activity).isFinishing) {
            dialogBuilder.show()
        }

    }

    @SuppressLint("InflateParams")
    private fun getRedeem() {

        val dialogBuilder = AlertDialog.Builder(this).create()
        val dialogView = layoutInflater.inflate(R.layout.dialog_redeem, null)

        val code = dialogView.find<EditText>(R.id.code)
        val submit = dialogView.find<CircularProgressButton>(R.id.submit)
        val close = dialogView.find<ImageView>(R.id.close)

        code.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (text.isNullOrEmpty()) {
                    submit.setBackgroundColor(Color.parseColor("#DDDDDD"))
                    submit.isEnabled = false
                } else {
                    submit.setBackgroundColor(Color.parseColor("#F44336"))
                    submit.isEnabled = true
                }
            }
        })

        close.setOnClickListener {
            dialogBuilder.dismiss()
        }

        submit.setOnClickListener {

            if (referral == "" && code.text.toString() != referralId) {

                submit.startAnimation()

                getUserRef.data().subscribe { userRef ->

                    var isExist = false

                    userRef?.children?.forEach {

                        val user = it.getValue(User::class.java)

                        if (user != null) {

                            if (user.refId == code.text.toString() && user.id != getUserId) {

                                toast("successfully add EGP 5 to Wallet")

                                addWalletToUser(user.id, 5f, "referral friend sign up")
                                addWalletToUser(getUserId, 5f, "get EGP 5 reward from code")
                                getUser(getUserId).child("referral").setValue(user.refId)

                                isExist = true

                            }
                        }
                    }

                    if (isExist) {

                        code.setTextColor(Color.parseColor("#32c360"))
                        submit.setBackgroundColor(Color.parseColor("#32c360"))

                        code.isEnabled = false
                        submit.isEnabled = false

                        Handler().postDelayed({
                            runOnUiThread {
                                submit.revertAnimation()
                                dialogBuilder.dismiss()
                            }
                        }, 2000)

                    } else {

                        Handler().postDelayed({
                            runOnUiThread {
                                toast("Code failed")
                                submit.revertAnimation()
                            }
                        }, 1500)

                    }


                }

            } else {

                toast("Code your failed")
            }

        }

        dialogBuilder.setView(dialogView)

        if (!(this as Activity).isFinishing) {
            dialogBuilder.show()
        }

    }

    private fun checkPromoFormUser(promoCheck: Promo): Boolean {

        if (promoCheck.active) {

            if (System.currentTimeMillis() < promoCheck.expireAt) {

                userPromo = promoCheck

                return true

            }
        }

        userPromo.reset()

        return false
    }

    override fun onBackPressed() {

        when {
            drawer_layout.isDrawerOpen(Gravity.START) -> {
                drawer_layout.closeDrawers()
            }
            mSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED -> {
                mSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            else -> {
                AlertDialog.Builder(this)
                        .setTitle("Really Exit?")
                        .setMessage("Are you sure you want to exit?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes) { _, _ ->

                            super.onBackPressed()
                            finish()
                        }
                        .create()
                        .show()
            }
        }
    }
}
