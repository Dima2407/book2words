package org.book2words.models

public class LibraryUser(val firstName: String,
                         val lastName: String,
                         var level: LibraryLevel) {

    constructor(user: LibraryUser) : this(user.firstName, user.lastName, user.level) {

    }

}