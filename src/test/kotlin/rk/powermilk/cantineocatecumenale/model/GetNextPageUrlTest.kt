package rk.powermilk.cantineocatecumenale.model

import org.jsoup.Jsoup
import rk.cantineocatecumenale.model.Scrapper.getNextPageUrl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetNextPageUrlTest {

    @Test
    fun `getNextPageUrl should return the next URL when available`() {
        val html = """
            <html>
              <body>
                <a class="page-numbers" href="https://example.com/page/2">Successivo</a>
              </body>
            </html>
        """
        val doc = Jsoup.parse(html)
        val result = getNextPageUrl(doc)
        assertEquals("https://example.com/page/2", result)
    }

    @Test
    fun `getNextPageUrl should return null when no Successivo link exists`() {
        val html = """
            <html>
              <body>
                <a class="page-numbers" href="#">Previous</a>
              </body>
            </html>
        """
        val doc = Jsoup.parse(html)
        val result = getNextPageUrl(doc)
        assertNull(result)
    }

    @Test
    fun `getNextPageUrl should return null when Successivo is disabled`() {
        val html = """
            <html>
              <body>
                <a class="page-numbers disabled" href="https://example.com/page/2">Successivo</a>
              </body>
            </html>
        """
        val doc = Jsoup.parse(html)
        val result = getNextPageUrl(doc)
        assertNull(result)
    }
}
