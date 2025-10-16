package rk.powermilk.cantineocatecumenale.model

import kotlin.test.Test
import kotlin.test.assertEquals

class SongNameUtilsTest {

    @Test
    fun `translate should convert known Bible references`() {
        assertEquals("Rdz 1", translate("Gen 1"))
        assertEquals("Mt 5,1", translate("Mt 5,1"))
        assertEquals("J 3,16", translate("Gv 3,16"))
        assertEquals("Koh 2,3", translate("Qo 2,3"))
        assertEquals("Iz 53", translate("Is 53"))
    }

    @Test
    fun `translate should return original if sigla not recognized`() {
        assertEquals("Xyz 1,1", translate("Xyz 1,1"))
    }

    @Test
    fun `sanitizeFileName should remove diacritics and special characters`() {
        val input = "Zażółć gęślą jaźń.mp3"
        val expected = "Zazolc_gesla_jazn.mp3"
        assertEquals(expected, sanitizeFileName(input))
    }

    @Test
    fun `sanitizeFileName should handle extra spaces and symbols`() {
        val input = "Psalm 23/4*?.mp3"
        val expected = "Psalm_23_4_.mp3"
        assertEquals(expected, sanitizeFileName(input))
    }

    @Test
    fun `sanitizeFileName should normalize multiple underscores`() {
        val input = "  Test   name   .mp3"
        val expected = "Test_name_.mp3"
        assertEquals(expected, sanitizeFileName(input))
    }
}
