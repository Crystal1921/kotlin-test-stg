package utils

import java.awt.Graphics
import java.awt.image.BufferedImage
import java.awt.image.ImageObserver
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO

fun scanImages(path: Path): MutableMap<String, BufferedImage> {
    val imagesMap : MutableMap<String, BufferedImage> = mutableMapOf()
    Files.walk(path).use { paths ->
        paths.filter { p: Path ->
            p.toString().lowercase(Locale.getDefault()).endsWith(".png")
        }
            .forEach { p: Path ->
                try {
                    val bufferedImage = ImageIO.read(p.toFile())
                    val name = p.fileName.toString()
                    if (bufferedImage != null) {
                        imagesMap[name] = bufferedImage
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
    }
    return imagesMap
}

fun Graphics.drawMiddleImage(image: BufferedImage?, width: Int, observer: ImageObserver, posY: Int, gridNum : Int, gridID: Int) {
    if (image == null) {throw IOException("image is null")}
    if (gridNum < gridID) {throw IOException("Grid Num is less than Grid ID")}
    val gridWidth = width / gridNum
    val posX = gridWidth * (gridID - 0.5)
    val imageWidth = image.width
    val imageHeight = image.height
    this.drawImage(image, (posX - imageWidth / 2).toInt(), posY - imageHeight / 2, observer)
}