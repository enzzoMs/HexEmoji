package enzzom.hexemoji.utils

object StringUtils {

    /**
     * Parses a given text into separate unicode characters.
     */
    fun unescapeString(escapedString: String): String {
        if ("\\u" !in escapedString) return escapedString

        var unescapedString = ""

        val unicodeTokenRegex = Regex("(?<=\\\\u)[0-9a-fA-F]{4}")
        unicodeTokenRegex.findAll(escapedString).forEach {
            unescapedString += it.value.toInt(16).toChar()
        }

        return unescapedString
    }
}