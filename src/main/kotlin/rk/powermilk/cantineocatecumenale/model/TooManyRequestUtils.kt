package rk.powermilk.cantineocatecumenale.model

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

/**
 * User Agent for GET request.
 */
private const val USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"

private const val TOO_MANY_REQUEST_STATUS_CODE = 429
private const val BACK_OFF_DELAY = 2000L

/**
 * Suspends the coroutine for a random delay between [minMs] and [maxMs] milliseconds.
 * Used to simulate human-like pauses between requests to avoid rate limiting.
 *
 * @param minMs minimum delay in milliseconds (default 500ms)
 * @param maxMs maximum delay in milliseconds (default 1500ms)
 */
suspend fun politeDelay(minMs: Long = 500, maxMs: Long = 1500) {
    delay(Random.nextLong(minMs, maxMs))
}

/**
 * Sends a safe HTTP GET request to the given [url] using Jsoup with a specified [USER_AGENT].
 * Retries the request if a 429 Too Many Requests response is encountered.
 *
 * @param url the URL to request
 * @param retries number of attempts before giving up (default is 3)
 * @return a [Document] if successful, or null if all attempts fail
 */
suspend fun safeRequest(url: String, retries: Int = 3): Document? {
    var result: Document? = null
    var attempt = 0

    while (attempt < retries && result == null) {
        try {
            result = Jsoup.connect(url).userAgent(USER_AGENT).get()
        } catch (e: HttpStatusException) {
            if (e.statusCode == TOO_MANY_REQUEST_STATUS_CODE && attempt < retries - 1) {
                val backoff = (attempt + 1) * BACK_OFF_DELAY
                logger.error(e) { "429 Too Many Requests. Retrying after ${backoff}ms..." }
                delay(backoff)
            } else {
                logger.error(e) { "Too many requests or failed: $url (${e.statusCode})" }
            }
        } catch (e: IOException) {
            logger.error(e) { "Request failed for $url: ${e.message}" }
        }
        attempt++
    }

    return result
}

