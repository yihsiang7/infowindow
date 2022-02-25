package com.yihsiang.infowindow

import android.animation.*
import android.annotation.*
import android.content.*
import android.content.res.*
import android.graphics.*
import android.os.*
import android.util.*
import android.view.*
import android.view.animation.*
import android.widget.*
import androidx.constraintlayout.widget.*
import kotlin.math.*

/**
 * @param contentView 要填充的資訊內容
 * @param position 顯示位置
 */
@SuppressLint("InflateParams")
class InfoWindow(private val contentView: View, private val position: Int) {

    private val windowManager: WindowManager

    private val infoWindowView: ConstraintLayout

    private val animationExecutor by lazy { InfoWindowAnimationExecutor() }

    private var isShowing = false

    init {
        val context = contentView.context
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val infoWindowLayout = when (position) {
            Gravity.BOTTOM -> R.layout.bottom_info_window
            else -> R.layout.top_info_window
        }
        infoWindowView = (LayoutInflater.from(context)
            .inflate(infoWindowLayout, null, false) as ConstraintLayout)
            .apply { alpha = 0f }
    }

    /**
     * 顯示資訊視窗
     *
     * @param anchor 指向該元件的 view
     * @param yOffset 垂直偏移量(px)
     */
    fun show(anchor: View, yOffset: Float = 0f) {
        // 避免被重複加入到某個容器內
        if (infoWindowView.parent != null || contentView.parent != null) return

        val container = infoWindowView.findViewById<FrameLayout>(R.id.container)
        container.addView(contentView, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT))

        val layoutParams = createWindowLayoutParams()
        calculateAndSetOffset(anchor, layoutParams, yOffset)

        windowManager.addView(infoWindowView, layoutParams)

        isShowing = true

        animationExecutor.execute()
    }

    fun dismiss() {
        if (isShowing) {
            animationExecutor.cancel()
            animationExecutor.stop()
        }
    }

    private fun calculateAndSetOffset(
        anchor: View,
        layoutParams: WindowManager.LayoutParams,
        yOffset: Float
    ) {
        val coordinate = IntArray(2)
        anchor.getLocationOnScreen(coordinate)
        val anchorPoint = Point(coordinate[0], coordinate[1])

        // 先測量該元件的寬高
        infoWindowView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val yOff = yOffset.absoluteValue.roundToInt()
        layoutParams.y = when (position) {
            Gravity.BOTTOM -> anchorPoint.y + anchor.height + yOff
            else -> anchorPoint.y - infoWindowView.measuredHeight - yOff
        }

        // 以對齊目標元件中心做偏移，正的往右偏移，負的往左偏移
        val offset = (anchor.width / 2f - infoWindowView.measuredWidth / 2f).roundToInt()
        val newX = (anchorPoint.x + offset)
        val arrow = infoWindowView.findViewById<ImageView>(R.id.arrow)
        val screenWidth = windowManager.defaultDisplay.width
        val container = infoWindowView.findViewById<FrameLayout>(R.id.container)
        val containerLayoutParams = container.layoutParams as ConstraintLayout.LayoutParams
        when {
            newX < 0 /*超出螢幕左邊*/ -> {
                layoutParams.x = anchorPoint.x /*往右位移對齊目標元件左邊*/
                arrow.x = offset.toFloat() /*對齊目標元件中間*/
                // 位移後超出螢幕右邊，強制調整寬度
                if (layoutParams.x + infoWindowView.measuredWidth > screenWidth) {
                    containerLayoutParams.matchConstraintMaxWidth = screenWidth - layoutParams.x
                }
            }
            newX + infoWindowView.measuredWidth > screenWidth /*超出螢幕右邊*/ -> {
                layoutParams.x =
                    anchorPoint.x - infoWindowView.measuredWidth + anchor.width /*往左位移對齊目標元件右邊*/
                arrow.x =
                    (anchorPoint.x + anchor.width / 2f) - (layoutParams.x + infoWindowView.measuredWidth / 2f) /*對齊目標元件中間*/
                // 位移後超出螢幕左邊，強制調整寬度
                if (layoutParams.x < 0) {
                    layoutParams.x = 0
                    containerLayoutParams.matchConstraintMaxWidth = anchorPoint.x + anchor.width
                }
            }
            else -> {
                layoutParams.x = newX
            }
        }
        container.layoutParams = containerLayoutParams
    }

    private fun createWindowLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT // 半透明
        ).apply {
            gravity = Gravity.START or Gravity.TOP
        }
    }

    inner class InfoWindowAnimationExecutor : CountDownTimer(TOTAL_DISPLAY_DURATION, 1L) {

        private var isFadeOut = false

        private var animator: ViewPropertyAnimator? = null

        fun execute() {
            start()
            executeAlphaAnimation(1f)
        }

        fun stop() {
            isFadeOut = false
            // 避免 Activity 不存在操作 remove 會拋錯
            if (contentView.isAttachedToWindow) {
                val container = infoWindowView.findViewById<FrameLayout>(R.id.container)
                container.removeView(contentView)
            }
            if (infoWindowView.isAttachedToWindow) {
                windowManager.removeViewImmediate(infoWindowView)
            }
            animator?.cancel()
            isShowing = false
        }

        override fun onTick(millisUntilFinished: Long) {
            // 開始做淡出
            if (millisUntilFinished <= ANIMATION_DURATION && !isFadeOut) {
                isFadeOut = true
                executeAlphaAnimation(0f)
            }
        }

        override fun onFinish() {
            stop()
        }

        private fun executeAlphaAnimation(alpha: Float) {
            animator = infoWindowView.animate()
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(LinearInterpolator())
                .alpha(alpha)
                .apply { start() }
        }
    }

    companion object {
        private val TAG = InfoWindow::class.simpleName
        private const val ANIMATION_DURATION = 300L
        // 300ms淡入 + 3000ms顯示 + 300ms淡出
        private const val TOTAL_DISPLAY_DURATION = 3600L
    }
}