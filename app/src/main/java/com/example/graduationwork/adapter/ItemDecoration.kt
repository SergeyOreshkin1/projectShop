package com.example.graduationwork.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.example.graduationwork.R


class ItemDecorationProduct() : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {

        val space =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10.toFloat(),
                view.resources.displayMetrics
            ).toInt()

        for (pos in 0 until parent.size) {
            outRect.right = space
            outRect.left = 0
        }
    }
}

class ItemDecorationDivider(
    context: Context,
    private val paddingLeft: Int,
    private val paddingRight: Int
) : RecyclerView.ItemDecoration() {

    private var mDivider: Drawable? = null

    init {
        mDivider = ContextCompat.getDrawable(context, R.drawable.divider_catalog)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView) {

        val left = parent.paddingLeft + paddingLeft
        val right = parent.width - parent.paddingRight - paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + (mDivider?.intrinsicHeight ?: 0)

            mDivider?.let {
                it.setBounds(left, top, right, bottom)
                it.draw(c)
            }
        }
    }
}
