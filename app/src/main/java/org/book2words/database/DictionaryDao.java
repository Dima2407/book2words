package org.book2words.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import org.book2words.database.model.WordDefinition;

import java.util.ArrayList;
import java.util.List;

public class DictionaryDao {

    private static final String TABLE_NAME = "VOCABULARY";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_TRANSCRIPTION = "transcription";
    private static final String COLUMN_POS = "pos";
    private static final String COLUMN_TRANSLATE = "translate";

    private SQLiteDatabase sqLiteDatabase;

    DictionaryDao(SQLiteDatabase database) {
        sqLiteDatabase = database;
    }

    @NonNull
    static String obtainCreateInstancesQuery() {
        return "CREATE TABLE IF NOT EXISTS '" + TABLE_NAME + "' ('" + COLUMN_ID + "' INTEGER PRIMARY KEY, '" + COLUMN_TEXT + "' TEXT NOT NULL, " +
                "'" + COLUMN_POS + "' TEXT NOT NULL, '" + COLUMN_TRANSCRIPTION + "' TEXT, '" + COLUMN_TRANSLATE + "' TEXT NOT NULL );" +
                "CREATE INDEX IF NOT EXISTS internal_words_index ON " + TABLE_NAME + "(" + COLUMN_TEXT + ");";
    }

    public List<WordDefinition> findWordDefinitions(String word) {
        List<WordDefinition> words = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, null, COLUMN_TEXT + "=?", new String[]{String.valueOf(word)}, null, null, null);
        if (cursor.moveToFirst()) {
            final int columnTextIndex = cursor.getColumnIndex(COLUMN_TEXT);
            final int columnTranscriptionIndex = cursor.getColumnIndex(COLUMN_TRANSCRIPTION);
            final int columnPosIndex = cursor.getColumnIndex(COLUMN_POS);
            final int columnTranslateIndex = cursor.getColumnIndex(COLUMN_TRANSLATE);
            do {
                WordDefinition wordDefinition = new WordDefinition();
                wordDefinition.setText(cursor.getString(columnTextIndex));
                wordDefinition.setPos(cursor.getString(columnPosIndex));
                wordDefinition.setTranscription(cursor.getString(columnTranscriptionIndex));
                wordDefinition.setTranslate(cursor.getString(columnTranslateIndex));
                words.add(wordDefinition);
            } while (cursor.moveToNext());
        }
        return words;
    }

    public void save(WordDefinition word) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TEXT, word.getText());
        contentValues.put(COLUMN_POS, word.getPos());
        contentValues.put(COLUMN_TRANSCRIPTION, word.getTranscription());
        contentValues.put(COLUMN_TRANSLATE, word.getTranslate());
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void save(List<WordDefinition> wordDefinitions) {
        sqLiteDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            for (WordDefinition word : wordDefinitions) {
                contentValues.put(COLUMN_TEXT, word.getText());
                contentValues.put(COLUMN_POS, word.getPos());
                contentValues.put(COLUMN_TRANSCRIPTION, word.getTranscription());
                contentValues.put(COLUMN_TRANSLATE, word.getTranslate());
                sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }
}
