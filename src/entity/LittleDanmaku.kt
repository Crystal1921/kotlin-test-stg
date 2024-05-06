package entity

import data.DanmakuType
import data.DoublePoint
import utils.Image
import java.awt.image.BufferedImage
import kotlin.math.*

class LittleDanmaku(override val danmakuType: DanmakuType, override var pos: DoublePoint, override val image: BufferedImage?, private val angle : Double) : AbstractDanmaku() {
    constructor(pos: DoublePoint,angle: Double) : this(DanmakuType.Enemy,pos, Image.images["little_danmaku.png"],angle)
    private val moveSpeed : Int = 1
    override fun tick() {
        pos.x += moveSpeed * cos(angle)
        pos.y += moveSpeed * sin(angle)
    }
}