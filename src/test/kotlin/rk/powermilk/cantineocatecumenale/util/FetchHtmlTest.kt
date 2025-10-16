package rk.powermilk.cantineocatecumenale.util

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.jsoup.Jsoup
import rk.powermilk.cantineocatecumenale.model.politeDelay
import rk.powermilk.cantineocatecumenale.model.safeRequest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FetchHtmlTest {

    private val testUrl = "https://example.com"
    private val mockHtml = "<html><body><h1>Test</h1></body></html>"

    @BeforeTest
    fun setup() {
        mockkStatic(::politeDelay)
        mockkStatic(::safeRequest)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `fetchHtml should call politeDelay and safeRequest`() = runTest {
        val expectedDoc = Jsoup.parse(mockHtml)

        coEvery { politeDelay(any(), any()) } returns Unit
        coEvery { safeRequest(testUrl) } returns expectedDoc

        val result = fetchHtml(testUrl)

        assertNotNull(result)
        assertEquals("Test", result.select("h1").text())

        coVerify(exactly = 1) { politeDelay(any(), any()) }
        coVerify(exactly = 1) { safeRequest(testUrl) }
    }

    @Test
    fun `fetchHtml should return null when safeRequest fails`() = runTest {
        coEvery { politeDelay(any(), any()) } returns Unit
        coEvery { safeRequest(testUrl) } returns null

        val result = fetchHtml(testUrl)

        assertNull(result)

        coVerify(exactly = 1) { politeDelay(any(), any()) }
        coVerify(exactly = 1) { safeRequest(testUrl) }
    }
}
