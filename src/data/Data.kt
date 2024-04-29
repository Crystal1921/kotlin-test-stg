package data

import java.awt.Point

data class Rectangle (
    val x: Int,
    val y: Int,
    val width : Int,
    val height : Int
)

data class MenuButton(
    val name : String,
    val pos  : Point,
    val area : Rectangle,
    var isPointed : Boolean,
    val id   : Int
)

data class Player(
    val id : Int,
    var pos : Point,
    var health : Int = 3,
    var bomb : Int = 3,
    var isShift : Boolean = false,
    var moveSpeed : Int = 4,
    var shootCD : Int = 5
)

enum class DanmakuType {
    Player,
    Enemy
}