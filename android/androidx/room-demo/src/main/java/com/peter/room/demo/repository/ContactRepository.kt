package com.peter.room.demo.repository

import com.peter.room.demo.db.dao.ContactDao
import com.peter.room.demo.db.entity.Contact
import kotlinx.coroutines.flow.Flow

class ContactRepository(private val contactDao: ContactDao) {
    val allContacts: Flow<List<Contact>> = contactDao.observeAll()
    suspend fun insert(contact: Contact): Long = contactDao.insert(contact)
    suspend fun deleteAll() = contactDao.deleteAll()
    suspend fun findByHomeCity(city: String) = contactDao.findByHomeCity(city)
    suspend fun findByWorkCity(city: String) = contactDao.findByWorkCity(city)
}
