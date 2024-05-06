package utils

import data.DoublePoint
import data.MenuButton
import data.Rectangle
import entity.AbstractEntity
import java.awt.Font
import java.awt.image.BufferedImage
import kotlin.math.pow

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

fun nearestEntity (pos : DoublePoint) : AbstractEntity? {
    return Entity.enemies.maxByOrNull { (pos.x - it.pos.x).pow(2) + (pos.y - it.pos.y).pow(2) }
}

fun getBoundingBox(image : BufferedImage?, pos: DoublePoint) : Rectangle {
    if (image == null) {
        return Rectangle(0,0,640,480)
    }
    return Rectangle(pos.x.toInt() - image.width / 2, pos.y.toInt() - image.height / 2,image.width,image.height)
}