package com.satyaprakash.customGaugeView

/*
 * Copyright (C) Satya Prakash 2024.
 * All Rights Reserved.
 */

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.roundToInt

class CustomGaugeView(context: Context, attributeSet: AttributeSet) :
    View(context, attributeSet) {
    /**
     * Progress Value to animate
     */
    private var progress: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     *  Gauge progress animation duration
     */
    private val animationDuration: Long = 1000

    /**
     * Total Linear Gauge Height
     */
    private var viewHeight: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Gauge END x coordinate
     */
    private var viewWidth = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var viewPadding = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * alert Segment  Height of vital gauge
     */
    private var alertSegmentHeight = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * slant Gauge Width
     */
    private var gaugeViewWidth = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Slanted Progress Color
     */
    private var slantProgressColor: Int = Color.LTGRAY
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Alert Segment Color
     */
    private var alertSegmentColor: Int = Color.LTGRAY
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Slanted horizontal Gauge Color
     */
    private var slantGaugeColor: Int = Color.LTGRAY
    /**
     * Vertical Gauge Top View Point
     */

    var alertType = AlertType.NONE
        set(value) {
            field = value
            invalidate()
        }
    private var orientationType = OrientationType.VERTICAL
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Horizontal Gauge End x coordinate
     */
    private var endView = 0f


    /**
     * set the progress gradient opacity
     */
    private var progressGradientOpacity = 0f
        set(value) {
            field = value
            invalidate()
        }
    private var isPositive: Boolean = false
        set(value) {
            field = value
            invalidate()
        }




    init {
        initAtr(attributeSet)
    }

    private fun initAtr(attr: AttributeSet) {
        val styleAttributeSet =
            context.obtainStyledAttributes(attr, R.styleable.CustomGaugeView)
        styleAttributeSet.apply {
            alertSegmentHeight =
                getFloat(R.styleable.CustomGaugeView_alertSegmentHeight, 0f)
            gaugeViewWidth = getFloat(R.styleable.CustomGaugeView_gaugeViewWidth, 0f)
            viewPadding = getFloat(R.styleable.CustomGaugeView_gaugeViewPadding, 0f)
            slantProgressColor =
                getColor(R.styleable.CustomGaugeView_slantProgressColor, Color.LTGRAY)
            slantGaugeColor =
                getColor(R.styleable.CustomGaugeView_slantGaugeColor, Color.LTGRAY)
            alertType =
                AlertType.values()[getInt(R.styleable.CustomGaugeView_alertType, 0)]
            orientationType = OrientationType.values()[getInt(
                R.styleable.CustomGaugeView_orientationType, 0
            )]

            alertSegmentColor =
                getColor(R.styleable.CustomGaugeView_alertSegmentColor, 0)

            progressGradientOpacity
                getFloat(R.styleable.CustomGaugeView_progressGradientOpacity, 0f)
            isPositive = getBoolean(R.styleable.CustomGaugeView_isPositive, true)
        }
    }

    /**
     * Get color opacity with Alpha
     */
    private fun getColorWithAlpha(color: Int): Int {
        return Color.argb(
            (Color.alpha(color) * progressGradientOpacity).roundToInt(),
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        when (orientationType) {
            OrientationType.HORIZONTAL -> {
                viewHeight = h.toFloat() / 2
                viewWidth = w.toFloat()
            }
            OrientationType.VERTICAL -> {
                viewHeight = h.toFloat()
                viewWidth = w.toFloat() / 2
            }
        }
        super.onSizeChanged(w, h, oldw, oldh)
    }
    override fun onDraw(canvas: Canvas?) {
        when (orientationType) {
            OrientationType.VERTICAL -> {
                drawVerticalLinearGauge(canvas)
            }
            OrientationType.HORIZONTAL -> {
                drawHorizontalLinearGauge(canvas)
            }
        }
    }

    private val getProgressValue: Float
        get() = when (orientationType) {
            OrientationType.VERTICAL -> progress * (viewHeight - viewPadding) / 100
            OrientationType.HORIZONTAL -> progress * (endView - viewPadding) / 100
        }

    private val slantProgressPaint: Paint by lazy {
        Paint().apply {
            color = Color.LTGRAY
        }
    }

    private var isAlertSegment: Boolean = false
        set(value) {
            field = value
            invalidate()
        }
    private val getVerticalProgressColor: Int
        get() = when (alertType) {
            AlertType.NONE -> {
                slantProgressColor
            }

            else -> {
                if (!isAlertSegment) {
                    if (progress == ALERT_STATE.toFloat()) {
                        alertSegmentColor
                    } else
                        slantProgressColor

                } else alertSegmentColor
            }
        }

    private fun drawVerticalLinearGauge(canvas: Canvas?) {
        var stopX = viewWidth
        var startX = stopX - gaugeViewWidth

        //Slanted Vital Gauge View progress path
        var path = Path()

        //Slanted Vital Gauge View  Path
        var pathGauge = Path()

        //Slanted Vitals Gauge View Alert Segment Path
        var pathAlertSeg = Path()

        pathGauge.apply {
            moveTo(startX, viewHeight)
            lineTo(startX, viewPadding)
            lineTo(stopX, viewPadding - SLANT_HEIGHT)
            lineTo(stopX, viewHeight - SLANT_HEIGHT)
        }

        canvas?.drawPath(pathGauge, Paint().apply {
            shader = LinearGradient(
                startX,
                viewHeight,
                stopX,
                viewPadding - SLANT_HEIGHT,
                intArrayOf(slantGaugeColor, slantGaugeColor),
                null,
                Shader.TileMode.MIRROR
            )
        })


        when (alertType) {

            AlertType.BOTTOM -> {
                pathAlertSeg.apply {
                    moveTo(startX, viewHeight)
                    lineTo(startX, viewHeight - alertSegmentHeight)
                    lineTo(stopX, viewHeight - alertSegmentHeight - SLANT_HEIGHT)
                    lineTo(stopX, viewHeight - SLANT_HEIGHT)
                }
            }

            AlertType.TOP -> {
                pathAlertSeg.apply {
                    moveTo(startX, viewPadding + alertSegmentHeight)
                    lineTo(startX, viewPadding)
                    lineTo(stopX, viewPadding - SLANT_HEIGHT)
                    lineTo(stopX, viewPadding + alertSegmentHeight - SLANT_HEIGHT)
                }
            }
        }
        canvas?.drawPath(pathAlertSeg, Paint().apply {
            color = alertSegmentColor
        })

        path.apply {
            moveTo(startX, viewHeight)
            lineTo(startX, viewHeight - getProgressValue)
            lineTo(stopX, viewHeight - getProgressValue - SLANT_HEIGHT)
            lineTo(stopX, viewHeight - SLANT_HEIGHT)
        }
        canvas?.drawPath(path, slantProgressPaint.apply {
            shader = LinearGradient(
                startX,
                viewHeight,
                stopX,
                viewHeight - getProgressValue,
                intArrayOf(getColorWithAlpha(getVerticalProgressColor), getVerticalProgressColor),
                null,
                Shader.TileMode.MIRROR
            )
        })
    }


    private fun drawHorizontalLinearGauge(canvas: Canvas?) {
        val x1 = viewPadding
        var stopX = viewWidth - viewPadding
        var startX = viewPadding
        var alertSegment = (stopX - viewPadding) * ALERT_END_PERCENTAGE / 100
        var startY = viewHeight
        var stopY = startY - gaugeViewWidth
        var path = Path()

        while (startX < stopX) {
            canvas?.drawLine(startX, startY, startX + SLANT_HEIGHT, stopY, Paint().apply {
                color = if (AlertType.NONE == alertType) slantGaugeColor
                else {
                    if (startX <= alertSegment) slantGaugeColor
                    else alertSegmentColor
                }
                startX += SEGMENT_STROKE_WIDTH
            })
        }
        if (endView != startX - SEGMENT_STROKE_WIDTH)
            endView = startX - SEGMENT_STROKE_WIDTH

            path.apply {
                moveTo(x1, startY)
                lineTo(
                    x1 + getProgressValue, startY
                )
                lineTo(
                    x1 + getProgressValue + SLANT_HEIGHT, stopY
                )
                lineTo(
                    x1 + SLANT_HEIGHT, stopY
                )
            }
            canvas?.drawPath(path, Paint().apply {
                shader = LinearGradient(
                    x1,
                    startY,
                    x1 + getProgressValue,
                    stopY,
                    intArrayOf(
                        getColorWithAlpha(slantProgressColor),
                        slantProgressColor
                    ),
                    null,
                    Shader.TileMode.MIRROR
                )
            })

    }



    fun setProgressBar(progress: Int, isAnimate: Boolean = false) {
        if (isAnimate)
            setAnimator(progress.toFloat(), animationDuration)
        invalidate()
    }

    fun setProgressData(progress: Float) {
        this.progress = progress
    }

    private fun setAnimator(progress: Float, animDuration: Long) {
        val heightAnimator: ObjectAnimator =
            ObjectAnimator.ofFloat(this, "progressData", this.progress, progress)
        heightAnimator.apply {
            duration = animDuration
            interpolator = DecelerateInterpolator()
        }.start()
        heightAnimator.addUpdateListener {
            invalidate()
        }
    }

    enum class AlertType {
        NONE, TOP, BOTTOM
    }

    enum class OrientationType {
        VERTICAL, HORIZONTAL
    }



    companion object {
        private const val SLANT_HEIGHT = 25
        private const val ALERT_END_PERCENTAGE = 86
        private const val SEGMENT_STROKE_WIDTH = 1
        private const val ALERT_STATE = 100
    }
}