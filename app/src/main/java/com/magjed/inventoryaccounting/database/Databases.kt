/*
 * This class contains Room database definitions.
 */
package com.magjed.inventoryaccounting.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProductEntity::class], version = 1)
abstract class HardwareItemsDatabase : RoomDatabase() {
  abstract fun hardwareItemsDao(): ProductsDao
}

@Database(entities = [LoggingEntity::class], version = 1)
abstract class LogsDatabase : RoomDatabase() {
  abstract fun logsDao(): LogDao
}