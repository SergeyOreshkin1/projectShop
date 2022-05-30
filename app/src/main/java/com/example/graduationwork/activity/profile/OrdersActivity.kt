package com.example.graduationwork.activity.profile

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.graduationwork.R
import com.example.graduationwork.activity.BaseActivity
import com.example.graduationwork.activity.catalog.CategoriesActivity
import com.example.graduationwork.adapter.ItemDecorationDivider
import com.example.graduationwork.adapter.OrderAdapter
import com.example.graduationwork.data.entity.Order
import com.example.graduationwork.data.local.SharedPreference
import com.example.graduationwork.databinding.ActivityOrdersBinding
import com.example.graduationwork.extension.addSystemWindowInsetToMargin
import com.example.graduationwork.extension.onClickFlow
import com.example.graduationwork.extension.onRefreshFlow
import com.example.graduationwork.screenState.ScreenStateOrders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await

private val ordersAdapter by lazy { OrderAdapter() }
@InternalCoroutinesApi
class OrdersActivity : BaseActivity() {

    override fun onBackPressed() {
        val intent = Intent(this, CategoriesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private lateinit var binding: ActivityOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ordersAdapter.listener = { order, position ->

            val number = "${order.number}"
            alertDialog(number, position)
        }

        binding.recyclerView.addItemDecoration(
            ItemDecorationDivider(
                this,
                resources.getDimension(R.dimen.item_catalog_margin).toInt(),
                resources.getDimension(R.dimen.item_catalog_margin).toInt()
            )
        )

        fitContentViewToInsets()
        binding.appbarLayoutOrders.addSystemWindowInsetToMargin(
            top = true,
            left = true, right = true
        )

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.goToProfile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                }
            }
            true
        }

        binding.buttonBuy.setOnClickListener {

            val intent = Intent(this, CategoriesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)


        }

        merge(
            flowOf(Unit),
            binding.refreshLayout.onRefreshFlow(),
            binding.buttonRefresh.onClickFlow(),
        ).flatMapLatest { loadOrders() }
            .distinctUntilChanged()
            .onEach {
                when (it) {
                    is ScreenStateOrders.DataLoaded -> {
                        setLoading(false)
                        setError(null)
                        setData(it.orders)
                    }
                    is ScreenStateOrders.Error -> {
                        setLoading(false)
                        setError(it.error)
                        setData(null)
                    }
                    is ScreenStateOrders.Loading -> {
                        setLoading(true)
                        setError(null)
                    }
                }
            }.launchIn(lifecycleScope)

    }

    private fun alertDialog(number: String, position: Int) {
        val dialog =
            MaterialAlertDialogBuilder(this, R.style.MyThemeOverlay_MaterialAlertDialog)

        dialog.setMessage(getString(R.string.dialog_message_order) + number + " ?")

        dialog.setPositiveButton(R.string.yes) { _, _ ->
            val db = FirebaseFirestore.getInstance()
            db.collection("orders")
                .document(number).delete()
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                                    val sb = Snackbar.make(
                                        findViewById(android.R.id.content),
                                        getString(R.string.order_delete),
                                        Snackbar.LENGTH_LONG
                                    )
                                    sb.view.setBackgroundColor(
                                        ContextCompat.getColor(
                                            this,
                                            R.color.status_bar
                                        )
                                    )
                                    sb.show()
                                    ordersAdapter.deleteItem(position)

                    }
                }
        }

        dialog.setNegativeButton(R.string.no) { dialog, _ ->
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun loadOrders() = flow {
        emit(ScreenStateOrders.Loading)
        val orders = getListOfOrders()
        emit(ScreenStateOrders.DataLoaded(orders))
    }.catch {
        emit(ScreenStateOrders.Error(getString(R.string.error)))
    }

    private suspend fun getListOfOrders(): MutableList<Order> {
        val orders = mutableListOf<Order>()
        val db = FirebaseFirestore.getInstance()

        val ordersIds = mutableListOf<String>()

        val sharedPreference = SharedPreference(this)
        val uid: String? = sharedPreference.getValueString("UID")

        db.collection("orders").whereEqualTo("userID", uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    ordersIds.add(document.data["number"].toString())
                }
            }
            .addOnFailureListener { exception ->
            }.await()

        for (i in ordersIds.indices) {
            val docRef = db.collection("orders").document(ordersIds[i])
            docRef.get().await().toObject(Order::class.java)?.let { orders.add(it) }
        }

        return orders
    }

    private fun setLoading(isLoading: Boolean) = with(binding) {
        refreshLayout.isRefreshing = isLoading && recyclerView.isVisible
    }

    private fun setData(orders: List<Order>?) = with(binding) {
        refreshLayout.isVisible = orders != null
        recyclerView.layoutManager = LinearLayoutManager(this@OrdersActivity)
        recyclerView.adapter = ordersAdapter
        ordersAdapter.submitList(orders ?: emptyList())

        if (orders != null) {
            if (orders.isEmpty()) {

                    errorLayout.isVisible = true
                    refreshLayout.isVisible = false
                    tvError.text = "Вы еще ничего не заказали"
                    buttonRefresh.isVisible = false

            }

        }
    }

    private fun setError(message: String?) = with(binding) {
        errorLayout.isVisible = message != null
        tvError.text = message
    }

    private fun fitContentViewToInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}
