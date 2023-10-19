package com.now.naaga.presentation.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.now.naaga.R

class GameButton(context: Context, attrs: AttributeSet? = null) : AppCompatButton(context, attrs) {
    private val radius: Float
    private var isClicked: Boolean = false
    private var clickAction: OnClickListener? = null

    @ColorInt
    private val mainColor: Int

    @ColorInt
    val firstShadowColor: Int

    @ColorInt
    val middleColor: Int

    @ColorInt
    val secondShadowColor: Int

    @ColorInt
    private val bottomColor: Int

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GameButton,
            0,
            0,
        ).apply {
            radius = getDimensionPixelSize(R.styleable.GameButton_radius, 0).toFloat()
            val gameButtonColor = GameButtonColor.getColor(getInteger(R.styleable.GameButton_buttonColor, 0))
            mainColor = ContextCompat.getColor(context, gameButtonColor.mainColor)
            firstShadowColor = ContextCompat.getColor(context, gameButtonColor.firstShadowColor)
            middleColor = ContextCompat.getColor(context, gameButtonColor.middleColor)
            secondShadowColor = ContextCompat.getColor(context, gameButtonColor.secondShadowColor)
            bottomColor = ContextCompat.getColor(context, gameButtonColor.bottomColor)
            recycle()
        }
    }

    private fun getPaint(color: Int) = Paint().apply {
        this.color = color
    }

    private val ripplePaint = Paint().apply {
        this.color = Color.parseColor("#4D000000")
    }

    override fun onDraw(canvas: Canvas) {
        setBackgroundColor(Color.TRANSPARENT)
        drawButton(canvas)
        if (isClicked) drawRipple(canvas)
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isClicked = true
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                isClicked = false
                invalidate()
                clickAction?.onClick(this)
            }
        }
        return true
    }

    override fun setOnClickListener(l: OnClickListener?) {
        clickAction = l
    }

    private fun drawRipple(canvas: Canvas) {
        canvas.drawRoundRect(getBottomRect(), radius, radius, ripplePaint)
    }

    private fun drawButton(canvas: Canvas) {
        with(canvas) {
            drawRoundRect(getBottomRect(), radius, radius, getPaint(bottomColor))
            drawRoundRect(getSecondShadowRect(), radius, radius, getPaint(secondShadowColor))
            drawRoundRect(getMiddleRect(), radius, radius, getPaint(middleColor))
            drawRoundRect(getFirstShadowRect(), radius, radius, getPaint(firstShadowColor))
            drawRoundRect(getMainRect(), radius, radius, getPaint(mainColor))
        }
    }

    // 154 x 70 (2,2)
    private fun getMainRect(): RectF {
        val width = (this.width * 0.956).toFloat()
        val height = (this.height * 0.833).toFloat()
        val start = (this.width * 0.012).toFloat()
        val end = start + width
        val top = (this.height * 0.02).toFloat()
        val bottom = top + height
        return RectF(start, top, end, bottom)
    }

    // 155 x 72 (2,2)
    private fun getFirstShadowRect(): RectF {
        val width = (this.width * 0.96).toFloat()
        val height = (this.height * 0.857).toFloat()
        val start = (this.width * 0.012).toFloat()
        val end = start + width
        val top = (this.height * 0.02).toFloat()
        val bottom = top + height
        return RectF(start, top, end, bottom)
    }

    // 158 x 77 (1,1)
    private fun getMiddleRect(): RectF {
        val middleWidth = (width * 0.98).toFloat()
        val middleHeight = (height * 0.916).toFloat()
        val start = (width * 0.006).toFloat()
        val end = start + middleWidth
        val top = (height * 0.01).toFloat()
        val bottom = top + middleHeight
        return RectF(start, top, end, bottom)
    }

    // 160 x 79 (0,0)
    private fun getSecondShadowRect(): RectF {
        return RectF(0f, 0f, (width * 0.99).toFloat(), (height * 0.94).toFloat())
    }

    // 161 x 84 (0,0)
    private fun getBottomRect(): RectF {
        return RectF(0f, 0f, width.toFloat(), height.toFloat())
    }
}
