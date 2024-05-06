package entity

import data.DoublePoint
import java.awt.image.BufferedImage

abstract class AbstractEntity {
    abstract var health : Int
    abstract var pos : DoublePoint
    abstract val image : BufferedImage?
    var lifeTime  = 0
    abstract fun tick()
}