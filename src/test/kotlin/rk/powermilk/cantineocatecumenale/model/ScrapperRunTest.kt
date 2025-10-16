package rk.powermilk.cantineocatecumenale.model

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import rk.powermilk.cantineocatecumenale.util.downloadMp3
import rk.powermilk.cantineocatecumenale.util.openFolder
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ScrapperRunTest {

    private val mockSongUrl1 = "https://example.com/song1"
    private val mockSongUrl2 = "https://example.com/song2"

    @BeforeTest
    fun setup() {
        mockkObject(Scrapper)
        mockkStatic(::downloadMp3)
        mockkStatic(::openFolder)
        mockkStatic(::translate)

        every { Scrapper.createDirs() } returns Unit
        every { translate(any()) } answers { firstArg() }
        every { downloadMp3(any(), any(), any()) } returns Unit
        every { openFolder(any()) } returns Unit
    }

    @AfterTest
    fun tearDown() {
        io.mockk.unmockkAll()
    }

    @Test
    fun `run should execute complete scraping workflow`() = runTest {
        // Mock getAllSongLinks
        coEvery { Scrapper.getAllSongLinks() } returns listOf(mockSongUrl1, mockSongUrl2)

        // Mock fetchSongTitleAndUrl
        coEvery { Scrapper.fetchSongTitleAndUrl(mockSongUrl1) } returns
            Pair("Gloria_Mt_5_1.mp3", mockSongUrl1)
        coEvery { Scrapper.fetchSongTitleAndUrl(mockSongUrl2) } returns
            Pair("Alleluia_Gv_3_16.mp3", mockSongUrl2)

        // Execute
        Scrapper.run()

        // Verify all steps were called
        verify(exactly = 1) { Scrapper.createDirs() }
        verify(exactly = 1) { downloadMp3("Gloria_Mt_5_1.mp3", mockSongUrl1, any()) }
        verify(exactly = 1) { downloadMp3("Alleluia_Gv_3_16.mp3", mockSongUrl2, any()) }
        verify(exactly = 1) { openFolder(any()) }
    }

    @Test
    fun `run should handle empty song list`() = runTest {
        coEvery { Scrapper.getAllSongLinks() } returns emptyList()

        Scrapper.run()

        verify(exactly = 1) { Scrapper.createDirs() }
        verify(exactly = 0) { downloadMp3(any(), any(), any()) }
        verify(exactly = 1) { openFolder(any()) }
    }

    @Test
    fun `run should filter out null results from fetchSongTitleAndUrl`() = runTest {
        coEvery { Scrapper.getAllSongLinks() } returns listOf(mockSongUrl1, mockSongUrl2)
        coEvery { Scrapper.fetchSongTitleAndUrl(mockSongUrl1) } returns
            Pair("Gloria_Mt_5_1.mp3", mockSongUrl1)
        coEvery { Scrapper.fetchSongTitleAndUrl(mockSongUrl2) } returns null

        Scrapper.run()

        verify(exactly = 1) { downloadMp3("Gloria_Mt_5_1.mp3", mockSongUrl1, any()) }
        verify(exactly = 0) { downloadMp3(match { it.contains("Alleluia") }, any(), any()) }
    }
}
