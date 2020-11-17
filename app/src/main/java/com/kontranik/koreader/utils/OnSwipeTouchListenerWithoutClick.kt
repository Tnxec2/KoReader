package com.kontranik.koreader.utils

import android.content.Context
import android.graphics.Point
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

/*
 *
 * OnTouchListener without onClick-Event
 *
 */
internal open class OnSwipeTouchListenerWithoutClick(c: Context?) :
        OnTouchListener {
    private val gestureDetector: GestureDetector
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(motionEvent)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD: Int = 100
        private val SWIPE_VELOCITY_THRESHOLD: Int = 100

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            onDoubleClick(Point( e.x.toInt(), e.y.toInt()))
            return super.onDoubleTap(e)
        }

        override fun onLongPress(e: MotionEvent) {
            onLongClick(Point( e.x.toInt(), e.y.toInt()))
            super.onLongPress(e)
        }

        override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float): Boolean {
            // return super.onScroll(e1, e2, distanceX, distanceY)
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD) {
                        if (diffX > 0) {
                            onSlideRight(Point( e1.x.toInt(), e1.y.toInt()))
                        } else {
                            onSlideLeft(Point( e1.x.toInt(), e1.y.toInt()))
                        }
                    }
                } else {
                    if (abs(diffY) > SWIPE_THRESHOLD) {
                        if (diffY < 0) {
                            onSlideUp(Point( e1.x.toInt(), e1.y.toInt()))
                        } else {
                            onSlideDown(Point( e1.x.toInt(), e1.y.toInt()))
                        }
                    }

                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return false
        }

        override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
        ): Boolean {
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if ( abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        // swipe
                        if (abs(diffX) > SWIPE_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight()
                            } else {
                                onSwipeLeft()
                            }
                        }
                    }
                } else {
                    if (abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (abs(diffY) > SWIPE_THRESHOLD) {
                            if (diffY < 0) {
                                onSwipeUp()
                            } else {
                                onSwipeDown()
                            }
                        }
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return false
        }
    }
    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
    open fun onSwipeUp() {}
    open fun onSwipeDown() {}
    open fun onSlideUp(point: Point){}
    open fun onSlideDown(point: Point){}
    open fun onSlideLeft(point: Point){}
    open fun onSlideRight(point: Point){}

    open fun onDoubleClick(point: Point) {}
    open fun onLongClick(point: Point) {}

    init {
        gestureDetector = GestureDetector(c, GestureListener())
    }
}