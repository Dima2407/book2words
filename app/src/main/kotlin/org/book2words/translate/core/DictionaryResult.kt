package org.book2words.translate.core

interface DictionaryResult {

    fun getResults() : Array<out Definition>
}