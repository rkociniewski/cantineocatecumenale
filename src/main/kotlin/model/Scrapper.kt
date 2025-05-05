package rk.cantineocatecumenale.model

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.jsoup.nodes.Document
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object Scrapper {
    private const val BASE_URL = "https://www.cantineocatecumenale.it"
    private const val LIST_URL = "$BASE_URL/lista-canti/"
    private val saveDir = "${System.getProperty("user.home")}/cantineocatecumenale"

    private fun createDirs() {
        try {
            val saveDirPath = Path.of(saveDir)
            if (Files.exists(saveDirPath)) {
                println("Directory '$saveDir' already exists")
                return
            }

            Files.createDirectories(saveDirPath)
            println("Directory '$saveDir' created")
        } catch (e: IOException) {
            println("Error creating directories: ${e.message}")
        }
    }

    private suspend fun fetchHtml(url: String): Document? {
        politeDelay()
        return safeRequest(url)
    }

    private fun parseSongLinks(doc: Document): List<String> =
        doc.select("div[data-elementor-id=\"682\"] h1.elementor-heading-title a")
            .mapNotNull { it.attr("href").trim() }

    private fun getNextPageUrl(doc: Document): String? {
        val next = doc.select("a.page-numbers:contains(Successivo)").firstOrNull()
        return if (next != null && !next.hasClass("disabled")) next.attr("href") else null
    }

    private suspend fun getAllSongLinks(): List<String> {
        val allLinks = mutableListOf<String>()
        var nextUrl: String? = LIST_URL

        while (nextUrl != null) {
            val doc = fetchHtml(nextUrl) ?: break
            val links = parseSongLinks(doc)
            println("Found ${links.size} songs on page: $nextUrl")
            allLinks.addAll(links)
            nextUrl = getNextPageUrl(doc)
        }
        return allLinks
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

    private suspend fun fetchSongTitleAndUrl(songUrl: String): Pair<String, String>? {
        val doc = fetchHtml(songUrl) ?: return null

        val title = doc.select("div[data-id=\"4e9b0b4\"] h1").firstOrNull()?.text()?.trim() ?: "Unknown Title"
        val subtitle = doc.select("div[data-id=\"4e9b0b4\"] h5")
            .firstOrNull { it.text().contains("Cfr.") }?.text()?.trim() ?: ""

        val fileName = processTitle(title, subtitle)
        return Pair(fileName, songUrl)
    }

    fun run() = runBlocking {
        createDirs()
        val links = getAllSongLinks()

        val semaphore = Semaphore(5)

        val results = coroutineScope {
            links.map { url ->
                async {
                    semaphore.withPermit {
                        fetchSongTitleAndUrl(url)
                    }
                }
            }.awaitAll().filterNotNull().toMap()
        }

        results.forEach { (name, url) ->
                downloadMp3(name, url, saveDir)
                println("$name => $url")
        }

        openFolder(saveDir)
    }
}