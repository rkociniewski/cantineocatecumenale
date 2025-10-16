package rk.powermilk.cantineocatecumenale.model

import org.jsoup.Jsoup
import rk.powermilk.cantineocatecumenale.model.Scrapper.parseSongLinks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParseSongLinksTest {

    @Test
    fun `parseSongLinks should extract all song links from page`() {
        val html = """
            <html>
                <body>
                    <div data-elementor-id="682">
                        <h1 class="elementor-heading-title">
                            <a href="https://example.com/song1">Song 1</a>
                        </h1>
                        <h1 class="elementor-heading-title">
                            <a href="https://example.com/song2">Song 2</a>
                        </h1>
                        <h1 class="elementor-heading-title">
                            <a href="https://example.com/song3">Song 3</a>
                        </h1>
                    </div>
                </body>
            </html>
        """

        val doc = Jsoup.parse(html)
        val result = parseSongLinks(doc)

        assertEquals(3, result.size)
        assertEquals("https://example.com/song1", result[0])
        assertEquals("https://example.com/song2", result[1])
        assertEquals("https://example.com/song3", result[2])
    }

    @Test
    fun `parseSongLinks should return empty list when no links found`() {
        val html = """
            <html>
                <body>
                    <div data-elementor-id="999">
                        <h1>No links here</h1>
                    </div>
                </body>
            </html>
        """

        val doc = Jsoup.parse(html)
        val result = parseSongLinks(doc)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseSongLinks should trim whitespace from URLs`() {
        val html = """
            <html>
                <body>
                    <div data-elementor-id="682">
                        <h1 class="elementor-heading-title">
                            <a href="  https://example.com/song1  ">Song 1</a>
                        </h1>
                    </div>
                </body>
            </html>
        """

        val doc = Jsoup.parse(html)
        val result = parseSongLinks(doc)

        assertEquals(1, result.size)
        assertEquals("https://example.com/song1", result[0])
    }

    @Test
    fun `parseSongLinks should filter out empty href attributes`() {
        val html = """
            <html>
                <body>
                    <div data-elementor-id="682">
                        <h1 class="elementor-heading-title">
                            <a href="">Empty</a>
                        </h1>
                        <h1 class="elementor-heading-title">
                            <a href="https://example.com/song1">Valid Song</a>
                        </h1>
                    </div>
                </body>
            </html>
        """

        val doc = Jsoup.parse(html)
        val result = parseSongLinks(doc)

        assertEquals(2, result.size)
        assertTrue { result[0].isEmpty() }
    }
}
