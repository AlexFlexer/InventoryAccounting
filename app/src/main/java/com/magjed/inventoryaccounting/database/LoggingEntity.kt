package com.magjed.inventoryaccounting.database

import androidx.annotation.Keep
import androidx.room.*
import com.magjed.inventoryaccounting.TABLE_LOGS
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
@Keep
@Entity(tableName = TABLE_LOGS)
data class LoggingEntity(
  val time: String,
  val type: ModificationType,
  @Embedded(prefix = "t1") val oldProduct: ProductEntity?,
  @Embedded(prefix = "t2") val newProduct: ProductEntity?
) {
  @PrimaryKey(autoGenerate = true)
  var id: Int = 0
}

/**
 * DAO for interacting with logs table.
 *
 * @author Alexander Dyachenko
 */
@Dao
interface LogDao {

  @Query("select * from $TABLE_LOGS order by time desc")
  fun getAllLogs(): Flow<List<LoggingEntity>>

  @Query("delete from $TABLE_LOGS")
  suspend fun removeLogs()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addLog(log: LoggingEntity)
}