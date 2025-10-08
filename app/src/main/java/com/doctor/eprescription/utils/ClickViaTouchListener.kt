package com.doctor.eprescription.utils

import android.util.Log
import android.view.MotionEvent
import android.view.View

//Created by Bilal Bangash 28/06/2021 2:31 PM
open class ClickViaTouchListener(val clickListener : View.OnClickListener) : View.OnTouchListener {

    private val CLICK_ACTION_THRESHOLD = 200
    private var startX = 0f
    private var startY = 0f

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("ClickViaTouch", "onDown")
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_UP -> {
                Log.e("ClickViaTouch", "onUp")
                val endX = event.x
                val endY = event.y
                if (isAClick(startX, endX, startY, endY)) {
                    clickListener.onClick(v)
                }
            }
        }
        v!!.parent.requestDisallowInterceptTouchEvent(true) //specific to my project

        return false //specific to my project

    }

    private fun isAClick(startX: Float, endX: Float, startY: Float, endY: Float): Boolean {
        val differenceX = Math.abs(startX - endX)
        val differenceY = Math.abs(startY - endY)
        return !(differenceX > CLICK_ACTION_THRESHOLD /* =5 */ || differenceY > CLICK_ACTION_THRESHOLD)
    }
}