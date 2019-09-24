package com.ickhov.pinnumberedittext

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.content.res.ColorStateList
import android.graphics.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat


class PinNumberEditText: AppCompatEditText {

    var mSpace = 16f                                    // can't be changed
    var mCharSize = 0f                                  // can't be changed
    var mNumChars = 4f                                  // 4 boxes by default
    var mLineSpacing = 8f                               // can't be changed
    var mRoundedRadius = 8f                             // 8dp by default
    private var mClickListener: OnClickListener? = null // can't be changed

    private var mStates = arrayOf(
        intArrayOf(android.R.attr.state_selected),      // selected
        intArrayOf(android.R.attr.state_focused),       // focused
        intArrayOf(-android.R.attr.state_focused)       // unfocused
    )

    private var mColors = intArrayOf(
        Color.GRAY,                                     // selected
        Color.WHITE,                                    // focused
        Color.WHITE                                     // unfocused
    )

    private var mColorStates = ColorStateList(
        mStates,
        mColors
    )

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun init(context: Context) {
        setBackgroundResource(0)
        inputType = InputType.TYPE_CLASS_NUMBER
        height = 400

        isCursorVisible = false

        val multi = context.resources.displayMetrics.density
        mSpace *= multi //convert to pixels for our density
        mLineSpacing *= multi
        mRoundedRadius *= multi

        //Disable copy && paste
        super.setCustomSelectionActionModeCallback(
            object : ActionMode.Callback {
                override fun onPrepareActionMode(
                    mode: ActionMode,
                    menu: Menu
                ): Boolean {
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode) {}

                override fun onCreateActionMode(
                    mode: ActionMode,
                    menu: Menu
                ): Boolean {
                    return false
                }

                override fun onActionItemClicked(
                    mode: ActionMode,
                    item: MenuItem
                ): Boolean {
                    return false
                }
            })

        //When tapped, move cursor to end of the text
        super.setOnClickListener { v ->
            text?.let {
                setSelection(it.length)
            }
            mClickListener?.onClick(v)
        }

    }

    override fun setOnClickListener(listener: OnClickListener?) {
        mClickListener = listener
    }

    override fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback?) {
        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
    }

    override fun onDraw(canvas: Canvas?) {
        val availableWidth = width - paddingRight - paddingLeft

        mCharSize = when (mSpace < 0) {
            true -> availableWidth / (mNumChars * 2 - 1)
            false -> (availableWidth - mSpace * (mNumChars - 1)) / mNumChars
        }

        var startX = paddingLeft.toFloat()
        val startY = paddingTop.toFloat()
        val height = height.toFloat()

        //Text Width
        val text = text
        val textLength = text!!.length
        val textWidths = FloatArray(textLength)

        paint.getTextWidths(text, 0, textLength, textWidths)

        for (i in 0 until mNumChars.toInt()) {

            updateFillColor(i == textLength)
            canvas!!.drawRoundRect(
                startX,
                startY,
                startX + mCharSize,
                height,
                mRoundedRadius,
                mRoundedRadius,
                paint)

            if (text.length > i) {
                paint.color = Color.BLACK
                val middle = startX + mCharSize / 2
                canvas.drawText(
                    text,
                    i,
                    i + 1,
                    middle - textWidths[i] / 2,
                    height / 2 + textWidths[i],
                    paint
                )
            }

            startX += when (mSpace < 0) {
                true -> mCharSize * 2
                false -> mCharSize + mSpace
            }
        }
    }

    private fun getColorForState(vararg states: Int): Int {
        return mColorStates.getColorForState(states, Color.GRAY)
    }

    private fun updateFillColor(next: Boolean) {
        if (isFocused) {
            paint.color = getColorForState(android.R.attr.state_focused)
            if (next) {
                paint.color = getColorForState(android.R.attr.state_selected)
            }
        } else {
            paint.color = getColorForState(-android.R.attr.state_focused)
        }
    }






}