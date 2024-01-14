package enzzom.hexemoji.utils

object StringUtils {

    /**
     * Parses a given text into separate unicode characters.
     */
    fun unescapeString(escapedString: String): String {
        if ("\\u" !in escapedString) return escapedString

        val unicodeTokenPattern = Regex("(?<=\\\\u)[0-9a-fA-F]{4}")

        val unescapedString = unicodeTokenPattern.findAll(escapedString).map {
            it.value.toInt(16).toChar()
        }.joinToString(separator = "")

        return unescapedString
    }
}