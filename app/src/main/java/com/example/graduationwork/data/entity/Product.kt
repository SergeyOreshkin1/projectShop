package com.example.graduationwork.data.entity

data class Product(
    var name: String = "",
    var description: String = "",
    var image: String = "",
    var price: Int = 0,
    var type: List<String> = listOf(""),
    var id: String? = "",
    var images: List<String> = listOf(""),
    var details: String = ""
)
