package com.example.graduationwork.data.entity

data class Order(
    var number: Int = 0,
    var pickup: Boolean = false,
    var name: String = "",
    var image: String = "",
    var amount: Int = 0,
    var quantity: Int = 0,
    var size: String = "",
    var userID: String = "",
    var date1: String = "",
    var date2: String = "",
    var address: String = ""
)
