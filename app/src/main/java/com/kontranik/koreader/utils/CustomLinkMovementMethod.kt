package com.kontranik.koreader.utils

import android.content.Context
import android.graphics.Point
import android.text.Spannable
import android.text.method.MovementMethod
import android.text.style.URLSpan
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast


/**
 * Implementation of LinkMovementMethod to allow the loading of
 * a link clicked inside text inside an Android application
 * without exiting to an external browser.
 *
 * @author Isaac Whitfield
 * @version 25/08/2013
 */
open class CustomLinkMovementMethod(): android.text.method.LinkMovementMethod() {
    override fun onTouchEvent(
            widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        // Get the event action
        val action = event.action

        // If action has finished
        if (action == MotionEvent.ACTION_UP) {
            // Locate the area that was pressed
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY

            // Locate the URL text
            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            // Find the URL that was pressed
            val link = buffer.getSpans(off, off, URLSpan::class.java)
            // If we've found a URL
            if (link.size != 0) {
                // Find the URL
                val url = link[0].url
                // If it's a valid URL
                onLinkClicked(url)
                /*
                if (url.contains("https") or url.contains("tel") or url.contains("mailto") or url.contains("http") or url.contains("https") or url.contains("www")) {
                    // Open it in an instance of InlineBrowser
                    //movementContext!!.startActivity(Intent(movementContext, MinimalBrowser::class.java).putExtra("url", url))
                }
                 */
                // If we're here, something's wrong
                return true
            } else {
                onClick(Point(event.x.toInt(), event.y.toInt()))
            }
        }
        //return false
        return super.onTouchEvent(widget, buffer, event)
    }

    open fun onLinkClicked(url: String) { }
    open fun onClick(point: Point) {}

    companion object {
        /*
        // The context we pass to the method
        private var movementContext: Context? = null

        // A new LinkMovementMethod
        private val linkMovementMethod = CustomLinkMovementMethod()
        fun getInstance(c: Context?): MovementMethod {
            // Set the context
            movementContext = c
            // Return this movement method
            return linkMovementMethod
        }

         */
    }
}