package com.magjed.inventoryaccounting

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.magjed.inventoryaccounting.database.HardwareItemsDatabase
import com.magjed.inventoryaccounting.database.LogDao
import com.magjed.inventoryaccounting.database.LogsDatabase
import com.magjed.inventoryaccounting.database.ProductsDao
import com.magjed.inventoryaccounting.ui.ItemQrGenViewModel
import com.magjed.inventoryaccounting.ui.MainViewModel
import com.magjed.inventoryaccounting.utils.getOrCreateDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get

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
  ItemQrGenViewModel(get(Gson::class.java))
  MainViewModel(get(ProductsDao::class.java), get(Gson::class.java), get(LogDao::class.java))
}