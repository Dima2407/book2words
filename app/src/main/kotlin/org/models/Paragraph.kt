package org.models

public class Paragraph(val words : MutableList<String> ){

    override fun toString(): String {
        return "${words}";
    }
}