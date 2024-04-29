package danmaku

import data.DanmakuType
import java.awt.Point
import java.awt.image.BufferedImage

abstract class AbstractDanmaku {
    abstract val danmakuType : DanmakuType
    abstract var pos : Point
    abstract val image : BufferedImage?
    abstract fun tick()
}