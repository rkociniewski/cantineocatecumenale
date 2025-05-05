package rk.cantineocatecumenale.model

import java.text.Normalizer

private val siglaMap = mapOf(
    "Gen" to "Rdz",
    "Es" to "Wj",
    "Lv" to "Kpł",
    "Nm" to "Lb",
    "Dt" to "Pwt",
    "Gs" to "Joz",
    "Gdc" to "Sdz",
    "Rt" to "Rt",
    "1 Sam" to "1 Sm",
    "2 Sam" to "2 Sm",
    "1 Re" to "1 Krl",
    "2 Re" to "2 Krl",
    "1 Cr" to "1 Krn",
    "2 Cr" to "2 Krn",
    "Esd" to "Ezd",
    "Ne" to "Ne",
    "Tb" to "Tb",
    "Gdt" to "Jdt",
    "Est" to "Est",
    "1 Mac" to "1 Mch",
    "2 Mac" to "2 Mch",
    "Gb" to "Hi",
    "Sal" to "Ps",
    "Pr" to "Prz",
    "Qo" to "Koh",
    "Ct" to "Pnp",
    "Sap" to "Mdr",
    "Sir" to "Syr",
    "Is" to "Iz",
    "Ger" to "Jr",
    "Lam" to "Lm",
    "Bar" to "Ba",
    "Ez" to "Ez",
    "Dn" to "Dn",
    "Os" to "Oz",
    "Gl" to "Jl",
    "Am" to "Am",
    "Abd" to "Ab",
    "Gn" to "Jon",
    "Mi" to "Mi",
    "Na" to "Na",
    "Ab" to "Ha",
    "Sof" to "So",
    "Ag" to "Ag",
    "Zc" to "Za",
    "Ml" to "Ml",
    "Mt" to "Mt",
    "Mc" to "Mk",
    "Lc" to "Łk",
    "Gv" to "J",
    "At" to "Dz",
    "Rm" to "Rz",
    "1 Cor" to "1 Kor",
    "2 Cor" to "2 Kor",
    "Gal" to "Ga",
    "Ef" to "Ef",
    "Fil" to "Flp",
    "Col" to "Kol",
    "1 Ts" to "1 Tes",
    "2 Ts" to "2 Tes",
    "1 Tm" to "1 Tm",
    "2 Tm" to "2 Tm",
    "Tt" to "Tt",
    "Fm" to "Flm",
    "Eb" to "Hbr",
    "Gc" to "Jk",
    "1 Pt" to "1 P",
    "2 Pt" to "2 P",
    "1 Gv" to "1 J",
    "2 Gv" to "2 J",
    "3 Gv" to "3 J",
    "Gd" to "Jud",
    "Ap" to "Ap"
)

/**
 * Sanitizes a given string to be a valid file name.
 * Removes diacritics, illegal file characters and replaces whitespace with underscores.
 *
 * @param title the raw file name string
 * @return sanitized file name
 */
fun sanitizeFileName(title: String): String {
    // Removing diacritics (e.g. é → e)
    val normalized = Normalizer.normalize(title, Normalizer.Form.NFD)
    val withoutDiacritics = normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")

    // Replace illegals characters to "_"
    val illegalChars = "[\\\\/:*?\"<>|]".toRegex()
    val sanitized = withoutDiacritics.replace(illegalChars, "_")

    // Replace space to "_" nad removing additional characters
    return sanitized.trim().replace("\\s+".toRegex(), "_")
}

/**
 * Translates Bible verse references (e.g. Mt 5,9) using a predefined abbreviation map.
 *
 * @param subtitle text containing potential Bible references
 * @return subtitle with replaced Bible abbreviations
 */
fun translate(subtitle: String): String {
    // Regular expression to match Bible references in the subtitle:
    val regex = """\b([A-Z][a-z]{1,5})\s*(\d{1,3}(?:,\d{1,3}(?:-?\d{0,3}[a-z]?)?)?)\b""".toRegex()

    // Replace Bible references with translated versions in the subtitle:
    return regex.replace(subtitle) { matchResult ->
        val sigla = matchResult.groupValues[1] // np. "Mt"
        val verses = matchResult.groupValues[2] // np. "1,18ss"
        val translatedSigla = siglaMap[sigla] ?: sigla // Translation or original sigla
        "$translatedSigla $verses"
    }.trim()
}

