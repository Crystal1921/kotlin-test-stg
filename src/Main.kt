import thread.GameThread
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.FileNotFoundException
import javax.imageio.ImageIO
import javax.swing.SwingUtilities

fun main() {
    val main = GameThread()
    main.iconImage = ImageIO.read(Thread.currentThread().contextClassLoader.getResource("texture/reimu_logo.png")?.openStream() ?: throw FileNotFoundException("Default image path"))
    main.addWindowListener( object : WindowAdapter() {
        override fun windowClosing(e: WindowEvent?) {
            main.onWindowClosing()
        }
    })
    SwingUtilities.invokeLater { main.createAndShowGUI() }
}