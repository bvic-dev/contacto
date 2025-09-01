package com.bvic.lydiacontacts.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bvic.lydiacontacts.data.local.entity.RandomUserEntity

@Dao
interface RandomUserDao {
    @Query("SELECT * FROM randomUsers WHERE page = :page")
    suspend fun getPage(page: Int): List<RandomUserEntity>

    @Query(
        """
        SELECT * FROM randomUsers
        WHERE (:query = '' 
               OR first_name LIKE :query 
               OR last_name LIKE :query 
               OR email LIKE :query 
               OR phone LIKE :query)
        ORDER BY last_name ASC, first_name ASC
    """,
    )
    suspend fun search(query: String): List<RandomUserEntity>

    @Query("SELECT * FROM randomUsers WHERE id = :id")
    suspend fun getRandomUserById(id: String): RandomUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(randomUsersEntity: List<RandomUserEntity>)

    @Query("DELETE FROM randomUsers")
    suspend fun deleteAllRandomUsers()
}
