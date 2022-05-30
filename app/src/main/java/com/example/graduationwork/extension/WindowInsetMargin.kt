package com.example.graduationwork.extension

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins

fun View.addSystemWindowInsetToMargin(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {
    val (initialLeft, initialTop, initialRight, initialBottom) =
        listOf(marginLeft, marginTop, marginRight, marginBottom)

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        view.updateLayoutParams {
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            (this as? ViewGroup.MarginLayoutParams)?.let {
                updateMargins(
                    left = initialLeft + (if (left) systemBarsInsets.left else 0),
                    top = initialTop + (if (top) systemBarsInsets.top else 0),
                    right = initialRight + (if (right) systemBarsInsets.right else 0),
                    bottom = initialBottom + (if (bottom) systemBarsInsets.bottom else 0)
                )
            }
        }
        insets
    }
}
