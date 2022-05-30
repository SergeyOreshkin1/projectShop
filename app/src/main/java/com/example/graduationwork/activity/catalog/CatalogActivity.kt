package com.example.graduationwork.activity.catalog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ActivityCatalogBinding
import com.example.graduationwork.activity.profile.ProfileActivity
import com.example.graduationwork.adapter.ItemDecorationDivider
import com.example.graduationwork.adapter.ProductAdapter
import com.example.graduationwork.data.entity.Product
import com.example.graduationwork.extension.addSystemWindowInsetToMargin
import com.example.graduationwork.extension.onClickFlow
import com.example.graduationwork.extension.onRefreshFlow
import com.example.graduationwork.screenState.ScreenStateCatalog
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

private val productsAdapter by lazy { ProductAdapter() }

@InternalCoroutinesApi
class CatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogBinding

    private var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)
        fitContentViewToInsets()

        binding.recyclerView.addItemDecoration(
            ItemDecorationDivider(
                this,
                resources.getDimension(R.dimen.item_catalog_margin).toInt(),
                resources.getDimension(R.dimen.item_catalog_margin).toInt()
            )
        )
        binding.appbarLayout.addSystemWindowInsetToMargin(
            top = true,
            left = true, right = true
        )

        binding.refreshLayout.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.primary
            )
        )

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.goToProfile -> {
                    startActivity(
                        Intent(
                            this@CatalogActivity, ProfileActivity::class.java
                        )
                    )
                    finish()
                }
            }
            true
        }

        binding.toolbar.setNavigationOnClickListener { finish() }


        when (intent.getStringExtra("category")) {
            "1" -> {
                category = "muscle"
            }
            "2" -> {
                category = "weightLoss"
            }
            "3" -> {
                category = "immunity"
            }
            "4" -> {
                category = "sale"
            }

        }

        merge(
            flowOf(Unit),
            binding.refreshLayout.onRefreshFlow(),
            binding.buttonRefresh.onClickFlow()
        ).flatMapLatest { loadProducts(category) }
            .distinctUntilChanged()
            .onEach {
                when (it) {
                    is ScreenStateCatalog.DataLoaded -> {
                        setLoading(false)
                        setError(null)
                        setData(it.products)
                    }
                    is ScreenStateCatalog.Error -> {
                        setLoading(false)
                        setError(it.error)
                        setData(null)
                    }
                    is ScreenStateCatalog.Loading -> {
                        setLoading(true)
                        setError(null)
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun setLoading(isLoading: Boolean) = with(binding) {
        refreshLayout.isRefreshing = isLoading && recyclerView.isVisible
    }

    private fun setData(products: List<Product>?) = with(binding) {
        refreshLayout.isVisible = products != null
        recyclerView.layoutManager = LinearLayoutManager(this@CatalogActivity)
        recyclerView.adapter = productsAdapter
        productsAdapter.submitList(products ?: emptyList())
        productsAdapter.listener = {

            val intent = Intent(this@CatalogActivity, ProductActivity::class.java)
            intent.putExtra("id", it.id)
            intent.putExtra("title", it.name)
            intent.putExtra("category", category)
            startActivity(intent)

        }
    }

    private fun setError(message: String?) = with(binding) {
        errorLayout.isVisible = message != null
        tvError.text = message
    }

    private fun loadProducts(category: String) = flow {
        emit(ScreenStateCatalog.Loading)
        val products = getListOfProducts(category)
        emit(ScreenStateCatalog.DataLoaded(products))
    }.catch {
        emit(ScreenStateCatalog.Error(getString(R.string.error)))
    }

    private suspend fun getListOfProducts(category: String): MutableList<Product> {
        val products = mutableListOf<Product>()
        val db = FirebaseFirestore.getInstance()

        val productsIds = mutableListOf<String>()

        db.collection(category).whereEqualTo("category", category).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    productsIds.add(document.data["id"].toString())
                }
            }
            .addOnFailureListener { exception ->
            }.await()

        for (i in productsIds.indices) {
            val docRef = db.collection(category).document(productsIds[i])
            docRef.get().await().toObject(Product::class.java)?.let { products.add(it) }
        }

        return products
    }

    private fun fitContentViewToInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}