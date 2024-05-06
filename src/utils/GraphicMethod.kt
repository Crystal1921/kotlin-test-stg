package utils

import data.MenuButton
import data.Power
import data.Power.Big
import data.Power.Small
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
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

fun rotateImage(image: BufferedImage?, angle: Double): BufferedImage {
    if (image == null) {throw IOException("image is null")}
    val rotatedImage = BufferedImage(image.width, image.height, image.type)
    val g2d: Graphics2D = rotatedImage.createGraphics()
    val at = AffineTransform.getRotateInstance(angle, (image.width / 2).toDouble(), (image.height / 2).toDouble())
    g2d.transform(at)
    g2d.drawImage(image, 0, 0, null)
    g2d.dispose()
    return rotatedImage
}

fun getImage(power: Power): BufferedImage? {
    return when(power) {
        Small -> Image.images["small_point.png"]
        Big -> Image.images["big_point.png"]
    }
}