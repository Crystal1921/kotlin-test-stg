package entity

import data.DanmakuType
import data.DoublePoint
import utils.Image
import utils.nearestEntity
import java.awt.image.BufferedImage
import java.lang.Math.toRadians
import kotlin.math.*

class HakureiDanmaku(override val danmakuType: DanmakuType, override var pos: DoublePoint, override val image: BufferedImage?) : AbstractDanmaku() {
    constructor(pos: DoublePoint) : this(DanmakuType.Player,pos, Image.images["hakurei_danmaku.png"])
    private var target : AbstractEntity? = null
    private var angle = toRadians(-90.0)
    private var moveSpeed = 6.0
    override fun tick() {
        target = nearestEntity(this.pos)
        if (target != null) {
            val x = target!!.pos.x - pos.x
            val y = target!!.pos.y - pos.y
            val targetAngle = atan2(y,x)
            val deltaAngle = angle - targetAngle
            if (abs(deltaAngle) < toRadians(1.0)) {
                angle = targetAngle
            } else if (deltaAngle > 0){
                angle -= toRadians(1.0)
            } else {
                angle += toRadians(1.0)
            }
        }
        pos.x += moveSpeed * cos(angle)
        pos.y += moveSpeed * sin(angle)
    }
}