package com.example.graduationwork.screenState

import com.example.graduationwork.data.entity.Product

sealed class ScreenStateProduct {
    data class DataLoaded(val product: Product?) : ScreenStateProduct()
    object Loading : ScreenStateProduct()
    data class Error(val error: String) : ScreenStateProduct()
}
