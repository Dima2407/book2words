package org.book2words.models.book

import com.google.gson.annotations.SerializedName
import java.io.Serializable

public class Paragraph(
        SerializedName("k") val key: String,
        SerializedName("s") val start: Int,
        SerializedName("elf") val end: Int) : Serializable {
}