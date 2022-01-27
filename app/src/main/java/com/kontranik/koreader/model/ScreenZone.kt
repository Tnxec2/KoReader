package com.kontranik.koreader.model

import android.graphics.Point

enum class ScreenZone {

    TopLeft,
    TopCenter,
    TopRight,
    MiddleLeft,
    MiddleCenter,
    MiddleRight,
    BottomLeft,
    BottomCenter,
    BottomRight;

    companion object {
        fun zone(point: Point, width: Int, height: Int): ScreenZone {
            return if ( isLeft(point.x, width) && isTop(point.y, height)) TopLeft
            else if ( isCenter(point.x, width) && isTop(point.y, height)) TopCenter
            else if ( isRight(point.x, width) && isTop(point.y, height)) TopRight
            else if ( isLeft(point.x, width) && isMiddle(point.y, height)) MiddleLeft
            else if ( isCenter(point.x, width) && isMiddle(point.y, height)) MiddleCenter
            else if ( isRight(point.x, width) && isMiddle(point.y, height)) MiddleRight
            else if ( isLeft(point.x, width) && isBottom(point.y, height)) BottomLeft
            else if ( isCenter(point.x, width) && isBottom(point.y, height)) BottomCenter
            else BottomRight
        }

        private fun isLeft(x: Int, width: Int): Boolean {
            return x >= 0 && x < width / 3
        }
        private fun isCenter(x: Int, width: Int): Boolean {
            return x >= width / 3 && x < width / 3 * 2
        }
        private fun isRight(x: Int, width: Int): Boolean {
            return x >= width / 3 * 2 && x <= width
        }
        private fun isTop(y: Int, height: Int): Boolean {
            return y >= 0 && y < height / 3
        }
        private fun isMiddle(y: Int, height: Int): Boolean {
            return y >= height / 3 && y < height / 3 * 2
        }
        private fun isBottom(y: Int, height: Int): Boolean {
            return y >= height / 3 * 2 && y <= height
        }
    }
}