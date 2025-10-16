package rk.powermilk.cantineocatecumenale.model

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.jsoup.nodes.Document
import rk.powermilk.cantineocatecumenale.util.downloadMp3
import rk.powermilk.cantineocatecumenale.util.fetchHtml
import rk.powermilk.cantineocatecumenale.util.openFolder
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * A web scraper for downloading Neocatechumenal songs from the cantineocatecumenale.it website.
 */
object Scrapper {
    private val logger = KotlinLogging.logger {}
    private const val SEMAPHORE_COUNT = 5
    private const val BASE_URL = "https://www.cantineocatecumenale.it"
    private const val LIST_URL = "$BASE_URL/lista-canti/"
    private val saveDir = "${System.getProperty("user.home")}/cantineocatecumenale"

    /** Adds a random delay to avoid being rate-limited. */
    internal fun createDirs() {
        try {
            val saveDirPath = Path.of(saveDir)
            if (Files.exists(saveDirPath)) {
                logger.info { "Directory '$saveDir' already exists" }
                return
            }

            Files.createDirectories(saveDirPath)
            logger.info { "Directory '$saveDir' created" }
        } catch (e: IOException) {
            logger.error(e) { "Error creating directories: ${e.message}" }
        }
    }

    /** Extracts links to individual songs from the page document.
     * @param doc page where a link is searching for.
     * @return song links
     */
    internal fun parseSongLinks(doc: Document): List<String> =
        doc.select("div[data-elementor-id=\"682\"] h1.elementor-heading-title a")
            .mapNotNull { it.attr("href").trim() }

    /** Finds the link to the next page of songs, if available.
     * @param doc page where a link is searching for.
     * @return next page link
     */
    internal fun getNextPageUrl(doc: Document): String? {
        val next = doc.select("a.page-numbers:contains(Successivo)").firstOrNull()
        return if (next != null && !next.hasClass("disabled")) next.attr("href") else null
    }

    /** Crawls through paginated song list pages to gather all song URLs. */
    internal suspend fun getAllSongLinks(): List<String> {
        val allLinks = mutableListOf<String>()
        var nextUrl: String? = LIST_URL

        while (nextUrl != null) {
            val doc = fetchHtml(nextUrl) ?: break
            val links = parseSongLinks(doc)
            logger.info { "Found ${links.size} songs on page: $nextUrl" }
            allLinks.addAll(links)
            nextUrl = getNextPageUrl(doc)
        }
        return allLinks
    }

    /** Combines and processes title and subtitle into a safe file name.
     *
     * @param title title of song
     * @param subTitle subtitle of this song, mostly Bible sigla
     * @return safe file name
     */
    internal fun processTitle(title: String, subTitle: String): String {
        val translatedSubTitle = if (subTitle.isNotEmpty()) {
            val cleanedSubTitle = subTitle
                .replace("Cfr. ", "")
                .substringBeforeLast(" - ")
                .replace("\\s*\\(\\d{1,3}\\)\\s*".toRegex(), "")
            translate(cleanedSubTitle)
        } else ""
        return sanitizeFileName("$title | $translatedSubTitle").trim().replace(Regex("_+"), "_") + ".mp3"
    }

    /** Retrieves the song title and audio URL from the song detail page.
     *
     * @param songUrl audio URL from the song detail page
     * @return [Pair] of song title and audio URL
     */
    internal suspend fun fetchSongTitleAndUrl(songUrl: String): Pair<String, String>? {
        val doc = fetchHtml(songUrl) ?: return null

        val title = doc.select("div[data-id=\"4e9b0b4\"] h1").firstOrNull()?.text()?.trim() ?: "Unknown Title"
        val subtitle = doc.select("div[data-id=\"4e9b0b4\"] h5")
            .firstOrNull { it.text().contains("Cfr.") }?.text()?.trim() ?: ""

        val fileName = processTitle(title, subtitle)
        return Pair(fileName, songUrl)
    }

    /** Main entry point. Collects all songs and downloads them to the target folder. */
    fun run() = runBlocking {
        createDirs()
        val links = getAllSongLinks()

        val semaphore = Semaphore(SEMAPHORE_COUNT)

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
            logger.info { "$name => $url" }
        }

        openFolder(saveDir)
    }
}
