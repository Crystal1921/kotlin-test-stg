package utils

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class KeyboardInput : KeyListener {
    private val keys: BooleanArray = BooleanArray(256)
    private val polled: IntArray = IntArray(256)

    @Synchronized
    fun poll() {
        for (i in keys.indices) {
            if (keys[i]) {
                polled[i]++
            } else polled[i] = 0
        }
    }

    @Synchronized
    fun keyDown(keycode: Int): Boolean {
        return polled[keycode] > 0
    }

    @Synchronized
    fun keyDownOnce(keycode: Int): Boolean {
        return polled[keycode] == 1
    }

    override fun keyTyped(e: KeyEvent) {
    }

    @Synchronized
    override fun keyPressed(e: KeyEvent) {
        val keyCode: Int = e.keyCode
        if (keyCode >= 0 && keyCode < keys.size) {
            keys[keyCode] = true
        }
    }

    @Synchronized
    override fun keyReleased(e: KeyEvent) {
        val keyCode: Int = e.keyCode
        if (keyCode >= 0 && keyCode < keys.size) {
            keys[keyCode] = false
        }
    }
}