package rk.powermilk.cantineocatecumenale.model

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import rk.powermilk.cantineocatecumenale.util.detectOs
import rk.powermilk.cantineocatecumenale.util.openFolder
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertTrue

class OpenFolderTest {

    private val testDir = "build/test-folder"

    @BeforeEach
    fun setup() {
        mockkStatic("rk.powermilk.cantineocatecumenale.util.FileUtilsKt")
        mockkStatic(Runtime::class)
        Files.createDirectories(Path.of(testDir))
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `openFolder should call system command based on OS`() {
        val runtimeMock = mockk<Runtime>(relaxed = true)

        every { detectOs() } returns "windows"
        every { Runtime.getRuntime() } returns runtimeMock
        every { runtimeMock.exec(arrayOf("C:\\Windows\\explorer.exe", testDir)) } returns mockk()

        openFolder(testDir)

        verify { runtimeMock.exec(arrayOf("C:\\Windows\\explorer.exe", testDir)) }
    }

    @Test
    fun `openFolder should handle missing folder gracefully`() {
        val fakePath = "build/does-not-exist"
        openFolder(fakePath) // Should print message and not throw
        assertTrue(true)
    }

    @Test
    fun `openFolder should handle unsupported OS`() {
        every { detectOs() } returns "plan9"

        openFolder(testDir) // Should print unsupported OS
        assertTrue(true)
    }

    @Test
    fun `openFolder should catch IOExceptions`() {
        val runtimeMock = mockk<Runtime>(relaxed = true)

        every { detectOs() } returns "linux"
        every { Runtime.getRuntime() } returns runtimeMock
        every { runtimeMock.exec(arrayOf("xdg-open", testDir)) } throws IOException("test failure")

        openFolder(testDir)
        assertTrue(true)
    }
}
