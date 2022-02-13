package com.kukus.customer.profile


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.androidhuman.rxfirebase2.database.data
import com.androidhuman.rxfirebase2.database.dataChanges
import com.kukus.customer.R
import com.kukus.customer.address.AddressAdapter
import com.kukus.customer.point.ActivityPoint
import com.kukus.customer.wallet.ActivityWallet
import com.kukus.library.Constant.Companion.EXTRA_USER_EMAIL
import com.kukus.library.Constant.Companion.EXTRA_USER_MOBILE
import com.kukus.library.Constant.Companion.EXTRA_USER_NAME
import com.kukus.library.Constant.Companion.EXTRA_USER_POINT
import com.kukus.library.Constant.Companion.EXTRA_USER_WALLET
import com.kukus.library.Constant.Companion.RESULT_MAP
import com.kukus.library.FirebaseUtils.Companion.getAddress
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Address
import com.kukus.library.model.User
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.toast


class ActivityProfile : AppCompatActivity() {

    private var mName       = ""
    private var mEmail      = ""
    private var mMobile     = ""
    private var mPoint      = 0
    private var mWallet     = 0f

    private lateinit var sharedPreferences : SharedPreferences
    lateinit var dialogEditProfile : Dialog

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        setContentView(R.layout.activity_profile)

        mName           = sharedPreferences.getString(EXTRA_USER_NAME,     mName)
        mEmail          = sharedPreferences.getString(EXTRA_USER_EMAIL,    mEmail)
        mMobile         = sharedPreferences.getString(EXTRA_USER_MOBILE,   mMobile)
        mPoint          = sharedPreferences.getInt(EXTRA_USER_POINT,   mPoint)
        mWallet         = sharedPreferences.getFloat(EXTRA_USER_WALLET,   mWallet)

        txt_name.text   = mName.toUpperCase()
        txt_email.text  = mEmail.toUpperCase()
        txt_phone.text  = mMobile

        txt_point.text   = ("$mPoint points")
        txt_wallet.text  = ("EGP $mWallet")

        initDialogEditProfile()

        layout_profile.setOnClickListener {

            dialogEditProfile.show()

        }
        layout_wallet.setOnClickListener {

            val intent = Intent(this, ActivityWallet::class.java)
            startActivity(intent)

        }
        layout_point.setOnClickListener {

            val intent = Intent(this, ActivityPoint::class.java)
            startActivity(intent)

        }

        //getAddressAdapter()

