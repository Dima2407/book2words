package org.book2words.services.net

import android.content.Intent

public trait RestCommand {

    fun execute(arguments: Intent)
}