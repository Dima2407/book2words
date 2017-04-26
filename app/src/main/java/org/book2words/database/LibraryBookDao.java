package org.book2words.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import org.book2words.database.models.LibraryBook;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LibraryBookDao {

    private static final String TABLE_NAME = "LIBRARY_BOOKS";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_AUTHORS = "AUTHORS";
    private static final String COLUMN_WORDS_ID = "WORDS_ID";
    private static final String COLUMN_ADAPTED = "ADAPTED";
    private static final String COLUMN_CURRENT_PARTITION = "CURRENT_PARTITION";
    private static final String COLUMN_COUNT_PARTITIONS = "COUNT_PARTITIONS";
    private static final String COLUMN_WORDS_COUNT = "WORDS_COUNT";
    private static final String COLUMN_UNIQUE_WORDS_COUNT = "UNIQUE_WORDS_COUNT";
    private static final String COLUMN_UNKNOWN_WORDS_COUNT = "UNKNOWN_WORDS_COUNT";
    private static final String COLUMN_LANGUAGE = "LANGUAGE";
    private static final String COLUMN_PATH = "PATH";

    private SQLiteDatabase sqLiteDatabase;

    LibraryBookDao(SQLiteDatabase database) {
        sqLiteDatabase = database;
    }

    @NonNull
    static String obtainCreateInstancesQuery() {
        return "CREATE TABLE IF NOT EXISTS '" + TABLE_NAME + "' ('" + COLUMN_ID + "' INTEGER PRIMARY KEY, '" + COLUMN_NAME + "' TEXT NOT NULL, " +
                "'" + COLUMN_AUTHORS + "' TEXT NOT NULL, '" + COLUMN_WORDS_ID + "' TEXT, '" + COLUMN_ADAPTED + "' INTEGER NOT NULL, '" + COLUMN_CURRENT_PARTITION + "' INTEGER NOT NULL, " +
                "'" + COLUMN_COUNT_PARTITIONS + "' INTEGER NOT NULL, '" + COLUMN_WORDS_COUNT + "' INTEGER NOT NULL, '" + COLUMN_UNIQUE_WORDS_COUNT + "' INTEGER NOT NULL, " +
                "'" + COLUMN_UNKNOWN_WORDS_COUNT + "' INTEGER NOT NULL, '" + COLUMN_LANGUAGE + "' TEXT NOT NULL, '" + COLUMN_PATH + "' TEXT NOT NULL UNIQUE );";
    }

    public List<LibraryBook> getAllBooks() {
        List<LibraryBook> bookList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
                final int columnNameIndex = cursor.getColumnIndex(COLUMN_NAME);
                final int columnAuthorsIndex = cursor.getColumnIndex(COLUMN_AUTHORS);
                final int columnAdaptedIndex = cursor.getColumnIndex(COLUMN_ADAPTED);
                final int columnCurrentPartIndex = cursor.getColumnIndex(COLUMN_CURRENT_PARTITION);
                final int columnCountPartitionPartIndex = cursor.getColumnIndex(COLUMN_COUNT_PARTITIONS);
                final int columnWordCountPartIndex = cursor.getColumnIndex(COLUMN_WORDS_COUNT);
                final int columnUniqueWordCountIndex = cursor.getColumnIndex(COLUMN_UNIQUE_WORDS_COUNT);
                final int columnUnknownWordsIndex = cursor.getColumnIndex(COLUMN_UNKNOWN_WORDS_COUNT);
                final int columnLanguageIndex = cursor.getColumnIndex(COLUMN_LANGUAGE);
                final int columnPathIndex = cursor.getColumnIndex(COLUMN_PATH);
                do {
                    LibraryBook book = new LibraryBook(
                            cursor,
                            columnIdIndex,
                            columnNameIndex,
                            columnAuthorsIndex,
                            columnAdaptedIndex,
                            columnCurrentPartIndex,
                            columnCountPartitionPartIndex,
                            columnWordCountPartIndex,
                            columnUniqueWordCountIndex,
                            columnUnknownWordsIndex,
                            columnLanguageIndex,
                            columnPathIndex);
                    bookList.add(book);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bookList;
    }

    public void delete(LibraryBook book) {
        sqLiteDatabase.delete(TABLE_NAME, COLUMN_PATH + "=? OR " + COLUMN_ID + "=?", new String[]{book.getPath(), String.valueOf(book.getId())});
    }

    public void save(@NotNull LibraryBook book) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, book.getName());
        contentValues.put(COLUMN_AUTHORS, book.getAuthors());
        contentValues.put(COLUMN_ADAPTED, book.getAdapted());
        contentValues.put(COLUMN_CURRENT_PARTITION, book.getCurrentPartition());
        contentValues.put(COLUMN_COUNT_PARTITIONS, book.getCountPartitions());
        contentValues.put(COLUMN_WORDS_COUNT, book.getWordsCount());
        contentValues.put(COLUMN_UNIQUE_WORDS_COUNT, book.getUniqueWordsCount());
        contentValues.put(COLUMN_UNKNOWN_WORDS_COUNT, book.getUnknownWordsCount());
        contentValues.put(COLUMN_LANGUAGE, book.getLanguage());
        contentValues.put(COLUMN_PATH, book.getPath());

        if (book.getId() == null) {
            long id = sqLiteDatabase.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            book.setId(id);
        } else {
            sqLiteDatabase.update(TABLE_NAME, contentValues, COLUMN_ID + "=?", new String[]{String.valueOf(book.getId())});
        }
    }
}
