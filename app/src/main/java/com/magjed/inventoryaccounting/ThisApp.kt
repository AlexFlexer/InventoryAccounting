package com.magjed.inventoryaccounting

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ThisApp : Application() {
  override fun onCreate() {
    super.onCreate()
    startKoin {
      androidContext(this@ThisApp)
      modules(
        moduleDb, moduleGson, moduleViewModels
      )
    }
  }
}