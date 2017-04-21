package org.book2words.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import org.book2words.models.book.Paragraph;
import org.book2words.models.book.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by user on 12.04.2017.
 */

public class WordsFoundDao {

    private static final int WORD_TRANSLATED = 1;
    private static final int WORD_NOT_TRANSLATED = 0;
    public static final String TAG = WordsFoundDao.class.getSimpleName();
    private static final String TABLE_NAME = "FOUND_WORDS";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_BOOK_ID = "book_id";
    private static final String COLUMN_VALUE = "value";
    private static final String COLUMN_PARAGRAPHS = "paragraphs";
    private static final String COLUMN_TRANSLATED = "translated";

   // private static HashMap<Long, WordsFoundDao> wordsFoundDaos = new HashMap<>();
    private SQLiteDatabase sqLiteDatabase;
   // private SQLiteOpenHelper sqLiteOpenHelper;
    //private Long bookId;

    WordsFoundDao(SQLiteDatabase database) {
       /* this.bookId = bookId;
        TABLE_NAME = "FOUND_WORDS_" + bookId;
        sqLiteOpenHelper = new WordsFoundOpenHelper(context);*/
        sqLiteDatabase = database;
    }

  /*  public static WordsFoundDao getWordsFoundDao(Context context, Long bookId) {
        //Log.i(TAG, "getWordsFoundDao");
        if (!wordsFoundDaos.containsKey(bookId))
            wordsFoundDaos.put(bookId, new WordsFoundDao(context, bookId));
        return wordsFoundDaos.get(bookId);
    }*/

    public Word getWord(int id) {
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnBookIdIndex = cursor.getColumnIndex(COLUMN_BOOK_ID);
            final int columnValueIndex = cursor.getColumnIndex(COLUMN_VALUE);
            final int columnTranslatedIndex = cursor.getColumnIndex(COLUMN_TRANSLATED);

            Word word = new Word(cursor.getString(columnValueIndex));
            word.setId(cursor.getInt(columnIdIndex));
            word.setBookId(cursor.getLong(columnBookIdIndex));
            word.setTranslated(cursor.getInt(columnTranslatedIndex) == 1 ? true : false);
            setParagraphsFromDataBase(cursor, word);
            return word;
        }
        return null;
    }

    public Word getWord(String value) {
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, COLUMN_VALUE + "=?", new String[]{String.valueOf(value)}, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnBookIdIndex = cursor.getColumnIndex(COLUMN_BOOK_ID);
            final int columnValueIndex = cursor.getColumnIndex(COLUMN_VALUE);
            final int columnTranslatedIndex = cursor.getColumnIndex(COLUMN_TRANSLATED);

            Word word = new Word(cursor.getString(columnValueIndex));
            word.setId(cursor.getInt(columnIdIndex));
            word.setBookId(cursor.getLong(columnBookIdIndex));
            word.setTranslated(cursor.getInt(columnTranslatedIndex) == 1 ? true : false);
            setParagraphsFromDataBase(cursor, word);
            return word;
        }
        return null;
    }

    public List<Word> getAllWords() {
        List<Word> allWords = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnBookIdIndex = cursor.getColumnIndex(COLUMN_BOOK_ID);
            final int columnValueIndex = cursor.getColumnIndex(COLUMN_VALUE);
            final int columnTranslatedIndex = cursor.getColumnIndex(COLUMN_TRANSLATED);
            do {
                Word word = new Word(cursor.getString(columnValueIndex));
                word.setId(cursor.getInt(columnIdIndex));
                word.setBookId(cursor.getLong(columnBookIdIndex));
                word.setTranslated(cursor.getInt(columnTranslatedIndex) == 1 ? true : false);
                setParagraphsFromDataBase(cursor, word);
                allWords.add(word);
            } while (cursor.moveToNext());
        }
        // Log.i(TAG, "getAllWords");
        return allWords;
    }

    public List<Word> getAllFoundWordsInBook(Long bookId){
        List<Word> words = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, COLUMN_BOOK_ID + "=?", new String[]{String.valueOf(bookId)}, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnIdIndex = cursor.getColumnIndex(COLUMN_ID);
            final int columnBookIdIndex = cursor.getColumnIndex(COLUMN_BOOK_ID);
            final int columnValueIndex = cursor.getColumnIndex(COLUMN_VALUE);
            final int columnTranslatedIndex = cursor.getColumnIndex(COLUMN_TRANSLATED);
            do {
                Word word = new Word(cursor.getString(columnValueIndex));
                word.setId(cursor.getInt(columnIdIndex));
                word.setBookId(cursor.getLong(columnBookIdIndex));
                word.setTranslated(cursor.getInt(columnTranslatedIndex) == 1 ? true : false);
                setParagraphsFromDataBase(cursor, word);
                words.add(word);
            } while (cursor.moveToNext());
        }
        return words;
    }

    public void addWord(Word word) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_VALUE, word.getValue());
        contentValues.put(COLUMN_BOOK_ID, word.getBookId());
        StringBuilder sb = new StringBuilder();
        for (Paragraph paragraph : word.getParagraphs()) {
            sb.append(paragraph.toString()).append(";");
        }
        contentValues.put(COLUMN_PARAGRAPHS, sb.toString());
        contentValues.put(COLUMN_TRANSLATED, word.getTranslated() ? WORD_TRANSLATED : WORD_NOT_TRANSLATED);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        // Log.i(TAG, "addWord");
    }

    public void deleteWord(int id) {
        sqLiteDatabase.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteWord(String value) {
        sqLiteDatabase.delete(TABLE_NAME, COLUMN_VALUE + "=?", new String[]{String.valueOf(value)});
    }

    public void updateWord(Word word) {
        deleteWord(word.getValue());
        addWord(word);
    }

    private void setParagraphsFromDataBase(Cursor cursor, Word word) {
        String textParagraphs = cursor.getString(cursor.getColumnIndex(COLUMN_PARAGRAPHS));
        String[] arrayParagraphs = textParagraphs.split(";");
        for (String str : arrayParagraphs) {
            String[] valuesParagraph = str.split(",");
            List<Integer> values = new ArrayList<>();
            for (String s : valuesParagraph) {
                values.add(Integer.valueOf(s));
            }
            word.addParagraph(values.get(0), values.get(1), values.get(2), values.get(3));
        }
    }


  /*  public class WordsFoundOpenHelper extends SQLiteOpenHelper {


        public WordsFoundOpenHelper(Context context) {
            super(context, DaoSession.DB_NAME, null, DaoSession.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS '" + TABLE_NAME + "' ('" + COLUMN_ID + "' INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "'" + COLUMN_VALUE + "' TEXT NOT NULL UNIQUE, '" + COLUMN_PARAGRAPHS + "' TEXT,'" + COLUMN_TRANSLATED + "' INTEGER );");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onCreate(db);
        }
    }*/

    @NonNull
    static String obtainCreateInstancesQuery() {
        return "CREATE TABLE IF NOT EXISTS '" + TABLE_NAME + "' ('" + COLUMN_ID + "' INTEGER PRIMARY KEY," +
                "'" + COLUMN_VALUE + "' TEXT NOT NULL, '" + COLUMN_BOOK_ID + "' INTEGER, '" + COLUMN_PARAGRAPHS + "' TEXT,'"
                + COLUMN_TRANSLATED + "' INTEGER );";
    }
}
