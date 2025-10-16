package rk.powermilk.cantineocatecumenale.model

import io.mockk.coEvery
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.jsoup.Jsoup
import rk.powermilk.cantineocatecumenale.model.Scrapper.getAllSongLinks
import rk.powermilk.cantineocatecumenale.util.fetchHtml
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllSongLinksTest {

    private val htmlPage1 = """
        <html>
            <body>
                <div data-elementor-id="682">
                    <h1 class="elementor-heading-title">
                        <a href="https://example.com/song1">Song 1</a>
                    </h1>
                </div>
                <a class="page-numbers" href="https://example.com/page/2">Successivo</a>
            </body>
        </html>
    """

    private val htmlPage2 = """
        <html>
            <body>
                <div data-elementor-id="682">
                    <h1 class="elementor-heading-title">
                        <a href="https://example.com/song2">Song 2</a>
                    </h1>
                </div>
            </body>
        </html>
    """

    @BeforeTest
    fun setup() {
        mockkStatic(::fetchHtml)
        coEvery { fetchHtml("https://www.cantineocatecumenale.it/lista-canti/") } returns Jsoup.parse(htmlPage1)
        coEvery { fetchHtml("https://example.com/page/2") } returns Jsoup.parse(htmlPage2)
    }

    @AfterTest
    fun tearDown() {
        io.mockk.unmockkAll()
    }

    @Test
    fun `getAllSongLinks should return all song links across pages`() = runTest {
        val result = getAllSongLinks()
        assertEquals(2, result.size)
        assertEquals("https://example.com/song1", result[0])
        assertEquals("https://example.com/song2", result[1])
    }
}
