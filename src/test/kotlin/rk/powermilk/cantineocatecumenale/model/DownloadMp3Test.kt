package rk.powermilk.cantineocatecumenale.model

import io.mockk.every
import io.mockk.mockkStatic
import org.jsoup.Connection
import org.jsoup.Jsoup
import rk.cantineocatecumenale.util.downloadMp3
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.readBytes
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DownloadMp3Test {

    private val dummyBytes = "dummy audio".toByteArray()
    private val dummyUrl = "https://example.com/test.mp3"
    private val fileName = "test.mp3"
    private val saveDir = "build/test-output"

    @BeforeTest
    fun setup() {
        Files.createDirectories(Paths.get(saveDir))
        mockkStatic("org.jsoup.Jsoup")
        mockkStatic("kotlin.io.path.PathsKt__PathUtilsKt")
        val mockConnection = io.mockk.mockk<Connection>()
        every { Jsoup.connect(dummyUrl) } returns mockConnection
        every { mockConnection.ignoreContentType(true) } returns mockConnection
        every { mockConnection.execute().bodyAsBytes() } returns dummyBytes
    }

    @AfterTest
    fun cleanup() {
        io.mockk.unmockkAll()
        Files.deleteIfExists(Paths.get(saveDir, fileName))
    }

    @Test
    fun `downloadMp3 should save file when url is valid`() {
        downloadMp3(fileName, dummyUrl, saveDir)
        val outputFile = Paths.get(saveDir, fileName)
        assertTrue(Files.exists(outputFile))
        assertContentEquals(dummyBytes, outputFile.readBytes())
    }

    @Test
    fun `downloadMp3 should print message when url is null`() {
        downloadMp3(fileName, null, saveDir)
        val outputFile = Paths.get(saveDir, fileName)
        assertFalse(Files.exists(outputFile))
    }
}
