package org.screens.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.LinearLayout
import com.easydictionary.app.R

public class CheckableLinearLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), Checkable {
    init {
        setChecked(false)
    }

    private var mChecked = false

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun setChecked(b: Boolean) {
        if (b != mChecked) {
            mChecked = b
            refreshDrawableState()
        }
    }

    override fun toggle() {
        setChecked(!mChecked)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super<LinearLayout>.onCreateDrawableState(extraSpace + 1)
        if (isChecked()) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    override fun drawableStateChanged() {
        super<LinearLayout>.drawableStateChanged()
        invalidate()
    }

    companion object {
        private val CHECKED_STATE_SET = intArray(android.R.attr.state_checked)
    }
}