package thread

import data.DoublePoint
import entity.HakureiDanmaku
import data.MenuButton
import data.Player
import data.Rectangle
import entity.Youkai
import utils.*
import utils.Image
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferStrategy
import java.net.URI
import java.util.*
import java.util.function.Consumer
import javax.swing.JButton
import javax.swing.JEditorPane
import javax.swing.JFrame
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener
import kotlin.system.exitProcess

class GameThread : JFrame(), Runnable, HyperlinkListener, MouseListener, MouseMotionListener{
    private val backgroundMusic = MusicThread("sound/Eternal_Night.mp3")
    private val musicThread = Thread(backgroundMusic)
    private val gameThread = Thread(this)
    private val keyboardInput = KeyboardInput()
    private val random : Random = Random()
    private val tps : Int = 100
    private val mspt : Int = 1000 / tps
    private val width = 640
    private val height = 480
    private val gameArea = Rectangle(0,0,width,height)
    private val playerA = Player(0,Point(width / 2 - 50, height / 2 + 100))
    private val playerB = Player(0,Point(width / 2 + 50, height / 2 + 100))

    private var mousePosition = Point()
    private var running : Boolean = false
    private var gameStarted = false
    private var totalTime = 0
    private var gameTotalTime = 0
    private lateinit var bufferStrategy : BufferStrategy
    private lateinit var buttons : MutableList<MenuButton>

    override fun run() {
        running = true
        var currentTime = System.currentTimeMillis()
        var lastTime = currentTime
        var msPerFrame : Long
        while (running) {
            gameLoop()
            currentTime = System.currentTimeMillis()
            msPerFrame = (currentTime - lastTime)
            if (msPerFrame < mspt) {
                Thread.sleep(mspt - msPerFrame)
            }
            lastTime = System.currentTimeMillis()
        }
        exitProcess(0)
    }

    private fun gameLoop() {
        processInput()
        renderFrame()
        totalTime++
    }

    private fun processInput() {
        keyboardInput.poll()
        //判断玩家是否低速移动
        playerA.isShift = keyboardInput.keyDown(KeyEvent.VK_SHIFT)
        playerB.isShift = keyboardInput.keyDown(KeyEvent.VK_CONTROL)
        playerA.moveSpeed = if (playerA.isShift) 2 else 4
        playerB.moveSpeed = if (playerB.isShift) 2 else 4
        //处理玩家按键移动
        movePlayer(KeyEvent.VK_S, playerA, playerA.moveSpeed)
        movePlayer(KeyEvent.VK_W, playerA, playerA.moveSpeed)
        movePlayer(KeyEvent.VK_D, playerA, playerA.moveSpeed)
        movePlayer(KeyEvent.VK_A, playerA, playerA.moveSpeed)
        movePlayer(KeyEvent.VK_DOWN, playerB, playerB.moveSpeed)
        movePlayer(KeyEvent.VK_UP, playerB, playerB.moveSpeed)
        movePlayer(KeyEvent.VK_RIGHT, playerB, playerB.moveSpeed)
        movePlayer(KeyEvent.VK_LEFT, playerB, playerB.moveSpeed)
    }

    private fun renderFrame() {
        do {
            do {
                var graphics: Graphics? = null
                try {
                    graphics = bufferStrategy.drawGraphics
                    graphics.color = Color.WHITE
                    graphics.font = getFont(32)
                    graphics.clearRect(0, 0, width, height)
                    if (gameStarted) {
                        gamingScreen(graphics)
                        gamingLogic()
                        enemyLogic()
                    } else {
                        startScreen(graphics)
                    }
                }finally {
                    graphics?.dispose()
                }
            } while (bufferStrategy.contentsRestored())
            bufferStrategy.show()
        } while (bufferStrategy.contentsLost())
    }

    private fun gamingScreen(graphics: Graphics) {
        //渲染弹幕
        Entity.danmaku.forEach(Consumer { graphics.drawPosImage(it.image,this,it.pos.x.toInt(),it.pos.y.toInt()) })
        //渲染敌方实体
        Entity.entities.forEach(Consumer { graphics.drawPosImage(it.image,this,it.pos.x.toInt(),it.pos.y.toInt()) })
        //渲染魔理莎激光
        graphics.color = Color.YELLOW
        graphics.fillRect(playerB.pos.x - 3, 0, 6 , playerB.pos.y)
        graphics.color = Color.WHITE
        graphics.fillRect(playerB.pos.x - 2, 0, 4 , playerB.pos.y)
        //渲染自机
        graphics.drawPosImage(Image.images["reimu.png"],this,playerA.pos.x,playerA.pos.y)
        if (playerA.isShift) graphics.drawPosImage(Image.images["judge_point.png"],this,playerA.pos.x,playerA.pos.y)
        graphics.drawPosImage(Image.images["marisa.png"],this,playerB.pos.x,playerB.pos.y)
        if (playerB.isShift) graphics.drawPosImage(Image.images["judge_point.png"],this,playerB.pos.x,playerB.pos.y)
    }

