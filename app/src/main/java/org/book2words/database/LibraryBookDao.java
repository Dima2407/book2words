package org.book2words.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import org.book2words.database.model.LibraryBook;
import org.book2words.models.book.Partition;
import org.book2words.models.book.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 14.04.2017.
 */

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


    public LibraryBook getBook(Long id) {
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnNameIndex = cursor.getColumnIndex(COLUMN_NAME);
            final int columnAuthorsIndex = cursor.getColumnIndex(COLUMN_AUTHORS);
            final int columnAdaptedIndex = cursor.getColumnIndex(COLUMN_ADAPTED);
            final int columnCurrentPartIndex = cursor.getColumnIndex(COLUMN_CURRENT_PARTITION);
            final int columnCountPartitionPartIndex = cursor.getColumnIndex(COLUMN_COUNT_PARTITIONS);
            final int columnWordCountPartIndex = cursor.getColumnIndex(COLUMN_WORDS_COUNT);
            final int columnUniqueWordCountIndex = cursor.getColumnIndex(COLUMN_UNIQUE_WORDS_COUNT);
            final int columnUnknownWordstIndex = cursor.getColumnIndex(COLUMN_UNKNOWN_WORDS_COUNT);
            final int columnLanguageIndex = cursor.getColumnIndex(COLUMN_LANGUAGE);
            final int columnPathIndex = cursor.getColumnIndex(COLUMN_PATH);

            LibraryBook book = new LibraryBook();
            book.setId(cursor.getLong(columnIdIndex));
            book.setName(cursor.getString(columnNameIndex));
            book.setAuthors(cursor.getString(columnAuthorsIndex));
            book.setAdapted(cursor.getInt(columnAdaptedIndex));
            book.setCurrentPartition(cursor.getInt(columnCurrentPartIndex));
            book.setCountPartitions(cursor.getInt(columnCountPartitionPartIndex));
            book.setWordsCount(cursor.getInt(columnWordCountPartIndex));
            book.setUniqueWordsCount(cursor.getInt(columnUniqueWordCountIndex));
            book.setUnknownWordsCount(cursor.getInt(columnUnknownWordstIndex));
            book.setLanguage(cursor.getString(columnLanguageIndex));
            book.setPath(cursor.getString(columnPathIndex));
            return book;
        }
        return null;
    }

    public LibraryBook getBook(String path) {
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, COLUMN_PATH + "=?", new String[]{String.valueOf(path)}, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnNameIndex = cursor.getColumnIndex(COLUMN_NAME);
            final int columnAuthorsIndex = cursor.getColumnIndex(COLUMN_AUTHORS);
            final int columnAdaptedIndex = cursor.getColumnIndex(COLUMN_ADAPTED);
            final int columnCurrentPartIndex = cursor.getColumnIndex(COLUMN_CURRENT_PARTITION);
            final int columnCountPartitionPartIndex = cursor.getColumnIndex(COLUMN_COUNT_PARTITIONS);
            final int columnWordCountPartIndex = cursor.getColumnIndex(COLUMN_WORDS_COUNT);
            final int columnUniqueWordCountIndex = cursor.getColumnIndex(COLUMN_UNIQUE_WORDS_COUNT);
            final int columnUnknownWordstIndex = cursor.getColumnIndex(COLUMN_UNKNOWN_WORDS_COUNT);
            final int columnLanguageIndex = cursor.getColumnIndex(COLUMN_LANGUAGE);
            final int columnPathIndex = cursor.getColumnIndex(COLUMN_PATH);

            LibraryBook book = new LibraryBook();
            book.setId(cursor.getLong(columnIdIndex));
            book.setName(cursor.getString(columnNameIndex));
            book.setAuthors(cursor.getString(columnAuthorsIndex));
            book.setAdapted(cursor.getInt(columnAdaptedIndex));
            book.setCurrentPartition(cursor.getInt(columnCurrentPartIndex));
            book.setCountPartitions(cursor.getInt(columnCountPartitionPartIndex));
            book.setWordsCount(cursor.getInt(columnWordCountPartIndex));
            book.setUniqueWordsCount(cursor.getInt(columnUniqueWordCountIndex));
            book.setUnknownWordsCount(cursor.getInt(columnUnknownWordstIndex));
            book.setLanguage(cursor.getString(columnLanguageIndex));
            book.setPath(cursor.getString(columnPathIndex));
            return book;
        }
        return null;
    }

    public void addBook(LibraryBook book) {
        ContentValues contentValues = new ContentValues();
        if (book.getId() != null)
            contentValues.put(COLUMN_ID, book.getId());
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
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public List<LibraryBook> getAllBooks() {
        List<LibraryBook> bookList = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnNameIndex = cursor.getColumnIndex(COLUMN_NAME);
            final int columnAuthorsIndex = cursor.getColumnIndex(COLUMN_AUTHORS);
            final int columnAdaptedIndex = cursor.getColumnIndex(COLUMN_ADAPTED);
            final int columnCurrentPartIndex = cursor.getColumnIndex(COLUMN_CURRENT_PARTITION);
            final int columnCountPartitionPartIndex = cursor.getColumnIndex(COLUMN_COUNT_PARTITIONS);
            final int columnWordCountPartIndex = cursor.getColumnIndex(COLUMN_WORDS_COUNT);
            final int columnUniqueWordCountIndex = cursor.getColumnIndex(COLUMN_UNIQUE_WORDS_COUNT);
            final int columnUnknownWordstIndex = cursor.getColumnIndex(COLUMN_UNKNOWN_WORDS_COUNT);
            final int columnLanguageIndex = cursor.getColumnIndex(COLUMN_LANGUAGE);
            final int columnPathIndex = cursor.getColumnIndex(COLUMN_PATH);
            do {
                LibraryBook book = new LibraryBook();
                book.setId(cursor.getLong(columnIdIndex));
                book.setName(cursor.getString(columnNameIndex));
                book.setAuthors(cursor.getString(columnAuthorsIndex));
                book.setAdapted(cursor.getInt(columnAdaptedIndex));
                book.setCurrentPartition(cursor.getInt(columnCurrentPartIndex));
                book.setCountPartitions(cursor.getInt(columnCountPartitionPartIndex));
                book.setWordsCount(cursor.getInt(columnWordCountPartIndex));
                book.setUniqueWordsCount(cursor.getInt(columnUniqueWordCountIndex));
                book.setUnknownWordsCount(cursor.getInt(columnUnknownWordstIndex));
                book.setLanguage(cursor.getString(columnLanguageIndex));
                book.setPath(cursor.getString(columnPathIndex));
                bookList.add(book);
            } while (cursor.moveToNext());
        }
        return bookList;
    }

    public void deleteBook(Long id) {
        sqLiteDatabase.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteBook(String path) {
        sqLiteDatabase.delete(TABLE_NAME, COLUMN_PATH + "=?", new String[]{String.valueOf(path)});
    }

    public void updateBook(LibraryBook book) {
        deleteBook(book.getPath());
        addBook(book);
    }

    public void clearBook(Long id) {
        LibraryBook book = getBook(id);
        book.setUnknownWordsCount(0);
        book.setWordsCount(0);
        book.setCountPartitions(0);
        book.setCurrentPartition(0);
        book.setUniqueWordsCount(0);
        book.setAdapted(0);
        book.setFoundWords(null);
        book.setPartitions(null);
        updateBook(book);
    }


    @NonNull
    static String obtainCreateInstancesQuery() {
        return "CREATE TABLE IF NOT EXISTS '" + TABLE_NAME + "' ('" + COLUMN_ID + "' INTEGER PRIMARY KEY, '" + COLUMN_NAME + "' TEXT NOT NULL, " +
                "'" + COLUMN_AUTHORS + "' TEXT NOT NULL, '" + COLUMN_WORDS_ID + "' TEXT, '" + COLUMN_ADAPTED + "' INTEGER NOT NULL, '" + COLUMN_CURRENT_PARTITION + "' INTEGER NOT NULL, " +
                "'" + COLUMN_COUNT_PARTITIONS + "' INTEGER NOT NULL, '" + COLUMN_WORDS_COUNT + "' INTEGER NOT NULL, '" + COLUMN_UNIQUE_WORDS_COUNT + "' INTEGER NOT NULL, " +
                "'" + COLUMN_UNKNOWN_WORDS_COUNT + "' INTEGER NOT NULL, '" + COLUMN_LANGUAGE + "' TEXT NOT NULL, '" + COLUMN_PATH + "' TEXT NOT NULL UNIQUE );";
    }
}
