package rk.cantineocatecumenale.model

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Test
import rk.cantineocatecumenale.util.openFolder
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

class OpenFolderTest {

    private val testDir = "build/test-folder"

    @BeforeTest
    fun setup() {
        Files.createDirectories(Path.of(testDir))
    }

    @AfterTest
    fun tearDown() {
        io.mockk.unmockkAll()
    }

    @Test
    fun `openFolder should call system command based on OS`() {
        mockkStatic(System::class)
        mockkStatic(Runtime::class)
        val runtimeMock = mockk<Runtime>(relaxed = true)

        every { System.getProperty("os.name") } returns "Windows 10"
        every { Runtime.getRuntime() } returns runtimeMock
        every { runtimeMock.exec(arrayOf("explorer", testDir)) } returns mockk()

        openFolder(testDir)

        verify { runtimeMock.exec(arrayOf("explorer", testDir)) }
    }

    @Test
    fun `openFolder should handle missing folder gracefully`() {
        val fakePath = "build/does-not-exist"
        openFolder(fakePath) // Should print message and not throw
        assertTrue(true) // Just ensuring no exception
    }

    @Test
    fun `openFolder should handle unsupported OS`() {
        mockkStatic(System::class)
        every { System.getProperty("os.name") } returns "Plan9"

        openFolder(testDir) // Should print unsupported OS
        assertTrue(true)
    }

    @Test
    fun `openFolder should catch IOExceptions`() {
        mockkStatic(System::class)
        mockkStatic(Runtime::class)
        val runtimeMock = mockk<Runtime>()

        every { System.getProperty("os.name") } returns "Linux"
        every { Runtime.getRuntime() } returns runtimeMock
        every { runtimeMock.exec(arrayOf("xdg-open", testDir)) } throws IOException("test failure")

        openFolder(testDir) // Should catch exception and log
        assertTrue(true)
    }
}
