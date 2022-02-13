package com.kukus.customer.dialog


import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.androidhuman.rxfirebase2.database.data
import com.kukus.customer.R
import com.kukus.customer.home.ActivityHome
import com.kukus.library.FirebaseUtils.Companion.getPromo
import com.kukus.library.FirebaseUtils.Companion.getPromoRef
import com.kukus.library.FirebaseUtils.Companion.getUser
import com.kukus.library.FirebaseUtils.Companion.getUserId
import com.kukus.library.model.Promo
import com.kukus.library.model.User
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.util.*


class DialogPromo : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewRoot = inflater.inflate(R.layout.dialog_promo, container, false)
        val code = viewRoot.find<EditText>(R.id.code)
        val close = viewRoot.find<ImageView>(R.id.close)
        val image = viewRoot.find<ImageView>(R.id.image_gift)
        val progress = viewRoot.find<ProgressBar>(R.id.progressbar_promo)
        val complate = viewRoot.find<ImageView>(R.id.image_complate)
        val valid = viewRoot.find<TextView>(R.id.valid)

        val codePromo = arguments?.getString("code") ?: ""
        val exprPromo = arguments?.getLong("date") ?: 0

        close.setOnClickListener {

            dismiss()
        }

        if (codePromo != "") {

            code.setText(codePromo)
            code.setBackgroundResource(R.drawable.shape_radius_green)

            if (exprPromo != 0.toLong()) {
                viewRoot.find<TextView>(R.id.remove).visibility = View.VISIBLE
            }

            if (exprPromo > 0) {

                val dateExp = DateFormat.format("dd-MM-yyyy", Date(exprPromo))

                valid.text = "Expired $dateExp"
                valid.visibility = View.VISIBLE
            }

        }

        code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                code.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                code.setBackgroundResource(R.drawable.shape_radius_grey)
                valid.visibility = View.GONE
                valid.text = ""
            }

        })

        code.filters = arrayOf<InputFilter>(InputFilter.AllCaps())

        viewRoot.find<TextView>(R.id.remove).setOnClickListener {


            AlertDialog.Builder(activity as ActivityHome)
                    .setTitle("Really Remove?")
                    .setMessage("Are you sure you want to remove?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes) { _, _ ->

                        getUser(getUserId).child("promoId").setValue("")
                        viewRoot.find<TextView>(R.id.remove).visibility = View.GONE

                        code.setBackgroundResource(R.drawable.shape_radius_grey)
                        code.setText("")
                        valid.visibility = View.GONE

                    }
                    .create()
                    .show()


        }

        viewRoot.find<Button>(R.id.submit).setOnClickListener {

            var found = false

            complate.visibility = View.GONE
            progress.visibility = View.VISIBLE
            image.visibility = View.GONE

            getPromo(code.text.toString()).data().subscribe({ promoFound ->

                if (promoFound.exists()) {

                    val promo = promoFound.getValue(Promo::class.java)

                    if (promo != null) {

                        if (promo.active) {

                            if (promo.expireAt > System.currentTimeMillis()) {

                                if (promo.currentLimit < promo.limit) {

                                    if (!promo.user.contains(getUserId)) {

                                        found = true

                                        promo.user.add(getUserId)

                                        promoFound.ref.child("currentLimit").setValue(promo.currentLimit.plus(1))
                                        promoFound.ref.child("user").setValue(promo.user)

                                        activity?.toast("Promo code is valid")

                                        Handler().postDelayed({

                                            progress.visibility = View.GONE
                                            complate.visibility = View.VISIBLE
                                            image.visibility = View.GONE

                                            code.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_white, 0, R.drawable.ic_done_green, 0)
                                            code.setBackgroundResource(R.drawable.shape_radius_green)

                                            val dateExp = DateFormat.format("dd-MM-yyyy", Date(promo.expireAt))

                                            valid.text = "Expired $dateExp"
                                            valid.visibility = View.VISIBLE


                                        }, 1000)

                                        Handler().postDelayed({

                                            dismiss()

                                        }, 2000)


                                    } else {

                                        activity?.toast("you have already promo...")

                                    }


                                } else {
                                    activity?.toast("promo has limit...")
                                }


                            } else {

                                activity?.toast("promo code expire...")

                            }

                        } else {

                            activity?.toast("promo code not active...")

                        }

                    }

                }


                if (!found) {

                    Handler().postDelayed({

                        progress.visibility = View.GONE
                        complate.visibility = View.GONE
                        image.visibility = View.VISIBLE

                        code.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_done_white, 0, R.drawable.ic_close, 0)
                        code.setBackgroundResource(R.drawable.shape_radius_red)

                        valid.visibility = View.VISIBLE
                        valid.text = "Promo code not valid"

                    }, 1000)

                }


            }) {}


        }

        return viewRoot
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

}