package com.magjed.inventoryaccounting

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Function for creating [RoomDatabase] of type [D] with help of [this] [Context].
 *
 * @param D is type of the database returned.
 * @param dbName is the database name. By default, it is [Class.getName].
 * @param customBuilderSetup is a function to be invoked for custom parameters application
 * on the given [RoomDatabase.Builder] object.
 */
inline fun <reified D: RoomDatabase> Context.getOrCreateDatabase(
  dbName: String = D::class.java.name,
  customBuilderSetup: RoomDatabase.Builder<D>.() -> Unit = {}
): D {
  return Room.databaseBuilder(this, D::class.java, dbName)
    .apply(customBuilderSetup)
    .build()
}