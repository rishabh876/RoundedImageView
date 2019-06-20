package com.rishabhharit.roundedimageview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import java.util.*
import kotlin.math.roundToInt

class RoundedImageView : AppCompatImageView {

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
    private var reverseMask: Boolean = false

    private var _paddingTop = 0
    private var _paddingStart = 0
    private var _paddingEnd = 0
    private var _paddingBottom = 0

    constructor(context: Context) : super(context) {
        init()
        setupPath()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundedImageView, 0, 0)
        val cornerRadius = a.getDimensionPixelSize(R.styleable.RoundedImageView_cornerRadius, 0)
        val roundedCorners = a.getInt(R.styleable.RoundedImageView_roundedCorners, ALL_ROUNDED_CORNERS_VALUE)
        reverseMask = a.getBoolean(R.styleable.RoundedImageView_reverseMask, reverseMask)
        a.recycle()
        init()
        setCornerRadiusInternal(cornerRadius)
        setRoundedCornersInternal(roundedCorners)
        fixPadding()
        setupPath()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyleAttr, 0)
        val cornerRadius = a.getDimensionPixelSize(R.styleable.RoundedImageView_cornerRadius, 0)
        val roundedCorners = a.getInt(R.styleable.RoundedImageView_roundedCorners, ALL_ROUNDED_CORNERS_VALUE)
        reverseMask = a.getBoolean(R.styleable.RoundedImageView_reverseMask, reverseMask)
        a.recycle()
        init()
        setCornerRadiusInternal(cornerRadius)
        setRoundedCornersInternal(roundedCorners)
        fixPadding()
        setupPath()
    }

    private fun copyPadding() {
        _paddingTop = paddingTop
        _paddingStart = ViewCompat.getPaddingStart(this)
        _paddingEnd = ViewCompat.getPaddingEnd(this)
        _paddingBottom = paddingBottom
    }

    private fun fixPadding() {
        if (reverseMask) {
            if (ViewCompat.getPaddingStart(this) != 0
                    || ViewCompat.getPaddingEnd(this) != 0
                    || paddingTop != 0
                    || paddingBottom != 0) {
                copyPadding()
                ViewCompat.setPaddingRelative(this, 0, 0, 0, 0)
            }
        } else {
            if (ViewCompat.getPaddingStart(this) != _paddingStart
                    || ViewCompat.getPaddingEnd(this) != _paddingEnd
                    || paddingTop != _paddingTop
                    || paddingBottom != _paddingBottom) {
                copyPadding()
                ViewCompat.setPaddingRelative(this, _paddingStart, _paddingTop, _paddingEnd, _paddingBottom)
            }
        }
    }

    private fun init() {
        paint = Paint()
        path = Path()
        setupPaint()
    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight)
        val _newWidth = newWidth - (_paddingStart + _paddingEnd)
        val _newHeight = newHeight - (_paddingTop + _paddingBottom)

        if (pathWidth != _newWidth || pathHeight != _newHeight) {
            pathWidth = _newWidth
            pathHeight = _newHeight
            setupPath()
        }
    }

    /**
     * @param cornerRadius in pixels, default is 0
     */
    fun setCornerRadius(cornerRadius: Int) {
        if (setCornerRadiusInternal(cornerRadius)) {
            setupPath()
        }
    }


    private fun setCornerRadiusInternal(cornerRadius: Int): Boolean {
        if (this.cornerRadius != cornerRadius) {
            this.cornerRadius = cornerRadius
            return true
        }
        return false
    }

    /**
     * Clips the inside, instead of the outside of the ImageView
     * @param reverseMask default is false
     */
    fun setReverseMask(reverseMask: Boolean) {
        if (this.reverseMask != reverseMask) {
            this.reverseMask = reverseMask
            fixPadding()
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

    private fun setRoundedCornersInternal(roundedCorners: Int) {
        roundedTopLeft = TOP_LEFT == roundedCorners and TOP_LEFT
        roundedTopRight = TOP_RIGHT == roundedCorners and TOP_RIGHT
        roundedBottomLeft = BOTTOM_LEFT == roundedCorners and BOTTOM_LEFT
        roundedBottomRight = BOTTOM_RIGHT == roundedCorners and BOTTOM_RIGHT
    }

    /**
     * @param roundedCorners, where 1111 is All Corners {@see #Companion.ALL_ROUNDED_CORNERS_VALUE}
     */
    fun setRoundedCorners(roundedCorners: Int) {
        setRoundedCornersInternal(roundedCorners)
        setupPath()
    }

    private fun setupPaint(): Paint {
        paint.style = Paint.Style.FILL
        paint.color = Color.TRANSPARENT
        paint.isAntiAlias = true
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        return paint
    }

    private fun setupPath() {
        if (roundedTopLeft && roundedTopRight && roundedBottomRight && roundedBottomLeft
                && (cornerRadius >= pathHeight / 2 && cornerRadius >= pathWidth / 2)) {
            isCircle = true
            path = circlePath(path, _paddingStart + (pathWidth / 2.0f), _paddingTop + (pathHeight / 2.0f), pathWidth, pathHeight, reverseMask)
        } else {
            isCircle = false
            path = roundedRect(path,
                    left = _paddingStart.toFloat(),
                    top = _paddingTop.toFloat(),
                    right = _paddingStart + pathWidth.toFloat(),
                    bottom = _paddingTop + pathHeight.toFloat(),
                    rx = cornerRadius.toFloat(),
                    ry = cornerRadius.toFloat(),
                    tl = roundedTopLeft,
                    tr = roundedTopRight,
                    br = roundedBottomRight,
                    bl = roundedBottomLeft,
                    reverseMask = reverseMask)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (outlineProvider == ViewOutlineProvider.BACKGROUND
                    || outlineProvider is CircularOutlineProvider
                    || outlineProvider is RoundedRectangleOutlineProvider)
                outlineProvider = ViewOutlineProvider.BACKGROUND

            if (isInEditMode && !reverseMask) {
                clipToOutline = true
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    inner class CircularOutlineProvider : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            val radius = Math.min(pathWidth, pathHeight) / 2.0
            val left = (width / 2.0) - radius
            val top = (height / 2.0) - radius
            val right = (width / 2.0) + radius
            val bottom = (height / 2.0) + radius

            outline.setOval(Math.ceil(left).roundToInt(),
                    Math.ceil(top).roundToInt(),
                    Math.ceil(right).roundToInt(),
                    Math.ceil(bottom).roundToInt())
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    inner class RoundedRectangleOutlineProvider : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            try {
                outline.setConvexPath(path)
            } catch (iae: IllegalArgumentException) {
                if (roundedTopLeft && roundedBottomLeft && roundedBottomRight && roundedTopRight)
                    outline.setRoundRect(_paddingStart, paddingTop, pathWidth + _paddingStart, paddingTop + pathHeight, cornerRadius.toFloat())
                else outline.setEmpty()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setOutlineProvider(provider: ViewOutlineProvider?) {
        if (provider == ViewOutlineProvider.BACKGROUND
                || provider is CircularOutlineProvider
                || provider is RoundedRectangleOutlineProvider) {

            val viewOutlineProvider: ViewOutlineProvider?
            when {
                reverseMask -> viewOutlineProvider = null
                isCircle -> viewOutlineProvider = CircularOutlineProvider()
                else -> viewOutlineProvider = RoundedRectangleOutlineProvider()
            }

            super.setOutlineProvider(viewOutlineProvider)
        } else
            super.setOutlineProvider(provider)
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

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        fixPadding()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        fixPadding()
    }

    companion object {

        enum class Corner {
            TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

            companion object {
                val ALL = EnumSet.allOf(Corner::class.java)
                val TOP = EnumSet.of(TOP_LEFT, TOP_RIGHT)
            }
        }

        const val TOP_LEFT = 8 //1000 base 2
        const val TOP_RIGHT = 4 //0100 base 2
        const val BOTTOM_LEFT = 2 //0010 base 2
        const val BOTTOM_RIGHT = 1 //0001 base 2
        const val ALL_ROUNDED_CORNERS_VALUE = 15 //base 2 = 1111

        fun circlePath(path: Path, x: Float, y: Float, viewWidth: Int, viewHeight: Int, reverseMask: Boolean): Path {
            path.reset()
            val radius = Math.min(viewWidth, viewHeight) / 2.0f
            path.addCircle(x, y, radius, Path.Direction.CCW)
            path.fillType = if (reverseMask) Path.FillType.EVEN_ODD else Path.FillType.INVERSE_EVEN_ODD
            return path
        }

        fun roundedRect(path: Path?,
                        left: Float, top: Float, right: Float, bottom: Float,
                        rx: Float, ry: Float,
                        tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean,
                        reverseMask: Boolean): Path {
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

            path.close() //Given close, last lineto can be removed.

            path.fillType = if (!reverseMask) Path.FillType.INVERSE_EVEN_ODD else Path.FillType.EVEN_ODD
            return path
        }
    }
}
