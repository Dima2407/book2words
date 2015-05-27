package org.b2w.generator;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class B2WDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(8, "org.book2words.dao");
        schema.enableKeepSectionsByDefault();

        Entity book = schema.addEntity("LibraryBook");
        book.addIdProperty();
        book.addStringProperty("name").notNull();
        book.addStringProperty("authors").notNull();
        book.addBooleanProperty("adapted").notNull();
        book.addIntProperty("currentPartition").notNull();
        book.addIntProperty("countPartitions").notNull();
        book.addIntProperty("allWords").notNull();
        book.addIntProperty("uniqueWords").notNull();
        book.addIntProperty("capitalWords").notNull();
        book.addIntProperty("unknownWords").notNull();
        book.addBooleanProperty("read").notNull();
        book.addStringProperty("path").notNull().unique();
        book.implementsInterface("Parcelable");

        Entity dictionary = schema.addEntity("LibraryDictionary");
        dictionary.addIdProperty();
        dictionary.addStringProperty("name").notNull();
        dictionary.addBooleanProperty("use").notNull();
        dictionary.addBooleanProperty("readonly").notNull();
        dictionary.addBooleanProperty("personal").notNull();
        dictionary.addStringProperty("objectId").unique().notNull();
        dictionary.addIntProperty("size").notNull();
        dictionary.implementsInterface("Parcelable");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
