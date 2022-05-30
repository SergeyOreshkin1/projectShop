package com.example.graduationwork.screenState

import com.example.graduationwork.data.entity.Product

sealed class ScreenStateCatalog {
    data class DataLoaded(val products: List<Product>) : ScreenStateCatalog()
    object Loading : ScreenStateCatalog()
    data class Error(val error: String) : ScreenStateCatalog()
}