    private fun gamingLogic() {
        //灵梦发射符札
        playerA.shootCD --
        if (playerA.shootCD == 0) {
            Entity.danmaku.add(HakureiDanmaku(DoublePoint(playerA.pos.x.toDouble(),playerA.pos.y.toDouble())))
            playerA.shootCD = playerA.shootSpeed
        }
        Entity.danmaku.forEach(Consumer { it.tick() })
        Entity.danmaku.removeIf { !isInArea(gameArea,it.pos.x.toInt(),it.pos.y.toInt()) }
        Entity.entities.forEach(Consumer { it.tick() })
        Entity.entities.removeIf { !isInArea(gameArea,it.pos.x.toInt(),it.pos.y.toInt()) || it.health < 0}
        gameTotalTime ++
    }

    private fun enemyLogic() {
        if (gameTotalTime == 100) {
            Entity.entities.add(Youkai(10,DoublePoint(100.0,100.0),playerA))
        }
    }

    private fun startScreen(graphics: Graphics) {
        isButtonsInArea(buttons,mousePosition.x,mousePosition.y)
        buttons.forEach(Consumer { button -> graphics.drawButton(button)})
    }

    fun createAndShowGUI() {
        title = "Kotlin-STG"
        val editorDOWN = JEditorPane()
        val editorUP = JEditorPane()
        val canvas = Canvas()
        val musicStart = JButton("Start")
        this.buttons = initButton()
        //初始化底部bgm按钮
        musicStart.addActionListener {
            if(!musicThread.isAlive){
                musicThread.start()
            }
        }
        val musicStop = JButton("Stop")
        musicStop.addActionListener {
            if(musicThread.isAlive){
                musicThread.interrupt()
            }
        }
        //初始化底部
        editorDOWN.layout = FlowLayout()
        editorDOWN.preferredSize = Dimension(width, 40)
        editorDOWN.add(musicStart)
        editorDOWN.add(musicStop)
        editorUP.isVisible = true
        //初始化顶部广告（我很可爱，请给我star
        editorUP.contentType = "text/html"
        editorUP.text = "<html><body style='font-family:微软雅黑;text-align:center;'><a href='https://github.com/Crystal1921' style='text-decoration:none;color:black;'>我很可爱，请给我star</a></body></html>"
        editorUP.addHyperlinkListener(this)
        editorUP.isEditable = false
        editorUP.isVisible = true

        canvas.background = Color.BLACK
        canvas.ignoreRepaint = false
        canvas.addMouseListener(this)
        canvas.addMouseMotionListener(this)
        canvas.setSize(width, height)
        canvas.addKeyListener(keyboardInput)

        ignoreRepaint = true
        isVisible = true
        //布局
        contentPane.add(canvas,BorderLayout.CENTER)
        contentPane.add(editorUP, BorderLayout.NORTH)
        contentPane.add(editorDOWN, BorderLayout.SOUTH)

        pack()

        canvas.createBufferStrategy(2)
        bufferStrategy = canvas.bufferStrategy
        canvas.requestFocus()

        gameThread.start()
    }

    override fun hyperlinkUpdate(event: HyperlinkEvent) {
        if (event.eventType == HyperlinkEvent.EventType.ACTIVATED) {
            Desktop.getDesktop().browse(URI(event.url.toString()))
        }
    }

    override fun mousePressed(e: MouseEvent) {

    }

    override fun mouseReleased(e: MouseEvent) {

    }

    override fun mouseEntered(e: MouseEvent) {

    }
    //处理玩家鼠标点击事件
    override fun mouseClicked(e: MouseEvent) {
        val selected = buttons.filter{it.isPointed}
        if (selected.size == 1) {
            val selectedButton = selected.first()
            when (selectedButton.id) {
                0 -> {
                    gameStarted = true
                }
                1 -> buttons[1] = MenuButton("Co-op Mode", Point(230,150), Rectangle(230,150 - 24, 150,32),false,2)
                2 -> buttons[1] = MenuButton("Single Mode", Point(230,150), Rectangle(230,150 - 24, 170,32),false,1)
                3 -> exitProcess(0)
                else -> exitProcess(0)
            }
        }
    }

    override fun mouseExited(e: MouseEvent) {

    }

    override fun mouseMoved(e: MouseEvent?) {
        if (e != null) {
            mousePosition.x = e.x
            mousePosition.y = e.y
        }
    }

    override fun mouseDragged(e: MouseEvent) {

    }

    fun onWindowClosing(){
        exitProcess(0)
    }

    private fun movePlayer(keyCode: Int, player: Player, moveSpeed: Int) {
        val minX = 24
        val maxX = width - 24
        val minY = 16
        val maxY = height - 16

        if (keyboardInput.keyDown(keyCode)) {
            when (keyCode) {
                KeyEvent.VK_S, KeyEvent.VK_DOWN -> if (player.pos.y < maxY) player.pos.y += moveSpeed
                KeyEvent.VK_W, KeyEvent.VK_UP -> if (player.pos.y > minY) player.pos.y -= moveSpeed
                KeyEvent.VK_D, KeyEvent.VK_RIGHT -> if (player.pos.x < maxX) player.pos.x += moveSpeed
                KeyEvent.VK_A, KeyEvent.VK_LEFT -> if (player.pos.x > minX) player.pos.x -= moveSpeed
            }
        }
    }
}
