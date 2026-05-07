package com.peter.room.demo.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.peter.room.demo.db.entity.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact): Long

    @Query("SELECT * FROM contacts ORDER BY id DESC")
    fun observeAll(): Flow<List<Contact>>

    @Query("SELECT * FROM contacts ORDER BY id DESC")
    suspend fun getAll(): List<Contact>

    @Query("DELETE FROM contacts")
    suspend fun deleteAll()

    @Query("SELECT * FROM contacts WHERE city = :city")
    suspend fun findByHomeCity(city: String): List<Contact>

    @Query("SELECT * FROM contacts WHERE work_city = :city")
    suspend fun findByWorkCity(city: String): List<Contact>
}
