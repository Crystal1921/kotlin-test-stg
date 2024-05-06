package utils

import data.MenuButton
import data.Rectangle
import entity.AbstractDanmaku
import entity.AbstractEntity
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

object Image{
    val images = initImages(Paths.get("src/texture"))
}

object Entity{
    val danmaku = mutableListOf<AbstractDanmaku>()
    val entities = mutableListOf<AbstractEntity>()
}

fun initImages(path: Path): MutableMap<String, BufferedImage> {
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

fun initButton() : MutableList<MenuButton> {
    val deltaY = 24
    val buttons: MutableList<MenuButton> = mutableListOf()
    buttons.add(MenuButton("Game  Start", Point(230,100), Rectangle(230,100 - deltaY, 170,32),false,0))
    buttons.add(MenuButton("Single Mode", Point(230,150), Rectangle(230,150 - deltaY, 170,32),false,1))
    buttons.add(MenuButton("Exit", Point(230,200), Rectangle(230,200 - deltaY, 60,32),false,3))
    return buttons
}