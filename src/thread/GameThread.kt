package thread

import danmaku.AbstractDanmaku
import danmaku.HakureiDanmaku
import data.MenuButton
import data.Player
import data.Rectangle
import utils.*
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferStrategy
import java.net.URI
import java.nio.file.Paths
import java.util.function.Consumer
import javax.swing.JButton
import javax.swing.JEditorPane
import javax.swing.JFrame
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener
import kotlin.system.exitProcess

class GameThread : JFrame(), Runnable, HyperlinkListener, MouseListener, MouseMotionListener{
    private val backgroundMusic = MusicThread("sound/Eternal_Night.mp3")
    private val images = initImages(Paths.get("src/texture"))
    private val musicThread = Thread(backgroundMusic)
    private val gameThread = Thread(this)
    private val keyboardInput = KeyboardInput()
    private val tps : Int = 100
    private val mspt : Int = 1000 / tps
    private val width = 640
    private val height = 480
    private val danmaku : MutableList<AbstractDanmaku> = mutableListOf()
    private val playerA = Player(0,Point(width / 2 - 50, height / 2 + 100))
    private val playerB = Player(0,Point(width / 2 + 50, height / 2 + 100))

    private var mousePosition = Point()
    private var running : Boolean = false
    private var gameStarted = false
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
    }

    private fun processInput() {
        keyboardInput.poll()
        playerA.isShift = keyboardInput.keyDown(KeyEvent.VK_SHIFT)
        playerB.isShift = keyboardInput.keyDown(KeyEvent.VK_CONTROL)
        playerA.moveSpeed = if (playerA.isShift) 2 else 4
        playerB.moveSpeed = if (playerB.isShift) 2 else 4
        if ((keyboardInput.keyDown(KeyEvent.VK_S)) && playerA.pos.y < height - 16) {
            playerA.pos.y += playerA.moveSpeed
        }
        if ((keyboardInput.keyDown(KeyEvent.VK_W)) && playerA.pos.y > 16) {
            playerA.pos.y -= playerA.moveSpeed
        }
        if ((keyboardInput.keyDown(KeyEvent.VK_D)) && playerA.pos.x < width - 24) {
            playerA.pos.x += playerA.moveSpeed
        }
        if ((keyboardInput.keyDown(KeyEvent.VK_A)) && playerA.pos.x > 24) {
            playerA.pos.x -= playerA.moveSpeed
        }
        if ((keyboardInput.keyDown(KeyEvent.VK_DOWN)) && playerB.pos.y < height - 16) {
            playerB.pos.y += playerB.moveSpeed
        }
        if ((keyboardInput.keyDown(KeyEvent.VK_UP)) && playerB.pos.y > 16) {
            playerB.pos.y -= playerB.moveSpeed
        }
        if ((keyboardInput.keyDown(KeyEvent.VK_RIGHT)) && playerB.pos.x < width - 24) {
            playerB.pos.x += playerB.moveSpeed
        }
        if ((keyboardInput.keyDown(KeyEvent.VK_LEFT)) && playerB.pos.x > 24) {
            playerB.pos.x -= playerB.moveSpeed
        }
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
        danmaku.forEach(Consumer { graphics.drawPosImage(it.image,this,it.pos.x,it.pos.y) })
        graphics.color = Color.YELLOW
        graphics.fillRect(playerB.pos.x - 3, 0, 6 , playerB.pos.y)
        graphics.color = Color.WHITE
        graphics.fillRect(playerB.pos.x - 2, 0, 4 , playerB.pos.y)
        graphics.drawPosImage(images["reimu.png"],this,playerA.pos.x,playerA.pos.y)
        if (playerA.isShift) graphics.drawPosImage(images["judge_point.png"],this,playerA.pos.x,playerA.pos.y)
        graphics.drawPosImage(images["marisa.png"],this,playerB.pos.x,playerB.pos.y)
        if (playerB.isShift) graphics.drawPosImage(images["judge_point.png"],this,playerB.pos.x,playerB.pos.y)
    }

    private fun gamingLogic() {
        playerA.shootCD --
        if (playerA.shootCD == 0) {
            danmaku.add(HakureiDanmaku(Point(playerA.pos.x,playerA.pos.y),images["hakurei_danmaku.png"]))
            playerA.shootCD = 5
        }
        danmaku.forEach(Consumer { it.tick() })
        danmaku.filter { it.pos.y > 0 }
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

        editorDOWN.layout = FlowLayout()
        editorDOWN.preferredSize = Dimension(width, 40)
        editorDOWN.add(musicStart)
        editorDOWN.add(musicStop)
        editorUP.isVisible = true

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

    override fun mouseClicked(e: MouseEvent) {
        val selected = buttons.filter{it.isPointed}
        if (selected.size == 1) {
            val selectedButton = selected.first()
            when (selectedButton.id) {
                0 -> gameStarted = true
                1 -> buttons[1] = MenuButton("Co-op Mode", Point(230,150), Rectangle(230,150 - 24, 150,32),false,2)
                2 -> buttons[1] = MenuButton("Single Mode", Point(230,150), Rectangle(230,150 - 24, 170,32),false,1)
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
}
