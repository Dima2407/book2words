package org.book2words.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.book2words.database.model.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 20.04.2017.
 */

public class PartsDao {

    private static final String TABLE_NAME = "PARTS";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_BOOK_ID = "book_id";
    private static final String COLUMN_PARTITION_NUMBER = "partition_number";
    private static final String COLUMN_AMOUNT_OF_WORDS = "amount_of_words";
    private static final String COLUMN_AMOUNT_OF_SYMBOLS = "amount_of_symbols";
    private static final String COLUMN_TEXT = "text";

    private SQLiteDatabase sqLiteDatabase;

    public PartsDao(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public Part getPart(int id) {
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnBookIdIndex = cursor.getColumnIndex(COLUMN_BOOK_ID);
            final int columnPartitionKeyIndex = cursor.getColumnIndex(COLUMN_PARTITION_NUMBER);
            final int columnAmountOfWordsIndex = cursor.getColumnIndex(COLUMN_AMOUNT_OF_WORDS);
            final int columnAmountOfSymbolsIndex = cursor.getColumnIndex(COLUMN_AMOUNT_OF_SYMBOLS);
            final int columnTextIndex = cursor.getColumnIndex(COLUMN_TEXT);

            Part part = new Part(cursor.getInt(columnIdIndex));
            part.setBookId(cursor.getLong(columnBookIdIndex));
            part.setPartitionNumber(cursor.getInt(columnPartitionKeyIndex));
            part.setAmountOfWords(cursor.getInt(columnAmountOfWordsIndex));
            part.setAmountOfSymbols(cursor.getInt(columnAmountOfSymbolsIndex));
            part.setText(cursor.getString(columnTextIndex));
            return part;
        }
        return null;
    }

    public List<Part> getPartsInPartition(Long bookId, int partitionNumber) {
        List<Part> parts = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, COLUMN_BOOK_ID + "=? AND " + COLUMN_PARTITION_NUMBER + "=?", new String[]{String.valueOf(bookId), String.valueOf(partitionNumber)}, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnBookIdIndex = cursor.getColumnIndex(COLUMN_BOOK_ID);
            final int columnPartitionKeyIndex = cursor.getColumnIndex(COLUMN_PARTITION_NUMBER);
            final int columnAmountOfWordsIndex = cursor.getColumnIndex(COLUMN_AMOUNT_OF_WORDS);
            final int columnAmountOfSymbolsIndex = cursor.getColumnIndex(COLUMN_AMOUNT_OF_SYMBOLS);
            final int columnTextIndex = cursor.getColumnIndex(COLUMN_TEXT);
            do {
                Part part = new Part(cursor.getInt(columnIdIndex));
                part.setBookId(cursor.getLong(columnBookIdIndex));
                part.setPartitionNumber(cursor.getInt(columnPartitionKeyIndex));
                part.setAmountOfWords(cursor.getInt(columnAmountOfWordsIndex));
                part.setAmountOfSymbols(cursor.getInt(columnAmountOfSymbolsIndex));
                part.setText(cursor.getString(columnTextIndex));
                parts.add(part);
            } while (cursor.moveToNext());
        }
        return parts;
    }

    public void addPart(Part part) {
        ContentValues contentValues = new ContentValues();
        if (part.getId() > 0)
            contentValues.put(COLUMN_ID, part.getId());
        contentValues.put(COLUMN_BOOK_ID, part.getBookId());
        contentValues.put(COLUMN_PARTITION_NUMBER, part.getPartitionNumber());
        contentValues.put(COLUMN_AMOUNT_OF_SYMBOLS, part.getAmountOfSymbols());
        contentValues.put(COLUMN_AMOUNT_OF_WORDS, part.getAmountOfWords());
        contentValues.put(COLUMN_TEXT, part.getText());
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void deletePart(int id) {
        sqLiteDatabase.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<Part> getAllParts() {
        List<Part> parts = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnBookIdIndex = cursor.getColumnIndex(COLUMN_BOOK_ID);
            final int columnPartitionKeyIndex = cursor.getColumnIndex(COLUMN_PARTITION_NUMBER);
            final int columnAmountOfWordsIndex = cursor.getColumnIndex(COLUMN_AMOUNT_OF_WORDS);
            final int columnAmountOfSymbolsIndex = cursor.getColumnIndex(COLUMN_AMOUNT_OF_SYMBOLS);
            final int columnTextIndex = cursor.getColumnIndex(COLUMN_TEXT);
            do {
                Part part = new Part(cursor.getInt(columnIdIndex));
                part.setBookId(cursor.getLong(columnBookIdIndex));
                part.setPartitionNumber(cursor.getInt(columnPartitionKeyIndex));
                part.setAmountOfWords(cursor.getInt(columnAmountOfWordsIndex));
                part.setAmountOfSymbols(cursor.getInt(columnAmountOfSymbolsIndex));
                part.setText(cursor.getString(columnTextIndex));
                parts.add(part);
            } while (cursor.moveToNext());
        }
        return parts;
    }

    public void updatePart(Part part) {
        deletePart(part.getId());
        addPart(part);
    }

    @NonNull
    static String obtainCreateInstancesQuery() {
        return "CREATE TABLE IF NOT EXISTS '" + TABLE_NAME + "' ('" + COLUMN_ID + "' INTEGER PRIMARY KEY, '" + COLUMN_BOOK_ID + "' INTEGER, '" + COLUMN_PARTITION_NUMBER + "' INTEGER, " +
                "'" + COLUMN_AMOUNT_OF_WORDS + "' INTEGER, '" + COLUMN_AMOUNT_OF_SYMBOLS + "' INTEGER, '" + COLUMN_TEXT + "' TEXT NOT NULL );";
    }
}
