package org.book2words.services.net

public interface B2WResponseSender<T> {

    fun send(response : T)
}