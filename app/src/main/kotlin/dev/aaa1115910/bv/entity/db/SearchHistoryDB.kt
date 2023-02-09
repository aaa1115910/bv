package dev.aaa1115910.bv.entity.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "search_history")
data class SearchHistoryDB(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "keyword") val keyword: String,
    @ColumnInfo(name = "search_date") var searchDate: Date = Date(),
)
