package dev.aaa1115910.bv.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.aaa1115910.bv.entity.db.UserDB

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<UserDB>

    @Query("SELECT * FROM user WHERE uid = :uid LIMIT 1")
    suspend fun findUserByUid(uid: Long): UserDB?

    @Insert
    suspend fun insert(vararg userDB: UserDB)

    @Delete
    suspend fun delete(vararg userDB: UserDB)

    @Update
    suspend fun update(userDB: UserDB)
}