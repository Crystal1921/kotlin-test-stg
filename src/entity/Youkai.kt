package entity

import data.DoublePoint
import data.Player
import data.Power
import utils.Entity
import utils.Image
import java.awt.image.BufferedImage
import kotlin.math.atan2

class Youkai (override var health: Int, override var pos: DoublePoint, override val image: BufferedImage?, private val target : Player) : AbstractEntity() {
    constructor(health: Int,pos: DoublePoint,target: Player) : this(health,pos, Image.images["reimu_mouse.png"],target)

    override fun tick() {
        if (health <= 0) {
            Entity.items.add(ItemEntity(pos, Power.Big))
            return
        }
        lifeTime ++
        pos.x ++
        if (lifeTime % 50 == 0) {
            val x = target.pos.x - pos.x
            val y = target.pos.y - pos.y
            val angle = atan2(y,x)
            Entity.danmaku.add(LittleDanmaku(pos.copy(),angle))
        }
    }
}