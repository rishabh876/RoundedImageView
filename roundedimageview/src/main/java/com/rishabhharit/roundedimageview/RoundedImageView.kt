package com.rishabhharit.roundedimageview

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import java.util.*
import kotlin.math.roundToInt

class RoundedImageView : AppCompatImageView {

    private val ALL_ROUNDED_CORNERS_VALUE = 15 //base 2 = 1111

    private lateinit var paint: Paint
    private lateinit var path: Path
    private var pathWidth: Int = 0
    private var pathHeight: Int = 0
    private var cornerRadius = 0
    private var isCircle: Boolean = false

    private var roundedTopLeft: Boolean = false
    private var roundedBottomLeft: Boolean = false
    private var roundedTopRight: Boolean = false
    private var roundedBottomRight: Boolean = false

    enum class Corner {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

        companion object {
            val ALL = EnumSet.allOf(Corner::class.java)
            val TOP = EnumSet.of(TOP_LEFT, TOP_RIGHT)
        }
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundedImageView, 0, 0)
        val cornerRadius = a.getDimensionPixelSize(R.styleable.RoundedImageView_cornerRadius, 0)
        val roundedCorners = a.getInt(R.styleable.RoundedImageView_roundedCorners, ALL_ROUNDED_CORNERS_VALUE)
        a.recycle()
        init()
        setCornerRadius(cornerRadius)
        setRoundedCorners(roundedCorners)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyleAttr, 0)
        val cornerRadius = a.getDimensionPixelSize(R.styleable.RoundedImageView_cornerRadius, 0)
        val roundedCorners = a.getInt(R.styleable.RoundedImageView_roundedCorners, ALL_ROUNDED_CORNERS_VALUE)
        a.recycle()
        init()
        setCornerRadius(cornerRadius)
        setRoundedCorners(roundedCorners)
    }

    private fun setRoundedCorners(roundedCorners: Int) {
        val topLeft = 8 //1000 base 2
        val topRight = 4 //0100 base 2
        val bottomLeft = 2 //0010 base 2
        val bottomRight = 1 //0001 base 2

        roundedTopLeft = topLeft == roundedCorners and topLeft
        roundedTopRight = topRight == roundedCorners and topRight
        roundedBottomLeft = bottomLeft == roundedCorners and bottomLeft
        roundedBottomRight = bottomRight == roundedCorners and bottomRight
    }

    private fun init() {
        paint = Paint()
        path = Path()
        setupPaint()
    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight)
        val paddingStart = if (Build.VERSION.SDK_INT >= 17) getPaddingStart() else paddingLeft
        val paddingEnd = if (Build.VERSION.SDK_INT >= 17) getPaddingEnd() else paddingRight
        if (pathWidth != newWidth || pathHeight != newHeight) {
            pathWidth = newWidth - (paddingStart + paddingEnd)
            pathHeight = newHeight - (paddingTop + paddingBottom)
            setupPath()
        }
    }

    /**
     * @param cornerRadius in pixels, default is 0
     */
    fun setCornerRadius(cornerRadius: Int) {
        if (this.cornerRadius != cornerRadius) {
            this.cornerRadius = cornerRadius
            setupPath()
        }
    }

    /**
     * @param corners, default is All rounded corners
     */
    fun setRoundCorners(corners: EnumSet<Corner>) {
        if (roundedBottomLeft != corners.contains(Corner.BOTTOM_LEFT)
                || roundedBottomRight != corners.contains(Corner.BOTTOM_RIGHT)
                || roundedTopLeft != corners.contains(Corner.TOP_LEFT)
                || roundedTopRight != corners.contains(Corner.TOP_RIGHT)) {
            roundedBottomLeft = corners.contains(Corner.BOTTOM_LEFT)
            roundedBottomRight = corners.contains(Corner.BOTTOM_RIGHT)
            roundedTopLeft = corners.contains(Corner.TOP_LEFT)
            roundedTopRight = corners.contains(Corner.TOP_RIGHT)
            setupPath()
        }
    }

    private fun setupPaint(): Paint {
        paint.style = Paint.Style.FILL
        paint.color = Color.TRANSPARENT
        paint.isAntiAlias = true
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        return paint
    }

    private fun setupPath() {
        val paddingStart = if (Build.VERSION.SDK_INT >= 17) getPaddingStart() else paddingLeft
        if (roundedTopLeft && roundedTopRight && roundedBottomRight && roundedBottomLeft
                && (cornerRadius >= pathHeight / 2 && cornerRadius >= pathWidth / 2)) {
            isCircle = true
            path = circlePath(path, paddingStart + (pathWidth / 2.0f), paddingTop + (pathHeight / 2.0f), pathWidth, pathHeight)
        } else {
            isCircle = false
            path = roundedRect(path, paddingStart.toFloat(), paddingTop.toFloat(), pathWidth.toFloat(), pathHeight.toFloat(), cornerRadius.toFloat(), cornerRadius.toFloat(),
                    roundedTopLeft, roundedTopRight, roundedBottomRight, roundedBottomLeft)
        }
        setupShadowProvider()
    }

    private fun setupShadowProvider() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val viewOutlineProvider: ViewOutlineProvider
            if (isCircle) {
                viewOutlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        val radius = Math.min(width, height) / 2.0f
                        val left = width / 2 - radius
                        val top = height / 2 - radius
                        val right = width / 2 + radius
                        val bottom = height / 2 + radius
                        outline.setOval(left.roundToInt(), top.roundToInt(), right.roundToInt(), bottom.roundToInt())
                    }
                }
            } else {
                viewOutlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setConvexPath(path)
                    }
                }
            }

            outlineProvider = viewOutlineProvider
            invalidateOutline()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (!isInEditMode) {
            val saveCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
            else
                canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
            super.onDraw(canvas)
            canvas.drawPath(path, paint)
            canvas.restoreToCount(saveCount)
        } else {
            super.onDraw(canvas)
        }
    }

    companion object {

        fun circlePath(path: Path, x: Float, y: Float, viewWidth: Int, viewHeight: Int): Path {
            path.reset()
            val radius = Math.min(viewWidth, viewHeight) / 2.0f
            path.addCircle(x, y, radius, Path.Direction.CCW)
            return path
        }

        fun roundedRect(path: Path?,
                        left: Float, top: Float, right: Float, bottom: Float,
                        rx: Float, ry: Float,
                        tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean): Path {
            var rx = rx
            var ry = ry
            path!!.reset()
            if (rx < 0) {
                rx = 0f
            }
            if (ry < 0) {
                ry = 0f
            }
            val width = right - left
            val height = bottom - top
            if (rx > width / 2) {
                rx = width / 2
            }
            if (ry > height / 2) {
                ry = height / 2
            }
            val widthMinusCorners = width - 2 * rx
            val heightMinusCorners = height - 2 * ry

            path.moveTo(right, top + ry)
            if (tr) {
                path.rQuadTo(0f, -ry, -rx, -ry)//top-right corner
            } else {
                path.rLineTo(0f, -ry)
                path.rLineTo(-rx, 0f)
            }
            path.rLineTo(-widthMinusCorners, 0f)
            if (tl) {
                path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
            } else {
                path.rLineTo(-rx, 0f)
                path.rLineTo(0f, ry)
            }
            path.rLineTo(0f, heightMinusCorners)

            if (bl) {
                path.rQuadTo(0f, ry, rx, ry)//bottom-left corner
            } else {
                path.rLineTo(0f, ry)
                path.rLineTo(rx, 0f)
            }

            path.rLineTo(widthMinusCorners, 0f)
            if (br) {
                path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
            } else {
                path.rLineTo(rx, 0f)
                path.rLineTo(0f, -ry)
            }

            path.rLineTo(0f, -heightMinusCorners)

            path.close()//Given close, last lineto can be removed.

            path.fillType = Path.FillType.INVERSE_EVEN_ODD
            return path
        }
    }
}
