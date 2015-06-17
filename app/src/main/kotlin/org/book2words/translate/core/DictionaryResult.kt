package org.book2words.translate.core

public interface DictionaryResult {

    fun getResults() : Array<out Definition>
}