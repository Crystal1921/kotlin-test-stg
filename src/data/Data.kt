package data

import java.awt.image.BufferedImage

data class  DoublePoint (
    val x : Double,
    val y : Double
)

data class Danmaku(
    val position : DoublePoint,
    val theta : Double,
    val radius: Double,
    val image : BufferedImage
)

data class Entity(
    val position : DoublePoint,
    val health : Int,
    val emitterSpeed : Int
)
