package org.book2words.services.net

public trait B2WResponseSender<T> {

    fun send(response : T)
}