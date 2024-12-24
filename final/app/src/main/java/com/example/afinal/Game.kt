package com.example.afinal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

open class GameCircle(ctx: Context): View(ctx){

    data class Circle(var x: Float, var y: Float, val radius: Float, val color: Int)
    fun randomColor(): Int {
        return Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256))
    }

    var circles = mutableListOf<Circle>()
    var targetRectangle: Rect? = null
    var targetColor: Int = Color.WHITE
    val paint = Paint()
    var draggedCircle: Circle? = null
    var gameOver = false
    var isInitialized = false
    var r = Random(0)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!isInitialized) {
            repeat(10) {
                val radius = 50f
                val x = r.nextFloat() * (width - 2 * radius) + radius
                val y = r.nextFloat() * (height - 2 * radius) + radius
                circles.add(Circle(x, y, radius, randomColor()))
            }

            val rectLeft = (width / 2 - 150)
            val rectTop = (height / 2 - 150)
            val rectRight = (width / 2 + 150)
            val rectBottom = (height / 2 + 150)
            targetRectangle = Rect(rectLeft, rectTop, rectRight, rectBottom)
            targetColor = circles.firstOrNull()?.color ?: Color.WHITE

            isInitialized = true
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (gameOver) {
            paint.color = Color.BLACK
            paint.textSize = 60f
            canvas.drawText("Всё.", width / 2f - 150f, height / 2f, paint)
            return
        }

        paint.color = targetColor
        targetRectangle?.let { canvas.drawRect(it, paint) }

        for (circle in circles) {
            paint.color = circle.color
            canvas.drawCircle(circle.x, circle.y, circle.radius, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gameOver) return super.onTouchEvent(event)

        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                draggedCircle = circles.find { circle ->
                    val dx = circle.x - x
                    val dy = circle.y - y
                    dx * dx + dy * dy <= circle.radius * circle.radius
                }
            }

            MotionEvent.ACTION_MOVE -> {
                draggedCircle?.let {
                    it.x = x
                    it.y = y
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                draggedCircle?.let {
                    targetRectangle?.let { rect ->
                        if (rect.contains(it.x.toInt(), it.y.toInt()) && it.color == targetColor) {
                            circles.remove(it)
                            targetColor = circles.firstOrNull()?.color ?: Color.WHITE
                            if (circles.isEmpty()) {
                                gameOver = true
                            }
                            invalidate()
                        }
                    }
                }
                draggedCircle = null
            }
        }
        return true
    }
}


