package org.data

import android.app.Application
import org.book2words.dao.DaoSession

public trait DaoHolder {
    fun setDaoSession(newSession: DaoSession)
    fun getDaoSession(): DaoSession

}
