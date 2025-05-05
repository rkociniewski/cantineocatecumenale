package rk.cantineocatecumenale.model

import kotlinx.coroutines.delay
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.random.Random

private const val userAgent =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"

suspend fun politeDelay(minMs: Long = 500, maxMs: Long = 1500) {
    delay(Random.nextLong(minMs, maxMs))
}

suspend fun safeRequest(url: String, retries: Int = 3): Document? {
    repeat(retries) { attempt ->
        try {
            return Jsoup.connect(url).userAgent(userAgent).get()
        } catch (e: HttpStatusException) {
            if (e.statusCode == 429 && attempt < retries - 1) {
                val backoff = (attempt + 1) * 2000L
                println("429 Too Many Requests. Retrying after ${backoff}ms...")
                delay(backoff)
            } else {
                println("Too many requests or failed: $url (${e.statusCode})")
                return null
            }
        } catch (e: Exception) {
            println("Request failed for $url: ${e.message}")
            return null
        }
    }
    return null
}