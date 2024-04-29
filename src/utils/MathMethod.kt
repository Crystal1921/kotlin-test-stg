package utils

import data.MenuButton
import data.Rectangle
import java.awt.Font

class MathMethod {
}

fun getFont(size : Int) : Font {
    return Font("Bradley Hand ITC", Font.PLAIN, size)
}

fun isInArea(area: Rectangle, mouseX: Int, mouseY: Int): Boolean {
    return isInArea(area.x, area.y, area.width, area.height, mouseX, mouseY)
}

fun isInArea(posX: Int, posY: Int, width: Int, height: Int, mouseX: Int, mouseY: Int ) : Boolean {
    val inX : Boolean = mouseX > posX && mouseX < posX + width
    val inY : Boolean = mouseY > posY && mouseY < posY + height
    return inX && inY
}

fun isButtonsInArea(buttons : MutableList<MenuButton>, mouseX: Int, mouseY : Int){
    buttons.forEach { it.isPointed = isInArea(it.area, mouseX, mouseY) }
}