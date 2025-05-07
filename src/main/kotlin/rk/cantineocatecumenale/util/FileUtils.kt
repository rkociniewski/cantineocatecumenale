package rk.cantineocatecumenale.util

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.Jsoup
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Locale
import kotlin.io.path.absolute
import kotlin.io.path.writeBytes

private val logger = KotlinLogging.logger {}

/**
 * Downloads an MP3 file from the given [url] and saves it in [saveDir] with the provided [name].
 *
 * @param name the target file name to save the audio as
 * @param url the full URL to the MP3 file
 * @param saveDir the directory where the file should be saved
 */
fun downloadMp3(name: String, url: String?, saveDir: String) = if (url != null) {
    logger.info { "Downloading: $name" }
    val audioFile = Paths.get(saveDir, name)

    audioFile.writeBytes(
        Jsoup.connect(url).ignoreContentType(true).execute().bodyAsBytes(),
        StandardOpenOption.CREATE
    )
    logger.info { "Downloaded: ${audioFile.absolute()}" }
} else {
    logger.warn { "No audio found for: $name" }
}

internal fun detectOs(): String = System.getProperty("os.name").lowercase(Locale.getDefault())

/**
 * Opens the specified [path] directory in the system's file explorer.
 * Works for Windows, macOS, and Unix/Linux systems.
 *
 * @param path the directory to open
 */
fun openFolder(path: String) {
    val saveDir = Paths.get(path)
    if (!Files.exists(saveDir)) {
        println("Directory doesn't exist: $saveDir")
        return
    }

    try {
        val os = detectOs()

        when {
            os.contains("win") -> Runtime.getRuntime().exec(arrayOf("explorer", path))
            os.contains("mac") -> Runtime.getRuntime().exec(arrayOf("open", path))
            os.contains("nix") || os.contains("nux") -> Runtime.getRuntime().exec(arrayOf("xdg-open", path))
            else -> println("Unsupported OS: $os")
        }

        println("Opening directory: $path")
    } catch (e: IOException) {
        println("Error during opening directory: ${e.message}")
    }
}

