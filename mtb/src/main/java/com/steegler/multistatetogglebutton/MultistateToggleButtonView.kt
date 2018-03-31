package com.steegler.multistatetogglebutton

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.transition.AutoTransition
import android.support.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.LinearLayout
import java.util.*


/**
 * Created by argi on 3/30/18.
 */


class MultistateToggleButton : ConstraintLayout {

    private var _selectedPosition = -1
    var selectedPosition: Int
        set(value) {
            this._selectedPosition = value
            highlightButton()
            callback?.notNull { it.onStateChanged(this, value) }
        }
        get() {
            return this._selectedPosition
        }

    private var mInterface: MTBInterface? = null
    var callback: MTBInterface?
        set(value) {
            this.mInterface = value
        }
        get() {
            return mInterface
        }

    var buttonsLabels = ArrayList<String>()
    var indicatorHeight = 4f
    var stratch = false
    var textSize = 13f
    var textFont: Typeface = Typeface.DEFAULT
    var textColor: ColorStateList

    var indicatorView: View
    var linearLayout: LinearLayout

    val set = ConstraintSet()


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val attributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.MTB,
                defStyleAttr, 0)

        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.MTB, defStyleAttr, 0)

        try {

            val arrayID = array.getResourceId(R.styleable.MTB_entries, 0)
            if (arrayID > 0) resources.getStringArray(arrayID).iterator().forEach { buttonsLabels.add(it) }

            indicatorHeight = array.getDimension(R.styleable.MTB_indicator_height, 4f)

            stratch = array.getBoolean(R.styleable.MTB_stretch, false)

            textSize = array.getFloat(R.styleable.MTB_textSize, 13f)

            val textColorRes = array.getResourceId(R.styleable.MTB_textColorList, 0)
            textColor = resources.getColorStateList(if (textColorRes != 0) textColorRes else R.color.buttons_text_color)

        } finally {
            attributes.recycle()
        }

        linearLayout = LinearLayout(context)
        indicatorView = View(context)

        afterMeasured {
            if (buttonsLabels.size > 0) createButtonsLine()

            drawIndicator()
        }
    }


    private fun createButtonsLine() {
        linearLayout = LinearLayout(context)
        linearLayout.id = View.generateViewId()
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.layoutParams = ConstraintLayout.LayoutParams(width, (height - indicatorHeight).toInt())
        addView(linearLayout)

        set.clone(this)
        set.connect(linearLayout.id, ConstraintSet.BOTTOM, id, ConstraintSet.BOTTOM, 0)
        set.connect(linearLayout.id, ConstraintSet.START, id, ConstraintSet.START, 0)
        set.connect(linearLayout.id, ConstraintSet.END, id, ConstraintSet.END, 0)
        set.constrainDefaultWidth(linearLayout.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)
        set.applyTo(this)

        buttonsLabels.forEachIndexed { index, label ->
            val layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f)
            val btn = Button(context)
            btn.layoutParams = layoutParams
            btn.text = label
            btn.background = null
            btn.tag = index
            btn.setBackgroundColor(resources.getColor(android.R.color.transparent))
            btn.textSize = textSize
            btn.setTextColor(textColor)
            btn.typeface = textFont

            if (stratch) {
                if (index == 0) btn.gravity = Gravity.START
                if (index == buttonsLabels.size - 1) btn.gravity = Gravity.END
            }

            btn.setOnClickListener({
                selectedPosition = index

            })

            btn.isSelected = selectedPosition == index

            linearLayout.addView(btn, layoutParams)
        }

    }

    private fun highlightButton() {
        var currentButton: Button? = null
        for (i in 0 until linearLayout.childCount) {
            (linearLayout.getChildAt(i) as Button).isSelected = false
            if ((linearLayout.getChildAt(i) as Button).tag == selectedPosition)
                currentButton = (linearLayout?.getChildAt(i) as Button)

        }

        currentButton.notNull {
            it.isSelected = true
            val margin = ((width / buttonsLabels.size) * selectedPosition)

            set.connect(indicatorView.id, ConstraintSet.TOP, id, ConstraintSet.TOP, 1)
            set.connect(indicatorView.id, ConstraintSet.START, id, ConstraintSet.START, margin)
            set.constrainWidth(indicatorView.id, (width / buttonsLabels.size))
            set.constrainHeight(indicatorView.id, indicatorHeight.toInt())


            val transition = AutoTransition()
            transition.duration = 250
            transition.interpolator = AccelerateDecelerateInterpolator()

            TransitionManager.beginDelayedTransition(this, transition)
            set.applyTo(this)
        }

    }

    private fun drawIndicator() {
        val margin = ((width / buttonsLabels.size) * selectedPosition)
        indicatorView = View(context)
        val indicatorWidth = (width / buttonsLabels.size)
        indicatorView.id = View.generateViewId()
        indicatorView.layoutParams = ConstraintLayout.LayoutParams(indicatorWidth, indicatorHeight.toInt())
        indicatorView.setBackgroundResource(R.color.tomato_red)
        addView(indicatorView)

        set.clone(this)
        set.connect(indicatorView.id, ConstraintSet.TOP, id, ConstraintSet.TOP, 1)
        set.connect(indicatorView.id, ConstraintSet.START, id, ConstraintSet.START, margin)
        set.constrainWidth(indicatorView.id, indicatorWidth)
        set.constrainHeight(indicatorView.id, indicatorHeight.toInt())
        set.applyTo(this)
    }

    interface MTBInterface {
        fun onStateChanged(view: View, index: Int) {
            if (BuildConfig.DEBUG)
                Log.d("MTB", "<DEBUG> button index $index")
        }
    }

}


inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

fun <T : Any> T?.notNull(f: (it: T) -> Unit) {
    if (this != null) f(this)
}