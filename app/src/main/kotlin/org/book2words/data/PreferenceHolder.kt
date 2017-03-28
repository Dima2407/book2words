package org.book2words.data

public interface PreferenceHolder {

    fun getConfigs(): Configs

    fun getDictionary(): CacheDictionary
}