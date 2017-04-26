package org.book2words.database

import android.provider.BaseColumns

class Schema {
    object WordTable : BaseColumns {
        const val TABLE = "words"
        const val COLUMN_VALUE = "value"
        const val COLUMN_TRANSLATED = "translated"
    }

    object PartTable : BaseColumns {
        const val TABLE = "parts"
        const val COLUMN_BOOK_ID = "book_id"
        const val COLUMN_PARTITION_NUMBER = "partition_number"
        const val COLUMN_PARAGRAPH_NUMBER = "paragraph_number"
        const val COLUMN_AMOUNT_OF_WORDS = "amount_of_words"
        const val COLUMN_AMOUNT_OF_SYMBOLS = "amount_of_symbols"
        const val COLUMN_TEXT = "text"
    }

    object WordLocationTable {
        const val TABLE = "word_locations"
        const val COLUMN_WORD_ID = "word_id"
        const val COLUMN_BOOK_ID = "book_id"
        const val COLUMN_CHAPTER_ID = "chapter_id"
        const val COLUMN_PARAGRAPH_ID = "paragraph_id"
        const val COLUMN_START = "start"
        const val COLUMN_END = "end"
    }

    fun setup(): List<String> {
        val queries = ArrayList<String>();
        queries.add("CREATE TABLE IF NOT EXISTS ${WordTable.TABLE} (${BaseColumns._ID} INTEGER PRIMARY KEY,${WordTable.COLUMN_VALUE} TEXT NOT NULL, ${WordTable.COLUMN_TRANSLATED} INTEGER, UNIQUE(${WordTable.COLUMN_VALUE}) );");

        queries.add("CREATE INDEX IF NOT EXISTS word_index ON ${WordTable.TABLE} (${WordTable.COLUMN_VALUE})");

        queries.add("CREATE TABLE IF NOT EXISTS ${WordLocationTable.TABLE} (${WordLocationTable.COLUMN_BOOK_ID} INTEGER NOT NULL,${WordLocationTable.COLUMN_CHAPTER_ID} INTEGER NOT NULL, ${WordLocationTable.COLUMN_PARAGRAPH_ID} INTEGER NOT NULL, ${WordLocationTable.COLUMN_WORD_ID} INTEGER NOT NULL, ${WordLocationTable.COLUMN_START} INTEGER NOT NULL, ${WordLocationTable.COLUMN_END} INTEGER NOT NULL, UNIQUE(${WordLocationTable.COLUMN_BOOK_ID},${WordLocationTable.COLUMN_CHAPTER_ID},${WordLocationTable.COLUMN_PARAGRAPH_ID},${WordLocationTable.COLUMN_WORD_ID},${WordLocationTable.COLUMN_START},${WordLocationTable.COLUMN_END}));")

        queries.add("CREATE INDEX IF NOT EXISTS word_location_index ON ${WordLocationTable.TABLE} (${WordLocationTable.COLUMN_BOOK_ID},${WordLocationTable.COLUMN_CHAPTER_ID},${WordLocationTable.COLUMN_PARAGRAPH_ID},${WordLocationTable.COLUMN_WORD_ID},${WordLocationTable.COLUMN_START},${WordLocationTable.COLUMN_END});")

        queries.add("CREATE TABLE IF NOT EXISTS ${PartTable.TABLE} (${BaseColumns._ID} INTEGER PRIMARY KEY, ${PartTable.COLUMN_BOOK_ID} INTEGER NOT NULL, ${PartTable.COLUMN_PARTITION_NUMBER} INTEGER NOT NULL, ${PartTable.COLUMN_PARAGRAPH_NUMBER} INTEGER NOT NULL,${PartTable.COLUMN_AMOUNT_OF_WORDS} INTEGER, ${PartTable.COLUMN_AMOUNT_OF_SYMBOLS} INTEGER, ${PartTable.COLUMN_TEXT} TEXT NOT NULL, UNIQUE(${PartTable.COLUMN_BOOK_ID},${PartTable.COLUMN_PARTITION_NUMBER},${PartTable.COLUMN_PARAGRAPH_NUMBER}) );");
        return queries;

    }
}
