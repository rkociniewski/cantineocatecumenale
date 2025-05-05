package rk.cantineocatecumenale.model

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

/**
 * User Agent for GET request.
 */
private const val userAgent =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"

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
 * Sends a safe HTTP GET request to the given [url] using Jsoup with a specified [userAgent].
 * Retries the request if a 429 Too Many Requests response is encountered.
 *
 * @param url the URL to request
 * @param retries number of attempts before giving up (default is 3)
 * @return a [Document] if successful, or null if all attempts fail
 */
suspend fun safeRequest(url: String, retries: Int = 3): Document? {
    repeat(retries) { attempt ->
        try {
            return Jsoup.connect(url).userAgent(userAgent).get()
        } catch (e: HttpStatusException) {
            if (e.statusCode == 429 && attempt < retries - 1) {
                val backoff = (attempt + 1) * 2000L
                logger.error(e) { "429 Too Many Requests. Retrying after ${backoff}ms..." }
                delay(backoff)
            } else {
                logger.error(e) { "Too many requests or failed: $url (${e.statusCode})" }
                return null
            }
        } catch (e: Exception) {
            logger.error(e) { "Request failed for $url: ${e.message}" }
            return null
        }
    }
    return null
}