        getUser(getUserId).dataChanges().subscribe {
            if(it.exists()){

                val users = it.getValue(User::class.java)
                if(users != null) {

                    mName       = users.name
                    mEmail      = users.email
                    mMobile     = users.mobile
                    mWallet     = users.wallet
                    mPoint      = users.point

                    txt_name.text  = mName.toUpperCase()
                    txt_email.text = mEmail.toUpperCase()
                    txt_phone.text = mMobile

                    txt_point.text   = ("$mPoint points")
                    txt_wallet.text  = ("EGP $mWallet")

                    etName.setText(mName)
                    etEmail.setText(mEmail)
                    etPhone.setText(mMobile)

                    sharedPreferences.edit().putString(EXTRA_USER_NAME,     mName).apply()
                    sharedPreferences.edit().putString(EXTRA_USER_EMAIL,    mEmail).apply()
                    sharedPreferences.edit().putString(EXTRA_USER_MOBILE,   mMobile).apply()
                    sharedPreferences.edit().putInt(EXTRA_USER_POINT,   mPoint).apply()
                    sharedPreferences.edit().putFloat(EXTRA_USER_WALLET,   mWallet).apply()

                }
            }
        }
    }

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etPhone: EditText

    private fun initDialogEditProfile() {

        dialogEditProfile = Dialog(this, R.style.Theme_Dialog)

        dialogEditProfile.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val view = View.inflate(this, R.layout.dialog_edit_profile, null)

        dialogEditProfile.setContentView(view)

        etName = view.findViewById(R.id.et_title)
        etEmail = view.findViewById(R.id.et_email)
        etPhone = view.findViewById(R.id.et_phone)

        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressbar)

        btnSave.setOnClickListener {

            progressBar.visibility =  View.VISIBLE

            mName       = etName.text.toString()
            mEmail      = etEmail.text.toString()
            mMobile     = etPhone.text.toString()

            if(mName != "" || mMobile != "") {

                txt_name.text = mName
                txt_phone.text = mMobile
                txt_email.text = mEmail

                val update = HashMap<String, Any>()
                update["title"] = mName
                update["email"] = mEmail

                sharedPreferences.edit().putString(EXTRA_USER_NAME, mName).apply()
                sharedPreferences.edit().putString(EXTRA_USER_EMAIL, mEmail).apply()
                sharedPreferences.edit().putString(EXTRA_USER_MOBILE, mMobile).apply()

                getUser(getUserId).updateChildren(update).addOnCompleteListener {

                    Toast.makeText(this, "update successfully", Toast.LENGTH_LONG).show()

                    progressBar.visibility = View.GONE
                    dialogEditProfile.dismiss()
                }

            }else{
                progressBar.visibility = View.GONE
                toast("please field empty form")
            }
        }

        btnCancel.setOnClickListener {
            progressBar.visibility = View.GONE
            dialogEditProfile.dismiss()
        }

    }

    var addressList = arrayListOf<Address>()
    lateinit var adapter: AddressAdapter

    @SuppressLint("CheckResult")
    private fun getAddressAdapter(){

        //addressList.add(Address())
        adapter = AddressAdapter(addressList, this, 0)

        viewPager.adapter = adapter
        viewPager.setPadding(20, 0, 20, 0)

        getAddress(getUserId).data().subscribe{ it ->

            if (it.exists()) {

                addressList.clear()
                //addressList.add(Address())

                it.children.forEach { data ->

                    val address = data.getValue(Address::class.java)

                    if (address != null) {

                        addressList.add(address)

                    }
                }

                if (addressList.size > 0) {

                    adapter = AddressAdapter(addressList, this, 0)

                    viewPager.adapter = adapter
                    viewPager.currentItem = addressList.size - 1
                    viewPager.visibility = View.VISIBLE

                }
            }
        }
    }

    var lat     = ""
    var lng     = ""
    var address = ""
    var builder = ""
    var floor   = ""
    var apertment = ""
    var delivery = 0f
    var postion = 0

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == RESULT_MAP && resultCode == Activity.RESULT_OK) {

            if (data != null)
            {
                lat         = data.getStringExtra("lat")
                lng         = data.getStringExtra("lng")
                address     = data.getStringExtra("street")
                delivery    = data.getFloatExtra("delivery", 0f)

                builder     = data.getStringExtra("builder")
                floor       = data.getStringExtra("floor")
                apertment   = data.getStringExtra("apertment")

                addAddress()
            }
        }
    }


    fun selectAddress(position: Int) {

        postion = position
        adapter = AddressAdapter(addressList, this, position)

        delivery    = addressList[position].delivery
        lat         = addressList[position].lat
        lng         = addressList[position].lng
        address     = addressList[position].street
        builder     = addressList[position].building
        floor       = addressList[position].floor
        apertment   = addressList[position].apartment

        viewPager.adapter       = adapter
        viewPager.currentItem   = position

    }


    private fun addAddress()
    {

        addressList.add(Address(getUserId, address, builder, floor, apertment, lat, lng, delivery))

        postion = addressList.size - 1
        adapter = AddressAdapter(addressList, this, postion)

        delivery    = addressList[postion].delivery
        lat         = addressList[postion].lat
        lng         = addressList[postion].lng
        address     = addressList[postion].street
        builder     = addressList[postion].building
        floor       = addressList[postion].floor
        apertment   = addressList[postion].apartment

        viewPager.adapter       = adapter
        viewPager.currentItem   = postion
    }

    fun removeAddress(position: Int)
    {
        if (postion == position) {

            postion     = 0

            delivery    = 0f
            lat         = ""
            lng         = ""
            address     = ""
            builder     = ""
            floor       = ""
            apertment   = ""

        } else if (postion > position) {

            postion -= 1

        }

        addressList.removeAt(position)

        adapter = AddressAdapter(addressList, this, postion)

        viewPager.adapter       = adapter
        viewPager.currentItem   = postion

    }

}
