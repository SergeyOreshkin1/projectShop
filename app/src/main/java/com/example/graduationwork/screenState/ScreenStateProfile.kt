package com.example.graduationwork.screenState

sealed class ScreenStateProfile {
    data class DataLoaded(val name: String, val image: String) : ScreenStateProfile()
    object Loading : ScreenStateProfile()
    data class Error(val error: String) : ScreenStateProfile()
}
