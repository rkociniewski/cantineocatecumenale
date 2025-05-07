package rk.cantineocatecumenale.model

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import rk.cantineocatecumenale.model.Scrapper.fetchSongTitleAndUrl
import rk.cantineocatecumenale.util.fetchHtml
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FetchSongTitleAndUrlTest {

    private val html = """
        <html>
          <body>
            <div data-id="4e9b0b4">
              <h1>Gloria</h1>
              <h5>Cfr. Mt 5,1 - coś tam</h5>
            </div>
          </body>
        </html>
    """

    private lateinit var document: Document

    @BeforeTest
    fun setup() {
        mockkStatic(Jsoup::class)
        mockkStatic(::fetchHtml)
        mockkStatic(::translate)

        document = Jsoup.parse(html)

        every { translate("Mt 5,1") } returns "Mt 5,1"
        coEvery { fetchHtml("https://example.com/song") } returns document
    }

    @AfterTest
    fun tearDown() {
        io.mockk.unmockkAll()
    }

    @Test
    fun `fetchSongTitleAndUrl should return parsed title and url`() = runTest {
        val result = fetchSongTitleAndUrl("https://example.com/song")
        assertNotNull(result)
        assertEquals("Gloria_Mt_5_1.mp3", result.first)
        assertEquals("https://example.com/song", result.second)
    }
}
