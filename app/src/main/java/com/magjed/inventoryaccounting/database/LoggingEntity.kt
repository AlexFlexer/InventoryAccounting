package com.magjed.inventoryaccounting.database

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * This is which changes are allowed on database items.
 */
enum class ModificationType {
  /**
   * Added new item.
   */
  ADDED,

  /**
   * Removed item.
   */
  REMOVED,

  /**
   * Changed any of the attributes of the item.
   */
  CHANGED
}

/**
 * Log entity, represents a logged change in the database.
 *
 * @param time is a string-encoded time when the event has happened.
 * @param type defines what has changed in database.
 * @param oldProduct is old version of item (not null if [type] is [ModificationType.CHANGED] or
 * [ModificationType.REMOVED], null otherwise).
 * @param newProduct is the currently presented in the database item (null only if [type] is
 * [ModificationType.REMOVED]).
 *
 * @author Alexander Dyachenko
 */
data class LoggingEntity(
  @PrimaryKey(autoGenerate = true) val id: Int,
  val time: String,
  val type: ModificationType,
  @Embedded val oldProduct: ProductEntity?,
  @Embedded val newProduct: ProductEntity?
)

/**
 * DAO for interacting with logs table.
 *
 * @author Alexander Dyachenko
 */
@Dao
interface LogDao {

  @Query("select * from $TABLE_LOGS")
  fun getAllLogs(): Flow<LoggingEntity>

  @Query("delete from $TABLE_LOGS")
  fun removeLogs()
}