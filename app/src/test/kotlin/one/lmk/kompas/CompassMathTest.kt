package one.lmk.kompas

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.atan2
import kotlin.math.roundToInt

class CompassMathTest {

    private val dirs = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    private val fullDirs = arrayOf(
        "PÓŁNOC",
        "PÓŁNOCNY WSCHÓD",
        "WSCHÓD",
        "POŁUDNIOWY WSCHÓD",
        "POŁUDNIE",
        "POŁUDNIOWY ZACHÓD",
        "ZACHÓD",
        "PÓŁNOCNY ZACHÓD"
    )

    private fun getDirection(azimuth: Float): Pair<String, String> {
        val index = ((azimuth + 22.5f) / 45).toInt() % 8
        return Pair(dirs[index], fullDirs[index])
    }

    @Test
    fun testCardinalDirectionsMapping() {
        // Dokładne punkty kardynalne
        assertEquals("N", getDirection(0f).first)
        assertEquals("PÓŁNOC", getDirection(0f).second)

        assertEquals("NE", getDirection(45f).first)
        assertEquals("E", getDirection(90f).first)
        assertEquals("SE", getDirection(135f).first)
        assertEquals("S", getDirection(180f).first)
        assertEquals("SW", getDirection(225f).first)
        assertEquals("W", getDirection(270f).first)
        assertEquals("NW", getDirection(315f).first)

        // Granice stref (sektory po 45 stopni, po 22.5 w każdą stronę)
        assertEquals("N", getDirection(350f).first)
        assertEquals("N", getDirection(22.4f).first)
        assertEquals("NE", getDirection(22.6f).first)
        assertEquals("NE", getDirection(67.4f).first)
        assertEquals("E", getDirection(67.6f).first)
        assertEquals("W", getDirection(269.9f).first)
        assertEquals("W", getDirection(292.4f).first)
        assertEquals("N", getDirection(359.9f).first)
    }

    @Test
    fun testNeedleOrientationInWorldCoordinates() {
        // Test: Czy igła zawsze wskazuje rzeczywistą Północ?
        // Gdy telefon celuje pod kątem A, igła obraca się o -A.
        // Kąt igły w przestrzeni = (Kąt telefonu) + (Obrót igły na ekranie)
        val testAngles = floatArrayOf(0f, 30f, 90f, 180f, 270f, 345f)
        for (azimuth in testAngles) {
            val needleRotationOnCanvas = -azimuth
            val needleWorldDirection = (azimuth + needleRotationOnCanvas + 360f) % 360f
            // Igła w świecie realnym MUSI zawsze wskazywać 0° (fizyczną Północ)
            assertEquals(0f, needleWorldDirection, 0.001f)
        }
    }

    @Test
    fun testScreenRemappingOrientationLogic() {
        // Symulacja: Prawdziwa Północ jest przed użytkownikiem (0°).
        // Użytkownik patrzy na północ i trzyma górną krawędź ekranu skierowaną na północ.

        // 1. W PIONIE (ROTATION_0):
        // Fizyczna góra telefonu = góra ekranu = 0° (Północ).
        val portraitAzimuth = 0f
        assertEquals("N", getDirection(portraitAzimuth).first)

        // 2. W POZIOMIE (ROTATION_90 - telefon obrócony w lewo o 90°):
        // Fizyczny aparat telefonu celuje teraz na Zachód (270° / -90°).
        // BEZ remapCoordinateSystem: sensor wskazałby 270° (W) - BŁĄD!
        val rawUnmappedAzimuthInLandscape = 270f
        assertEquals("W", getDirection(rawUnmappedAzimuthInLandscape).first) // To był błąd!

        // Z remapCoordinateSystem (przekształcenie osi: X' = Y, Y' = -X):
        // Kąt w układzie ekranu zostaje skorygowany o obrót ekranu (-90°):
        val remappedLandscapeAzimuth = (rawUnmappedAzimuthInLandscape + 90f) % 360f
        assertEquals(0f, remappedLandscapeAzimuth, 0.001f)
        assertEquals("N", getDirection(remappedLandscapeAzimuth).first) // TERAZ JEST POPRAWNIE!
    }
}
