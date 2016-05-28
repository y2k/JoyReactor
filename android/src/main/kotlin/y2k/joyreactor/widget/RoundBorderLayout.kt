package y2k.joyreactor.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Created by y2k on 9/26/15.
 */
class RoundBorderLayout(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val clipPaint: Paint = Paint().apply { isAntiAlias = true }
    private val borderPaint: Paint = Paint().apply {
        isAntiAlias = true
        color = 1082163328
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    private val lastLayout = IntArray(2)
    private val rect = RectF()

    private var clipCanvas: Canvas? = null

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val w = right - left
        val h = bottom - top
        if (lastLayout[0] != w && lastLayout[1] != h)
            updateLayoutCanvas(w, h)
    }

    private fun updateLayoutCanvas(w: Int, h: Int) {
        if (w > 0 && h > 0) {
            val canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            clipCanvas = Canvas(canvasBitmap)
            rect.set(0f, 0f, w.toFloat(), h.toFloat())
            clipPaint.setShader(BitmapShader(canvasBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))
        } else {
            clipCanvas = null
            clipPaint.setShader(null)
            rect.setEmpty()
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (clipCanvas != null) {
            super.dispatchDraw(clipCanvas)
            canvas.drawOval(rect, clipPaint)
            canvas.drawOval(rect, borderPaint)
        }
    }
}