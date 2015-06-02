package org.book2words.services.net

import android.content.Intent

public interface RestCommand {

    fun execute(arguments: Intent)
}