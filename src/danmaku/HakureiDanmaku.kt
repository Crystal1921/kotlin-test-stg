package danmaku

import data.DanmakuType
import java.awt.Point
import java.awt.image.BufferedImage

class HakureiDanmaku(override val danmakuType: DanmakuType, override var pos: Point, override val image: BufferedImage?) : AbstractDanmaku() {
    constructor(pos: Point,image: BufferedImage?) : this(DanmakuType.Player,pos,image)
    override fun tick() {
        this.pos.y -= 10
    }
}