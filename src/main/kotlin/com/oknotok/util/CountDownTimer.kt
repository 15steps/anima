package com.oknotok.util

class CountDownTimer(
    private val interval: Long
) {
    private var startTime: Long = 0;

    fun start() = apply {
        startTime = System.currentTimeMillis()
    }

    fun isOverAndRestart(): Boolean = if (isOver()) {
        start()
        true
    } else {
        false
    }

    fun isOver(): Boolean = System.currentTimeMillis() > startTime + interval
}