package utils

import data.MenuButton
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver
import java.io.IOException

fun Graphics.drawPosImage(image: BufferedImage?, observer: ImageObserver, posX: Int, posY: Int) {
    if (image == null) {throw IOException("image is null")}
    this.drawImage(image, posX - image.width / 2, posY - image.height / 2, observer)
}

fun Graphics.drawButton(menuButton: MenuButton) {
    this.drawRect(menuButton.area.x,menuButton.area.y,menuButton.area.width,menuButton.area.height)
    if (menuButton.isPointed) this.color = Color.ORANGE
    this.drawString(menuButton.name, menuButton.pos.x, menuButton.pos.y)
    this.color = Color.WHITE
}