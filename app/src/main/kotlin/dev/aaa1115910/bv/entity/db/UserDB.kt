package dev.aaa1115910.bv.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")

data class UserDB(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "uid") val uid: Long,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "avatar") var avatar: String,
    @ColumnInfo(name = "auth") var auth: String,
    @ColumnInfo(name = "lock", defaultValue = "") var lock: String = "",
)