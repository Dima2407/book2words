package org.book2words.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import org.book2words.models.book.Partition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 15.04.2017.
 */

public class PartitionsBookDao {

    private static final String COLUMN_KEY = "key";
    private static final String COLUMN_PARAGRAPHS = "paragraphs";
    private static final String COLUMN_ID = "_id";
    private static final String TABLE_NAME = "PARTITIONS";

    //private static HashMap<Long, PartitionsBookDao> partitionsBookDaos = new HashMap<>();
   // private SQLiteOpenHelper openHelper;
    private SQLiteDatabase sqLiteDatabase;
   // private Long bookId;

    PartitionsBookDao(SQLiteDatabase database) {
        //this.bookId = bookId;
        //TABLE_NAME = "PARTITIONS_IN_BOOK_" + bookId;
        //openHelper = new PartitionsBookOpenHelper(context);
        sqLiteDatabase = database;
    }

  /*  public static PartitionsBookDao getInstance(Context context, Long bookId) {
        if (!partitionsBookDaos.containsKey(bookId))
            partitionsBookDaos.put(bookId, new PartitionsBookDao(context, bookId));
        return partitionsBookDaos.get(bookId);
    }*/

    public Partition getPartition(int id) {
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnKeyIndex = cursor.getColumnIndex(COLUMN_KEY);
            final int columnParagraphsIndex = cursor.getColumnIndex(COLUMN_PARAGRAPHS);

            Partition partition = new Partition(cursor.getString(columnKeyIndex));
            partition.setId(cursor.getInt(columnIdIndex));
            String[] paragraphs = cursor.getString(columnParagraphsIndex).split("\n");
            for (String paragraph : paragraphs) {
                partition.add(paragraph);
            }
            return partition;
        }
        return null;
    }

    public Partition getPartition(String key) {
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, COLUMN_KEY + "=?", new String[]{String.valueOf(key)}, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnKeyIndex = cursor.getColumnIndex(COLUMN_KEY);
            final int columnParagraphsIndex = cursor.getColumnIndex(COLUMN_PARAGRAPHS);

            Partition partition = new Partition(cursor.getString(columnKeyIndex));
            partition.setId(cursor.getInt(columnIdIndex));
            String[] paragraphs = cursor.getString(columnParagraphsIndex).split("\n");
            for (String paragraph : paragraphs) {
                partition.add(paragraph);
            }
            return partition;
        }
        return null;
    }

    public void setPartition(Partition partition) {

    }

    public void deletePartition(int id) {
        sqLiteDatabase.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<Partition> getAllPartitionsInBook() {
        List<Partition> partitions = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnKeyIndex = cursor.getColumnIndex(COLUMN_KEY);
            final int columnParagraphsIndex = cursor.getColumnIndex(COLUMN_PARAGRAPHS);
            do {
                Partition partition = new Partition(cursor.getString(columnKeyIndex));
                partition.setId(cursor.getInt(columnIdIndex));
                String[] paragraphs = cursor.getString(columnParagraphsIndex).split("\n");
                for (String paragraph : paragraphs) {
                    partition.add(paragraph);
                }
                partitions.add(partition);
            } while (cursor.moveToNext());
        }
        return partitions;
    }

    public void addPartition(Partition partition) {
        ContentValues contentValues = new ContentValues();
        if (partition.getId() != null)
            contentValues.put(COLUMN_ID, partition.getId());
        contentValues.put(COLUMN_KEY, partition.getKey());
        StringBuilder paragraphs = new StringBuilder();
        for (String paragraph : partition.getParagraphs()) {
            paragraphs.append(paragraph).append("\n");
        }
        contentValues.put(COLUMN_PARAGRAPHS, paragraphs.toString());
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }


    /*private class PartitionsBookOpenHelper extends SQLiteOpenHelper {


        public PartitionsBookOpenHelper(Context context) {
            super(context, DaoSession.DB_NAME, null, DaoSession.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS '" + TABLE_NAME + "' ('" + COLUMN_ID + "' INTEGER PRIMARY KEY, '" + COLUMN_KEY + "' TEXT NOT NULL UNIQUE, '"
                    + COLUMN_PARAGRAPHS + "' TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }*/

    @NonNull
    static String obtainCreateInstancesQuery() {
        return "CREATE TABLE IF NOT EXISTS '" + TABLE_NAME + "' ('" + COLUMN_ID + "' INTEGER PRIMARY KEY, '" + COLUMN_KEY + "' TEXT NOT NULL, '"
                + COLUMN_PARAGRAPHS + "' TEXT);";
    }
}
