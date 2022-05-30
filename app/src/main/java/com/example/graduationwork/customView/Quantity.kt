package com.example.graduationwork.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.example.graduationwork.R


class Quantity @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val buttonPlus: ImageButton
    val buttonMinus: ImageButton
    val tvQuantity: TextView

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.view_quantity, this, true)

        buttonPlus = root.findViewById(R.id.button_plus)
        buttonMinus = root.findViewById(R.id.button_minus)
        tvQuantity = root.findViewById(R.id.quantity)
    }
}
