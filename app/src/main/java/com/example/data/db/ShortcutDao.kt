package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ShortcutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShortcutDao {
    @Query("SELECT * FROM shortcuts ORDER BY createdAt DESC")
    fun getAllShortcutsFlow(): Flow<List<ShortcutEntity>>

    @Query("SELECT * FROM shortcuts ORDER BY createdAt DESC")
    suspend fun getAllShortcuts(): List<ShortcutEntity>

    @Query("SELECT * FROM shortcuts WHERE id = :id LIMIT 1")
    suspend fun getShortcutById(id: Long): ShortcutEntity?

    @Query("SELECT * FROM shortcuts WHERE shortcutId = :shortcutId LIMIT 1")
    suspend fun getShortcutByShortcutId(shortcutId: String): ShortcutEntity?

    @Query("SELECT * FROM shortcuts WHERE targetPackage = :packageName ORDER BY createdAt DESC")
    suspend fun getShortcutsForPackage(packageName: String): List<ShortcutEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortcut(shortcut: ShortcutEntity): Long

    @Update
    suspend fun updateShortcut(shortcut: ShortcutEntity)

    @Delete
    suspend fun deleteShortcut(shortcut: ShortcutEntity)

    @Query("DELETE FROM shortcuts WHERE id = :id")
    suspend fun deleteShortcutById(id: Long)

    @Query("DELETE FROM shortcuts")
    suspend fun deleteAllShortcuts()
}
