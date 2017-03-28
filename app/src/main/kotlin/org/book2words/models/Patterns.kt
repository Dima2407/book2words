package org.book2words.models

import java.util.regex.Pattern

public object Patterns {
    val WITH_APOSTROPHE = Pattern.compile("\\w+[’'](ve|re|ll|s|d|t|m)", Pattern.CASE_INSENSITIVE)

    val DUPLICATES = Pattern.compile(buildDuplicates(), Pattern.CASE_INSENSITIVE)

    val CAPITAL_WORD = Pattern.compile("[^.!?'`\"-]\\s\\b([A-Z][a-z'`’]+)\\b")

    val WORD = Pattern.compile("\\b((([a-zA-Z'`’]+-)*[a-zA-Z'`’]+){3,})\\b")

    val MAX_CAPITALS = 10

    public fun buildDuplicates(): String {
        val alphabet = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
        val builder = StringBuilder()
        for (i in alphabet.indices) {
            val character = alphabet[i]
            builder.append("(\\w*").append(character).append("{3,}\\w*)")
            if (i != alphabet.size - 1) {
                builder.append("|")
            }
        }
        return builder.toString()
    }
}