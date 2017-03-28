package org.book2words.services

import android.os.IBinder

public interface BookBinder : IBinder {
    fun release()
}