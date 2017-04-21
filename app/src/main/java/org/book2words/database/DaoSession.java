package org.book2words.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;

/**
 * Created by user on 18.04.2017.
 */

public class DaoSession {

    public static final String DB_NAME = "book2word.db";
    public static final int DB_VERSION = 1;

    private final SQLiteOpenHelper curentHelper;

    @NotNull
    private LibraryBookDao libraryBookDao;
   // private PartitionsBookDao partitionsBookDao;
    private WordsFoundDao wordsFoundDao;
    private PartsDao partsDao;

    public DaoSession(Context context) {

        curentHelper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(LibraryBookDao.obtainCreateInstancesQuery());
               // db.execSQL(PartitionsBookDao.obtainCreateInstancesQuery());
                db.execSQL(WordsFoundDao.obtainCreateInstancesQuery());
                db.execSQL(PartsDao.obtainCreateInstancesQuery());

            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };

        SQLiteDatabase database = curentHelper.getWritableDatabase();
        libraryBookDao = new LibraryBookDao(database);
       // partitionsBookDao = new PartitionsBookDao(database);
        wordsFoundDao = new WordsFoundDao(database);
        partsDao = new PartsDao(database);
    }


    @NotNull
    public LibraryBookDao getLibraryBookDao() {
        return libraryBookDao;
    }

    /*public PartitionsBookDao getPartitionsBookDao() {
        return partitionsBookDao;
    }*/

    public WordsFoundDao getWordsFoundDao() {
        return wordsFoundDao;
    }

    public PartsDao getPartsDao() {
        return partsDao;
    }
}
