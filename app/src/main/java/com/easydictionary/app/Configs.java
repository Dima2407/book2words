package com.easydictionary.app;

import org.book2dictionary.core.Provider;

import static org.book2dictionary.core.Provider.*;

public class Configs {

    private final String bookDirectory;
    private final Provider userDictionary;
    private final Provider bookDictionary;

    public Configs(String bookDirectory, Provider userDictionary, Provider bookDictionary) {
        this.bookDirectory = bookDirectory;
        this.userDictionary = userDictionary;
        this.bookDictionary = bookDictionary;
    }

    private final static Configs debugConfigs = new Configs("/sdcard/Books", ExelFile, ExelFile);

    public static String getBooksDirectory() {
        return debugConfigs.bookDirectory;
    }

    public static Provider getBookDictionaryProvider() {
        return debugConfigs.bookDictionary;
    }

    public static Provider getUserDictionaryProvider() {
        return debugConfigs.userDictionary;
    }

}
