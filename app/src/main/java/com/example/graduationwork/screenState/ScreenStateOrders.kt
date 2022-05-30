package com.example.graduationwork.screenState

import com.example.graduationwork.data.entity.Order

sealed class ScreenStateOrders {
    data class DataLoaded(val orders: List<Order>) : ScreenStateOrders()
    object Loading : ScreenStateOrders()
    data class Error(val error: String) : ScreenStateOrders()
}
