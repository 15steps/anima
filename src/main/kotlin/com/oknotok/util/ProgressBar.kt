package com.oknotok.util


fun timedProgressBar(timer: CountDownTimer = CountDownTimer(1000).start()) =
    fun(bytesRead: Long, contentLength: Long, done: Boolean) {
        if (timer.isOverAndRestart()) {
            clearConsole()
            val percentage = (bytesRead * 100) / contentLength
            print("Progress: [")
            repeat(percentage.toInt()) {
                print("=")
            }
            repeat(100-percentage.toInt()) {
                print(" ")
            }
            println("] $percentage% downloaded")

        }
        if (done) {
            println("Download is done!")
        }
    }

private fun clearConsole() {
    repeat(200) { println() }
}
