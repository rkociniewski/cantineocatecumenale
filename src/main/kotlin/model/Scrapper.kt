package rk.cantineocatecumenale.model

import org.jsoup.Jsoup
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Locale
import kotlin.io.path.absolute
import kotlin.io.path.writeBytes


object Scrapper {
    private const val BASE_URL = "https://www.cantineocatecumenale.it"
    private const val LIST_URL = "$BASE_URL/lista-canti/"
    private val saveDir = "${System.getProperty("user.home")}/cantineocatecumenale"

    private fun createDirs() = try {
        Files.createDirectories(Path.of(saveDir))
        println("Directory '$saveDir' created")
    } catch (e: Exception) {
        println("Error creating directories: ${e.message}")
    }

    private fun getAllSongLinks(): Set<String> {
        var nextPageUrl: String? = LIST_URL
        val allSongsList = mutableSetOf<String>()

        while (nextPageUrl != null) {
            try {
                val doc = Jsoup.connect(nextPageUrl).get()

                // Extracting song links from the page
                val songLinks = doc.select("div[data-elementor-id=\"682\"] h1.elementor-heading-title a")
                    .map { it.attr("href").trim() }

                println("Found ${songLinks.size} songs on page: $nextPageUrl")

                allSongsList.addAll(songLinks)

                // Checking if button "Successivo >>" exists and is activate
                val nextPageElement = doc.select("a.page-numbers:contains(Successivo)").firstOrNull()
                nextPageUrl = if (nextPageElement != null && !nextPageElement.hasClass("disabled")) {
                    nextPageElement.attr("href")
                } else {
                    null // No more pages
                }
            } catch (e: Exception) {
                println("Error loading page $nextPageUrl: ${e.message}")
                break
            }
        }

        return allSongsList
    }

    private fun processTitle(title: String, subTitle: String): String {
        val translatedSubTitle = if (subTitle.isNotEmpty()) {
            val cleanedSubTitle =
                subTitle.replace("Cfr. ", "").substringBeforeLast(" - ").replace(",", "")
                    .replace("\\s*\\(\\d{1,3}\\)\\s*".toRegex(), "")
            translate(cleanedSubTitle)
        } else ""
        return sanitizeFileName("$title | $translatedSubTitle").trim().replace(Regex("_+"), "_") + ".mp3"
    }

    private fun createNameUrlSongs(songs: Set<String>) = songs.associate { songUrl ->
        val songDoc = Jsoup.connect(songUrl).get()

        val titleElement = songDoc.select("div[data-id=\"4e9b0b4\"] h1").firstOrNull()
        val subtitleElement = songDoc.select("div[data-id=\"4e9b0b4\"] h5").firstOrNull { it.text().contains("Cfr.") }

        val title = titleElement?.text()?.trim() ?: "Unknown Title"
        val subtitle = subtitleElement?.text()?.trim() ?: ""

        val processedTitle = processTitle(title, subtitle)

        val audioElement = songDoc.select("audio source[type=audio/mpeg]").firstOrNull()
            ?: songDoc.select("audio source[type=audio/wav]").firstOrNull()
        val audioUrl = audioElement?.attr("src")
        processedTitle to audioUrl
    }

    private fun downloadMp3(nameUrlSongs: Map<String, String?>) = nameUrlSongs.forEach { (name, url) ->
        if (url != null) {
            val audioFile = Paths.get(saveDir, name)

            audioFile.writeBytes(
                Jsoup.connect(url).ignoreContentType(true).execute().bodyAsBytes(),
                StandardOpenOption.CREATE
            )
            println("Downloaded: ${audioFile.absolute()}")
        } else {
            println("No audio found for: $name")
        }
    }

    private fun openFolder(path: String) {
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
        } catch (e: IOException) {
            println("Error during opening directory: ${e.message}")
        }
    }

    fun scrap() {
        createDirs()

        val allSongsList = getAllSongLinks()
        println("Found ${allSongsList.size} unique songs")
        val nameUrlSongs = createNameUrlSongs(allSongsList)
        downloadMp3(nameUrlSongs)
        openFolder(saveDir)
    }
}