package thread

import utils.KeyboardInput
import utils.drawMiddleImage
import utils.scanImages
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferStrategy
import java.net.URI
import java.nio.file.Paths
import javax.swing.JButton
import javax.swing.JEditorPane
import javax.swing.JFrame
import javax.swing.event.HyperlinkEvent
import javax.swing.event.HyperlinkListener
import kotlin.system.exitProcess

class GameThread : JFrame(), Runnable, HyperlinkListener, MouseListener{
    private val backgroundMusic = MusicThread("sound/Eternal_Night.mp3")
    private val musicThread = Thread(backgroundMusic)
    private val gameThread = Thread(this)
    private val keyboardInput = KeyboardInput()
    private val tps : Int = 50
    private val mspt : Int = 1000 / tps
    private val moveSpeed = 3
    private val images = scanImages(Paths.get("src/texture"))

    private var position = Point()
    private var running : Boolean = false
    private var gameStarted = false
    private lateinit var bufferStrategy : BufferStrategy

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
    }

    private fun gameLoop() {
        processInput()
        renderFrame()
    }

    private fun processInput() {
        keyboardInput.poll()
        if ((keyboardInput.keyDown(KeyEvent.VK_DOWN) || keyboardInput.keyDown(KeyEvent.VK_S))) {
            position.y += moveSpeed
        }
        if ((keyboardInput.keyDown(KeyEvent.VK_UP) || keyboardInput.keyDown(KeyEvent.VK_W))) {
            position.y -= moveSpeed
        }
        if ((keyboardInput.keyDown(KeyEvent.VK_RIGHT) || keyboardInput.keyDown(KeyEvent.VK_D))) {
            position.x += moveSpeed
        }
        if ((keyboardInput.keyDown(KeyEvent.VK_LEFT) || keyboardInput.keyDown(KeyEvent.VK_A))) {
            position.x -= moveSpeed
        }
    }

    private fun renderFrame() {
        do {
            do {
                var graphics: Graphics? = null
                try {
                    graphics = bufferStrategy.drawGraphics
                    graphics.clearRect(0, 0, width, height)
                    if (gameStarted) {
                        gameRender(graphics)
                    } else {
                        startRender(graphics)
                    }
                }finally {
                    graphics?.dispose()
                }
            } while (bufferStrategy.contentsRestored())
            bufferStrategy.show()
        } while (bufferStrategy.contentsLost())
    }

    private fun gameRender(graphics: Graphics) {

    }

    private fun startRender(graphics: Graphics) {
        graphics.drawImage(images["background.png"],0,0,this)
        graphics.drawMiddleImage(images["slider.png"],width,this,100,2,1)
        graphics.drawMiddleImage(images["slider.png"],width,this,100,2,2)
    }

    fun createAndShowGUI() {
        title = "Kotlin-STG"
        val width = 640
        val height = 480
        val editorDOWN = JEditorPane()
        val editorUP = JEditorPane()
        val canvas = Canvas()
        val musicStart = JButton("Start")

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

        editorUP.contentType = "text/html"
        editorUP.isEditable = false
        editorUP.text = "<html><body style='font-family:微软雅黑;text-align:center;'><a href='https://github.com/Crystal1921' style='text-decoration:none;color:black;'>我很可爱，请给我star</a></body></html>"
        editorUP.addHyperlinkListener(this)

        canvas.background = Color.BLACK
        canvas.ignoreRepaint = false
        canvas.addMouseListener(this)
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

    }

    override fun mouseExited(e: MouseEvent) {

    }

    fun onWindowClosing(){
        exitProcess(0)
    }
}
