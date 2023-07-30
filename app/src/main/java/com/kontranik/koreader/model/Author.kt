package com.kontranik.koreader.model

class Author(val firstname: String?, val middlename: String?, val lastname: String?) {
    fun compare(other: com.kontranik.koreader.database.model.Author): Boolean {
        if (firstname != other.firstname) return false
        if (middlename != other.middlename) return false
        if (lastname != other.lastname) return false
        return true
    }
}