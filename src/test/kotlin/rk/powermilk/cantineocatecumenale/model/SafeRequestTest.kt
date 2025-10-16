package rk.powermilk.cantineocatecumenale.model

import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.io.IOException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SafeRequestTest {

    private val successHtml = "<html><body>OK</body></html>"

    @BeforeTest
    fun setup() {
        mockkStatic("org.jsoup.Jsoup")
    }

    @AfterTest
    fun tearDown() {
        io.mockk.unmockkAll()
    }

    @Test
    fun `safeRequest should return document on success`() = runTest {
        every { Jsoup.connect("https://example.com").userAgent(any()).get() } returns Jsoup.parse(successHtml)
        val result = safeRequest("https://example.com")
        assertNotNull(result)
    }

    @Test
    fun `safeRequest should return null on IOException`() = runTest {
        every { Jsoup.connect("https://fail.com").userAgent(any()).get() } throws IOException("Connection failed")
        val result = safeRequest("https://fail.com")
        assertNull(result)
    }

    @Test
    fun `safeRequest should retry on 429 and then fail`() = runTest {
        every {
            Jsoup.connect("https://retry.com").userAgent(any()).get()
        } throws HttpStatusException("Too Many Requests", 429, "https://retry.com")
        val result = safeRequest("https://retry.com", retries = 2)
        assertNull(result)
    }
}
