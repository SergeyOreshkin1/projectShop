package com.example.graduationwork.activity.catalog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import coil.load
import com.example.graduationwork.MapsActivity
import com.example.graduationwork.MapsAddressHouseActivity
import com.example.graduationwork.R
import com.example.graduationwork.activity.BaseActivity
import com.example.graduationwork.activity.profile.OrdersActivity
import com.example.graduationwork.data.entity.CountOrders
import com.example.graduationwork.data.local.SharedPreference
import com.example.graduationwork.databinding.ActivityOrderBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_order.textHouse
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
@DelicateCoroutinesApi
@InternalCoroutinesApi
class OrderActivity : BaseActivity() {

    private lateinit var binding: ActivityOrderBinding

    override fun onRestart() {
        super.onRestart()
        val sharedPreference = SharedPreference(this)
        val addressFromMap: String? = sharedPreference.getValueString("address")
        val addressHouseFromMap: String? =
            sharedPreference.getValueString(resources.getString(R.string.addressHouse))
        binding.textAdres.setText(addressFromMap)
        binding.textHouse.setText(addressHouseFromMap)

    }


    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            if (it.value) {
                val intent = Intent(this@OrderActivity, MapsAddressHouseActivity::class.java)
                startActivity(intent)
            }
        }
    }

       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)

        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("title")
        val price = intent.getIntExtra("price", 1300)
        val type = intent.getStringExtra("size")
        val image = intent.getStringExtra("image")
        var newPrice = price


        if (image != null) {
            binding.imagePreview.load(image) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
        } else {
            binding.imagePreview.load(R.drawable.placeholder) {
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
        }
        val titleOrder = type + " " + getString(R.string.circle) + " " + name
        binding.textTitle.text = titleOrder
        var quantity = 1
        var buttonText =
            getString(R.string.buy_for) + " " + "$price" + " " + getString(R.string.rub)
        binding.buttonBuyOrder.text = buttonText

        val datePicker = selectedDate()

        binding.textDate.setOnClickListener {
            datePicker.show(supportFragmentManager, datePicker.toString())
        }
        datePicker.addOnPositiveButtonClickListener {
            binding.textDate.setText(datePicker.headerText)
        }

        binding.textDate.inputType = InputType.TYPE_NULL
        binding.quantityView.tvQuantity.text = quantity.toString()

        binding.quantityView.buttonPlus.setOnClickListener {
            quantity = incrementQuantity(quantity)
            binding.quantityView.tvQuantity.text = quantity.toString()
            if (price != null) {
                newPrice = quantity * price
            }
            buttonText =
                getString(R.string.buy_for) + " " + "$newPrice" + " " + getString(R.string.rub)
            binding.buttonBuyOrder.text = buttonText
        }
        binding.quantityView.buttonMinus.setOnClickListener {
            quantity = decrementQuantity(quantity)
            binding.quantityView.tvQuantity.text = quantity.toString()
            if (price != null) {
                newPrice = quantity * price
            }
            buttonText =
                getString(R.string.buy_for) + " " + "$newPrice" + " " + getString(R.string.rub)
            binding.buttonBuyOrder.text = buttonText
        }
        binding.textAdres.setOnClickListener {

            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        binding.textHouse.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this@OrderActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this@OrderActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    val intent = Intent(this@OrderActivity, MapsAddressHouseActivity::class.java)
                    startActivity(intent)

                }
                else -> {
                    val permissions = arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                    requestMultiplePermissions.launch(
                        permissions
                    )

                }
            }
        }

        var address = ""
        var house = ""
        var apartment = ""
        var pickup = false

        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.layoutApartment.visibility = View.GONE
                binding.layoutTownStreetHouse.visibility = View.GONE
                binding.layoutAdres.visibility = View.VISIBLE

                pickup = true

            } else {
                binding.layoutApartment.visibility = View.VISIBLE
                binding.layoutTownStreetHouse.visibility = View.VISIBLE
                binding.layoutAdres.visibility = View.GONE

                pickup = false
            }
        }

        binding.buttonBuyOrder.setOnClickListener {

            val date = binding.textDate.text.toString()

            if (pickup) {
                address = binding.textAdres.text.toString()
            } else {
                house = binding.textHouse.text.toString()
                apartment = binding.textApartment.text.toString()
                address = if (house != "" && apartment != "") {
                    "$house, кв. $apartment"
                } else ""
            }

            if (date == "" || address == "") {

                val sb = Snackbar.make(findViewById(android.R.id.content),getString(R.string.order_error), Snackbar.LENGTH_LONG)
                sb.view.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.snackBarError
                    )
                )
                sb.anchorView = binding.buttonBuyOrder
                sb.show()
            }
            else {
                order(name, newPrice, image, type, quantity, date, address, pickup)
            }
        }
    }

    private suspend fun getCountOrders(): Int? {

        val db = FirebaseFirestore.getInstance()
        val doc = db.collection("orders").document("countOrders").get().await()
            .toObject(CountOrders::class.java)
        return doc?.count
    }

    private fun order(
        name: String?, amount: Int, image: String?, size: String?,
        quantity: Int, date2: String, address: String, pickup: Boolean
    ) {
        showProgressDialog(resources.getString(R.string.wait))

        val sharedPreference = SharedPreference(this)
        val uid: String = sharedPreference.getValueString("UID").toString()

        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance()
        val formatedDate = formatter.format(date)

        GlobalScope.launch {

            val orderNumber = getCountOrders()?.plus(1)

            val db = FirebaseFirestore.getInstance()

            db.collection("orders").document("$orderNumber")
                .set(
                    hashMapOf(
                        "number" to orderNumber,
                        "userID" to uid,
                        "name" to name,
                        "amount" to amount,
                        "image" to image,
                        "size" to size,
                        "quantity" to quantity,
                        "date1" to formatedDate,
                        "date2" to date2,
                        "address" to address,
                        "pickup" to pickup

                    )
                ).addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        db.collection("orders").document("countOrders")
                            .update("count", FieldValue.increment(1))
                            .addOnCompleteListener { task ->

                                hideProgressDialog()

                                if (task.isSuccessful) {

                                    Toast.makeText(
                                        this@OrderActivity,
                                        resources.getString(R.string.order_success),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    startActivity(
                                        Intent(
                                            this@OrderActivity,
                                            OrdersActivity::class.java
                                        )
                                    )

                                } else {
                                    showSnackBar(task.exception!!.message.toString(), true)
                                }
                            }

                    } else {
                        showSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    private fun selectedDate(): MaterialDatePicker<Long> {
        Locale.setDefault(Locale("RU"))
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

        return MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.pick_date)
            .setCalendarConstraints(constraintsBuilder.build())
            .setTheme(R.style.DatePickerDialog)
            .build()
    }

    private fun incrementQuantity(quantity: Int): Int {
        var quantity = quantity
        if (quantity < 5) {
            quantity += 1
        }
        return quantity
    }

    private fun decrementQuantity(quantity: Int): Int {
        var quantity = quantity
        if (quantity > 1) {
            quantity -= 1
        }
        return quantity
    }
}
