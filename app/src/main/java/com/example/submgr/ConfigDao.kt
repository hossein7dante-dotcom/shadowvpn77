package com.example.submgr

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    @Query("SELECT * FROM configs ORDER BY uid DESC")
    fun getAll(): Flow<List<ConfigItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ConfigItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ConfigItem)

    @Update
    suspend fun update(item: ConfigItem)

    @Query("DELETE FROM configs")
    suspend fun clear()
}
