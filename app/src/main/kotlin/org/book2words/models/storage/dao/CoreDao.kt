package org.book2words.models.storage.dao

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.book2words.core.Logger
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.ArrayList
import java.util.HashSet

public abstract class CoreDao<T> where T : EntityUpdatable<T> {

    private val serializer = Gson()

    private val items = HashSet<T>()

    protected abstract fun getPath() : File

    public fun list(): List<T> {
        return ArrayList(items)
    }

    public fun add(item: T): Boolean {
        return items.add(item)
    }

    public fun update(item: T): Boolean {
        val first = items.firstOrNull { it.equals(item) }
        if (first != null) {
            first.update(item)
            return true
        }
        return false
    }

    public fun remove(item: T): Boolean {
        return items.remove(item);
    }

    public fun commit() {
        try {
            val writer = FileWriter(getPath())
            serializer.toJson(items, writer)
        } catch(e: Exception) {
            Logger.error(e)
        }
    }


    public fun load() {
        items.clear()
        try {
            val reader = FileReader(getPath())
            val collectionType = object : TypeToken<List<T>>() {}.getType()
            val albums = serializer.fromJson<List<T>>(reader, collectionType)
            items.addAll(albums)
        } catch(e: Exception) {
            Logger.error(e)
        }
    }
}