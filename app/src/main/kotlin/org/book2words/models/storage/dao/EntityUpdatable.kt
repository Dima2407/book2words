package org.book2words.models.storage.dao

public interface EntityUpdatable<T> {
    fun update(item : T)
}