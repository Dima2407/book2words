package org.b2w.generator;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class B2WDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "org.book2words.dao");
        schema.enableKeepSectionsByDefault();
        Entity box = schema.addEntity("LibraryBook");
        box.addIdProperty();
        box.addStringProperty("name").notNull();
        box.addStringProperty("authors").notNull();
        box.addBooleanProperty("adapted").notNull();
        box.addBooleanProperty("read").notNull();
        box.addStringProperty("path").notNull().unique();
        box.implementsInterface("android.os.Parcelable");
        new DaoGenerator().generateAll(schema, args[0]);
    }
}
