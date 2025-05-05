package rk.cantineocatecumenale.model

import org.jsoup.Jsoup
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Locale
import kotlin.io.path.absolute
import kotlin.io.path.writeBytes

fun downloadMp3(name: String, url: String?, saveDir: String) = if (url != null) {
    println("Downloading: $name")
    val audioFile = Paths.get(saveDir, name)

    audioFile.writeBytes(
        Jsoup.connect(url).ignoreContentType(true).execute().bodyAsBytes(),
        StandardOpenOption.CREATE
    )
    println("Downloaded: ${audioFile.absolute()}")
} else {
    println("No audio found for: $name")
}

fun openFolder(path: String) {
    val saveDir = Paths.get(path)
    if (!Files.exists(saveDir)) {
        println("Directory doesn't exist: $saveDir")
        return
    }

    try {
        val os = System.getProperty("os.name").lowercase(Locale.getDefault())

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