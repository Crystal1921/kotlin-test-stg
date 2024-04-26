package thread

import javazoom.jl.decoder.JavaLayerException
import javazoom.jl.player.advanced.AdvancedPlayer
import javazoom.jl.player.FactoryRegistry
import java.io.InputStream

class MusicThread(private val file: String) : Runnable {
    private lateinit var player: AdvancedPlayer

    override fun run() {
        val isStream: InputStream? = javaClass.classLoader.getResourceAsStream(file)
        if (isStream != null) {
            try {
                player = AdvancedPlayer(isStream, FactoryRegistry.systemRegistry().createAudioDevice()).apply {
                    play()
                }
            } catch (exception: JavaLayerException) {
                exception.printStackTrace()
            }
        }
    }
}
