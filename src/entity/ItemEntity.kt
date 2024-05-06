package entity

import data.DoublePoint
import data.Power
import utils.getImage
import java.awt.image.BufferedImage

class ItemEntity(override var health: Int, override var pos: DoublePoint, override val image: BufferedImage?) : AbstractEntity() {
    constructor(pos: DoublePoint, power: Power) : this(1,pos, getImage(power))
    override fun tick() {
        this.pos.y += 1
    }
}