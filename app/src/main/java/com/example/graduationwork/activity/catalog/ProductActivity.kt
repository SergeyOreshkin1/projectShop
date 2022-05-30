package com.example.graduationwork.activity.catalog

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ActivityProductBinding
import com.example.graduationwork.adapter.ItemDecorationProduct
import com.example.graduationwork.data.entity.Product
import com.example.graduationwork.adapter.ImagesProductAdapter
import com.example.graduationwork.screenState.ScreenStateProduct
import com.example.graduationwork.data.entity.Type
import com.example.graduationwork.adapter.TypeAdapter
import com.example.graduationwork.adapter.ViewPagerAdapter
import com.example.graduationwork.extension.addSystemWindowInsetToMargin
import com.example.graduationwork.extension.onClickFlow
import com.google.android.material.bottomsheet.BottomSheetDialog
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

@InternalCoroutinesApi
class ProductActivity : AppCompatActivity() {
    private lateinit var title: String
    private lateinit var category: String
    private lateinit var preview: String
    private lateinit var productId: String
    private var typeList: MutableList<Type> = mutableListOf()
    private val typeAdapter by lazy { TypeAdapter() }
    private lateinit var imagesList: MutableList<String>
    private lateinit var binding: ActivityProductBinding

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewProduct.addItemDecoration(ItemDecorationProduct())

        fitContentViewToInsets()
        binding.appbarLayoutProduct.addSystemWindowInsetToMargin(
            top = true,
            left = true, right = true
        )
        binding.layoutProduct.addSystemWindowInsetToMargin(left = true, right = true)
        binding.recyclerViewProduct.addSystemWindowInsetToMargin(bottom = true)
        binding.layoutScrollView.addSystemWindowInsetToMargin(bottom = true)
        binding.viewPager2.addSystemWindowInsetToMargin(bottom = true)
        binding.buttonBuy.addSystemWindowInsetToMargin(bottom = true)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)

        productId = intent.getStringExtra("id").toString()
        title = intent.getStringExtra("title").toString()
        category = intent.getStringExtra("category").toString()

        binding.scrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY < 430 * resources.displayMetrics.density) {
                    binding.tvToolbar.text = " "
                }

                if (scrollY >= 430 * resources.displayMetrics.density) {
                    binding.tvToolbar.text = binding.textTitle.text
                }
            }
        )

        binding.toolbar.setNavigationOnClickListener {

          finish()
        }

        binding.textType.inputType = InputType.TYPE_NULL
        merge(
            flowOf(Unit),
            binding.buttonRefresh.onClickFlow()
        ).flatMapLatest { loadProduct(productId, category) }
            .distinctUntilChanged()
            .onEach {
                when (it) {
                    is ScreenStateProduct.DataLoaded -> {
                        setLoading(false)
                        setError(null)
                        setData(it.product)
                    }
                    is ScreenStateProduct.Error -> {
                        setLoading(false)
                        setError(it.error)
                        setData(null)
                    }
                    is ScreenStateProduct.Loading -> {
                        setLoading(true)
                        setError(null)
                    }
                }
            }.launchIn(lifecycleScope)

    }

    private fun typeBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewBottomSheet)
        rv.apply {
            layoutManager = LinearLayoutManager(
                this@ProductActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = typeAdapter
            typeAdapter.submitList(typeList)

            typeAdapter.listener = {
                binding.textType.setText(it.value)
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun setLoading(isLoading: Boolean) = with(binding) {
        progressBar.isVisible = isLoading && !layoutProduct.isVisible
    }

    private fun setData(product: Product?) = with(binding) {
        layoutProduct.isVisible = product != null
        binding.layoutType.visibility = View.VISIBLE
        binding.buttonBuy.visibility = View.VISIBLE

        if (product != null) {
            preview = product.image
        }
        val details = product?.details
        val textDetails = details?.replace("_n", "\n")
        binding.textDetails.text = textDetails

        val price = product?.price
        binding.textTitle.text = product?.name
        val priceText = price.toString() + " " + getString(R.string.rub)
        binding.textPrice.text = priceText
        binding.textDescription.text = product?.description
        imagesList = mutableListOf()
        if (product?.images?.size != null) {
            for (i in product.images.indices) {
                imagesList.add(i, product.images[i])
            }
        }

        binding.textType.setOnClickListener {
            typeBottomSheetDialog()
        }
        if (product != null) {
            for (i in product.type.indices) {
                typeList.add(Type(product.type[i]))
            }
        }
        val size = intent.getStringExtra("size")
        if (size != null) {
            binding.textType.setText(size)
        } else if (typeList.isNotEmpty()) {
            binding.textType.setText(typeList[0].value)
        }

        binding.recyclerViewProduct.apply {
            layoutManager = LinearLayoutManager(
                this@ProductActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            (layoutManager as LinearLayoutManager).scrollToPosition(
                Integer.MAX_VALUE / 2 + 1
            )

            adapter = ImagesProductAdapter(imagesList)

        }
        binding.recyclerViewProduct.suppressLayout(true)

        val adapterVp = ViewPagerAdapter(imagesList)
        binding.viewPager2.adapter = adapterVp
        binding.viewPager2.setCurrentItem(Integer.MAX_VALUE / 2 + 1, false)
        var newPos = Integer.MAX_VALUE / 2 + 1

        binding.viewPager2.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position < newPos) {
                        binding.recyclerViewProduct.suppressLayout(false)
                        binding.recyclerViewProduct.scrollToPosition(
                            newPos - 1
                        )
                        binding.recyclerViewProduct.suppressLayout(true)
                        newPos -= 1
                    } else if (position > newPos) {
                        binding.recyclerViewProduct.suppressLayout(false)
                        binding.recyclerViewProduct.scrollBy(
                            42.toDp(this@ProductActivity), 0
                        )
                        binding.recyclerViewProduct.suppressLayout(true)
                        newPos += 1
                    }
                }
            })

        binding.buttonBuy.setOnClickListener {
            val intent = Intent(this@ProductActivity, OrderActivity::class.java)
            intent.putExtra("title", product?.name)
            intent.putExtra("size", binding.textType.text.toString())
            intent.putExtra("image", preview)
            intent.putExtra("price", product?.price)
            intent.putExtra("id", productId)
            startActivity(intent)

        }
    }

    private fun setError(message: String?) = with(binding) {
        errorLayout.isVisible = message != null
        tvError.text = message

    }

    private fun loadProduct(id: String, category: String) = flow {
        emit(ScreenStateProduct.Loading)
        val product = getProduct(id, category)
        emit(ScreenStateProduct.DataLoaded(product))
    }.catch {
        emit(ScreenStateProduct.Error(getString(R.string.error)))
    }

    private suspend fun getProduct(id: String, category: String): Product? {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(category).document(id)

        return docRef.get().await().toObject(Product::class.java)
    }

    private fun Int.toDp(context: Context): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
    ).toInt()

    private fun fitContentViewToInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}