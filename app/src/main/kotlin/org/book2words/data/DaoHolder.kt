package org.book2words.data

import org.book2words.database.DaoSession

interface DaoHolder {
    fun setDaoSession(newSession: DaoSession)
    fun getDaoSession(): DaoSession

}
