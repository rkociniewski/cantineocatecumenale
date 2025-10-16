package rk.powermilk.cantineocatecumenale.util

import org.jsoup.nodes.Document
import rk.powermilk.cantineocatecumenale.model.politeDelay
import rk.powermilk.cantineocatecumenale.model.safeRequest

/** Loads and parses the HTML from a URL with delay and retries.
 * @param url link to be loaded
 * @return [Document] of page
 */
internal suspend fun fetchHtml(url: String): Document? {
    politeDelay()
    return safeRequest(url)
}
