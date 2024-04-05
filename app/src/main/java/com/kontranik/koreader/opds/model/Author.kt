package com.kontranik.koreader.opds.model

import java.io.Serializable

class Author(val name: String?, val uri: String?, val email: String?) : Serializable {

    override fun toString(): String {

        var result = name
        if (uri != null) {
            if (result != null) result.plus(" ($uri)")
            else result = uri
        }
        if (email != null) {
            if (result != null) result.plus(", $email")
            else result = email
        }
        return result ?: "no author"
    }
}