package org.book2words.models.storage.dao

public trait EntityUpdatable<T> {
    fun update(item : T)
}