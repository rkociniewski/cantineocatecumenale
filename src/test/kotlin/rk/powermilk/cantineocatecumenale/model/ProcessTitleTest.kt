package rk.powermilk.cantineocatecumenale.model

import io.mockk.every
import io.mockk.mockkStatic
import rk.cantineocatecumenale.model.Scrapper.processTitle
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessTitleTest {

    @BeforeTest
    fun setup() {
        mockkStatic(::translate)
    }

    @AfterTest
    fun teardown() {
        io.mockk.unmockkAll()
    }

    @Test
    fun `processTitle should include title and translated subtitle`() {
        every { translate("Mt 5,1") } returns "Mt 5,1"
        val result = processTitle("Chwała", "Cfr. Mt 5,1 - coś tam")
        assertEquals("Chwala_Mt_5_1.mp3", result)
    }

    @Test
    fun `processTitle should remove empty subtitle`() {
        val result = processTitle("Bez podtytułu", "")
        assertEquals("Bez_podtytulu.mp3", result)
    }

    @Test
    fun `processTitle should normalize multiple underscores`() {
        every { translate("Mt 5,1") } returns "Mt 5,1"
        val result = processTitle("  Tytuł   ", "Cfr. Mt 5,1 - coś")
        assertEquals("Tytul_Mt_5_1.mp3", result)
    }
}
