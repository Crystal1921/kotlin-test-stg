package entity

import data.DanmakuType
import data.DoublePoint
import java.awt.image.BufferedImage

abstract class AbstractDanmaku {
    abstract val danmakuType : DanmakuType
    abstract val image : BufferedImage?
    abstract var pos : DoublePoint
    var isDiscarded: Boolean = false
    abstract fun tick()
}