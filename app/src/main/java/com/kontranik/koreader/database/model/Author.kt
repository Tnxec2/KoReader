package com.kontranik.koreader.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = AuthorHelper.TABLE,
    [Index(value = [AuthorHelper.COLUMN_FIRSTNAME, AuthorHelper.COLUMN_MIDDLENAME, AuthorHelper.COLUMN_LASTNAME], unique = true)]
)
class Author(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = AuthorHelper.COLUMN_ID)
    var id: Long? = null,

    @ColumnInfo(name = AuthorHelper.COLUMN_FIRSTNAME)
    var firstname: String? = null,

    @ColumnInfo(name = AuthorHelper.COLUMN_MIDDLENAME)
    var middlename: String? = null,

    @ColumnInfo(name = AuthorHelper.COLUMN_LASTNAME)
    var lastname: String? = null,

) : Serializable {

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + firstname.hashCode()
        result = 31 * result + middlename.hashCode()
        result = 31 * result + lastname.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Author

        if (id != other.id) return false
        if (firstname != other.firstname) return false
        if (middlename != other.middlename) return false
        if (lastname != other.lastname) return false

        return true
    }

    fun asString() : String {
        var result = ""
        if (lastname != null) result += lastname
        if (firstname != null) {
            if (result.isNotEmpty()) result += ", "
            result += firstname
        }
        if (middlename != null) {
            result += if (result.isNotEmpty()) " ($middlename)"
            else middlename
        }
        return result
    }

    fun compare(other: Author): Boolean {
        if (firstname != other.firstname) return false
        if (middlename != other.middlename) return false
        if (lastname != other.lastname) return false
        return true
    }

}

val mocupAuthors = mutableListOf(
    Author(0L, "Fannie", null,"Rothfuss"),
    Author(2L, "Darline", null,"Amis"),
    Author(3L, "Patti", null,"Barrowman"),
)