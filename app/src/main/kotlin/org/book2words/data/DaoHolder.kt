package org.book2words.data

import org.book2words.dao.DaoSession

public interface DaoHolder {
    fun setDaoSession(newSession: DaoSession)
    fun getDaoSession(): DaoSession

}
