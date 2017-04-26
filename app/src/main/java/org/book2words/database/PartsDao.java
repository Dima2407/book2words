package org.book2words.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.book2words.database.Schema.PartTable;
import org.book2words.database.model.Part;

import java.util.ArrayList;
import java.util.List;


public class PartsDao {

    private SQLiteDatabase sqLiteDatabase;

    public PartsDao(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public List<Part> getPartsInPartition(long bookId, int partitionNumber, int firstVisibleParagraph) {
        List<Part> parts = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(PartTable.TABLE, null, PartTable.COLUMN_BOOK_ID + "=? AND " + PartTable.COLUMN_PARTITION_NUMBER + "=? AND " + PartTable.COLUMN_PARAGRAPH_NUMBER + ">=?", new String[]{String.valueOf(bookId), String.valueOf(partitionNumber), String.valueOf(firstVisibleParagraph)}, null, null, PartTable.COLUMN_PARAGRAPH_NUMBER, "10");
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(PartTable._ID);
            final int columnBookIdIndex = cursor.getColumnIndex(PartTable.COLUMN_BOOK_ID);
            final int columnPartitionKeyIndex = cursor.getColumnIndex(PartTable.COLUMN_PARTITION_NUMBER);
            final int columnParagraphKeyIndex = cursor.getColumnIndex(PartTable.COLUMN_PARAGRAPH_NUMBER);
            final int columnAmountOfWordsIndex = cursor.getColumnIndex(PartTable.COLUMN_AMOUNT_OF_WORDS);
            final int columnAmountOfSymbolsIndex = cursor.getColumnIndex(PartTable.COLUMN_AMOUNT_OF_SYMBOLS);
            final int columnTextIndex = cursor.getColumnIndex(PartTable.COLUMN_TEXT);
            do {
                Part part = new Part(cursor.getInt(columnIdIndex));
                part.setBookId(cursor.getLong(columnBookIdIndex));
                part.setPartitionNumber(cursor.getInt(columnPartitionKeyIndex));
                part.setParagraphNumber(cursor.getInt(columnParagraphKeyIndex));
                part.setAmountOfWords(cursor.getInt(columnAmountOfWordsIndex));
                part.setAmountOfSymbols(cursor.getInt(columnAmountOfSymbolsIndex));
                part.setText(cursor.getString(columnTextIndex));
                parts.add(part);
            } while (cursor.moveToNext());
        }
        return parts;
    }

    public void save(Iterable<Part> parts) {
        sqLiteDatabase.beginTransaction();
        try {

            ContentValues contentValues = new ContentValues();
            for(Part part : parts) {
                if (part.getId() > 0) {
                    contentValues.put(PartTable._ID, part.getId());
                }
                contentValues.put(PartTable.COLUMN_BOOK_ID, part.getBookId());
                contentValues.put(PartTable.COLUMN_PARTITION_NUMBER, part.getPartitionNumber());
                contentValues.put(PartTable.COLUMN_PARAGRAPH_NUMBER, part.getParagraphNumber());
                contentValues.put(PartTable.COLUMN_AMOUNT_OF_SYMBOLS, part.getAmountOfSymbols());
                contentValues.put(PartTable.COLUMN_AMOUNT_OF_WORDS, part.getAmountOfWords());
                contentValues.put(PartTable.COLUMN_TEXT, part.getText());
                long id = sqLiteDatabase.insertWithOnConflict(PartTable.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                part.setId(id);
            }

            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

    }

}
