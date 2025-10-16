package rk.powermilk.cantineocatecumenale

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import rk.powermilk.cantineocatecumenale.model.Scrapper
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class MainTest {

    @BeforeTest
    fun setup() {
        mockkObject(Scrapper)
        every { Scrapper.run() } returns Unit
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `main should call Scrapper run`() {
        main()

        verify(exactly = 1) { Scrapper.run() }
    }
}
