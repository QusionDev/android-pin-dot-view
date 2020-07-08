package com.qusion.lib_pindotview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.number_dial_view.view.*

typealias OnNumberClickListener = (Int)->Unit
typealias OnNumberRemovedListener = ()->Unit
typealias OnBiometricsClickedListener = ()->Unit

class NumberDialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var mTextSize = 12
    private var mTextColor = 0
    private var mTextStyle = 0
    private var mBackgroundColor = 0
    private var mDelimiterColor = 0
    private var mBiometricsTintColor = 0
    private var mVerticalDelimiterWidth = 0
    private var mHorizontalDelimiterWidth = 0

    private val numberDialView: View
    private val numbers: List<TextView>
    private val verticalDelimiters: List<View>
    private val horizontalDelimiters: List<View>

    private var numbersEntered = 0
    private var backVisible = false

    private var mOnNumberClickListener: OnNumberClickListener? = null
    private var mOnNumberRemovedListener: OnNumberRemovedListener? = null
    private var mOnBiometricsClickedListener: OnBiometricsClickedListener? = null

    init {
        val a: TypedArray = if (attrs != null) {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.NumberDial,
                0, 0
            )
        } else {
            throw IllegalArgumentException("The attributes need to be passed")
        }
        try {
            mTextSize = a.getDimensionPixelSize(R.styleable.NumberDial_textSize, 12)
            mTextColor = a.getColor(
                R.styleable.NumberDial_textColor,
                context.getColor(R.color.default_text_color)
            )
            mTextStyle = a.getInteger(R.styleable.NumberDial_textStyle, 0)
            mBackgroundColor = a.getColor(
                R.styleable.NumberDial_backgroundColor,
                context.getColor(R.color.transparent_color)
            )
            mDelimiterColor = a.getColor(
                R.styleable.NumberDial_delimiterColor,
                context.getColor(R.color.number_dial_view_delimiter_color)
            )
            mBiometricsTintColor = a.getColor(
                R.styleable.NumberDial_biometricsTint,
                context.getColor(R.color.number_dial_view_biometrics_color)
            )
            mVerticalDelimiterWidth = a.getDimensionPixelSize(R.styleable.NumberDial_verticalDelimiterWidth, 1)
            mHorizontalDelimiterWidth = a.getDimensionPixelSize(R.styleable.NumberDial_horizontalDelimiterWidth, 1)

        } finally {
            a.recycle()
        }

        numberDialView = View.inflate(context, R.layout.number_dial_view, this) as NumberDialView

        numbers = listOf(
            numberDialView.zero,
            numberDialView.one,
            numberDialView.two,
            numberDialView.three,
            numberDialView.four,
            numberDialView.five,
            numberDialView.six,
            numberDialView.seven,
            numberDialView.eight,
            numberDialView.nine)

        verticalDelimiters = listOf(
            numberDialView.vertical_one,
            numberDialView.vertical_two
        )

        horizontalDelimiters = listOf(
            numberDialView.horizontal_one,
            numberDialView.horizontal_two,
            numberDialView.horizontal_three
        )

        updateView()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        invalidate()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        updateView()
    }

    private fun updateView() {
        numberDialView.container.setBackgroundColor(mBackgroundColor)

        numbers.forEach {number ->
            number.apply {
                setTextColor(mTextColor)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize.toFloat())
                typeface = Typeface.defaultFromStyle(mTextStyle)
                setOnClickListener { view ->
                    numbersEntered += 1
                    toggleBackButton(true)
                    mOnNumberClickListener?.invoke(numbers.indexOf(view))
                }
            }
        }

        numberDialView.bottomRightBiometricsIcon.setColorFilter(mBiometricsTintColor)

        numberDialView.bottomRightButton.setOnClickListener {
            if(backVisible) {
                numbersEntered -= 1
                if(numbersEntered == 0) toggleBackButton(false)
                mOnNumberRemovedListener?.invoke()
            } else {
                mOnBiometricsClickedListener?.invoke()
            }
        }

        verticalDelimiters.forEach { delimiter ->
            delimiter.apply {
                val params = layoutParams
                params.width = mVerticalDelimiterWidth
                layoutParams = params
                setBackgroundColor(mDelimiterColor)
            }
        }

        horizontalDelimiters.forEach { delimiter ->
            delimiter.apply {
                val params = layoutParams
                params.height = mHorizontalDelimiterWidth
                layoutParams = params
                setBackgroundColor(mDelimiterColor)
            }
        }
    }

    private fun toggleBackButton(visible: Boolean) {
        if(visible) {
            backVisible = true
            bottomRightBiometricsIcon.visibility = View.GONE
            bottomRighBackIcon.visibility = View.VISIBLE
        } else {
            backVisible = false
            bottomRightBiometricsIcon.visibility = View.VISIBLE
            bottomRighBackIcon.visibility = View.GONE
        }
    }

    fun clear() {
        numbersEntered = 0
        toggleBackButton(false)
    }

    //region Setters
    fun setOnNumberClickListener(l: OnNumberClickListener) {
        mOnNumberClickListener = l
    }

    fun setOnNumberRemovedListener(l: OnNumberRemovedListener) {
        mOnNumberRemovedListener = l
    }

    fun setOnBiometricsClickedListener(l: OnBiometricsClickedListener) {
        mOnBiometricsClickedListener = l
    }

    var textSize: Int
        get() = mTextSize
        set(textSize) {
            this.mTextSize = textSize
            invalidate()
        }

    var textColor: Int
        get() = mTextColor
        set(textColor) {
            this.mTextColor = textColor
            invalidate()
        }

    var textStyle: Int
        get() = mTextStyle
        set(textStyle) {
            this.mTextStyle = textStyle
            invalidate()
        }

    override fun setBackgroundColor(mBackgroundColor: Int) {
        this.mBackgroundColor = mBackgroundColor
        invalidate()
    }

    fun getBackgroundColor(): Int {
        return mBackgroundColor
    }

    var delimiterColor: Int
        get() = mDelimiterColor
        set(delimiterColor) {
            this.mDelimiterColor = delimiterColor
        }

    var verticalDelimiterWidth: Int
        get() = mVerticalDelimiterWidth
        set(verticalDelimiterWidth) {
            this.mVerticalDelimiterWidth = verticalDelimiterWidth
        }

    var horizontalDelimiterWidth: Int
        get() = mHorizontalDelimiterWidth
        set(horizontalDelimiterWidth) {
            this.mHorizontalDelimiterWidth = horizontalDelimiterWidth
        }
    //endregion
}