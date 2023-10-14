package com.now.naaga.presentation.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton
import com.now.naaga.R

class GameButton(context: Context, attrs: AttributeSet? = null) : AppCompatButton(context, attrs) {
    private val radius: Float
    private var clicked: Boolean = false
    private var clickAction: OnClickListener? = null

    private val mainColor: Int
    private val firstShadowColor: Int
    private val middleColor: Int
    private val secondShadowColor: Int
    private val bottomColor: Int

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GameButton,
            0,
            0,
        ).apply {
            radius = getDimensionPixelSize(R.styleable.GameButton_radius, 0).toFloat()
            val gameButtonColor = when (getInteger(R.styleable.GameButton_buttonColor, 0)) {
                1 -> GameButtonColor.BLUE
                else -> GameButtonColor.YELLOW
            }
            mainColor = Color.parseColor(gameButtonColor.mainColor)
            firstShadowColor = Color.parseColor(gameButtonColor.firstShadowColor)
            middleColor = Color.parseColor(gameButtonColor.middleColor)
            secondShadowColor = Color.parseColor(gameButtonColor.secondShadowColor)
            bottomColor = Color.parseColor(gameButtonColor.bottomColor)
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
        if (clicked) drawRipple(canvas)
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                clicked = true
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                clicked = false
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
        canvas.drawRoundRect(getBottomRect(), radius, radius, getPaint(bottomColor))
        canvas.drawRoundRect(getSecondShadowRect(), radius, radius, getPaint(secondShadowColor))
        canvas.drawRoundRect(getMiddleRect(), radius, radius, getPaint(middleColor))
        canvas.drawRoundRect(getFirstShadowRect(), radius, radius, getPaint(firstShadowColor))
        canvas.drawRoundRect(getMainRect(), radius, radius, getPaint(mainColor))
    }

    private fun getMainRect(): RectF {
        val width = (this.width * 0.96).toFloat()
        val height = (this.height * 0.83).toFloat()
        val start = (this.width * 0.018).toFloat()
        val end = start + width
        val top = (this.height * 0.02).toFloat()
        val bottom = top + height
        return RectF(start, top, end, bottom)
    }

    private fun getFirstShadowRect(): RectF {
        val width = (this.width * 0.96).toFloat()
        val height = (this.height * 0.86).toFloat()
        val start = (this.width * 0.018).toFloat()
        val end = start + width
        val top = (this.height * 0.02).toFloat()
        val bottom = top + height
        return RectF(start, top, end, bottom)
    }

    private fun getMiddleRect(): RectF {
        val middleWidth = (width * 0.987).toFloat()
        val middleHeight = (height * 0.916).toFloat()
        val start = (width * 0.006).toFloat()
        val end = start + middleWidth
        val top = (height * 0.01).toFloat()
        val bottom = top + middleHeight
        return RectF(start, top, end, bottom)
    }

    private fun getSecondShadowRect(): RectF {
        return RectF(0f, 0f, width.toFloat(), (height * 0.94).toFloat())
    }

    private fun getBottomRect(): RectF {
        return RectF(0f, 0f, width.toFloat(), height.toFloat())
    }
}
