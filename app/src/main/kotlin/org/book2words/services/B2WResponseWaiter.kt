package org.book2words.services

public trait B2WResponseWaiter<T> {

    public fun onResult(success: Boolean, data: T)
}
