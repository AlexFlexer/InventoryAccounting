package com.magjed.inventoryaccounting

import com.google.gson.GsonBuilder
import com.magjed.inventoryaccounting.database.HardwareItemsDatabase
import com.magjed.inventoryaccounting.database.LogsDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val moduleDb = module {
  single { androidContext().getOrCreateDatabase<HardwareItemsDatabase>() }
  single { androidContext().getOrCreateDatabase<LogsDatabase>() }
  single { get<HardwareItemsDatabase>().hardwareItemsDao() }
  single { get<LogsDatabase>().logsDao() }
}

val moduleGson = module {
  single { GsonBuilder()./* Your setup here */create() }
}

val moduleViewModels = module {

}