package com.example.graduationwork.extension

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

fun SwipeRefreshLayout.onRefreshFlow(): SharedFlow<Unit>{
    val sharedFlow = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1
    )
    setOnRefreshListener {
        sharedFlow.tryEmit(Unit)
    }
    return sharedFlow.asSharedFlow()

}
