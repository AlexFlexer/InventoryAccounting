package com.magjed.inventoryaccounting.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.gson.Gson
import com.magjed.inventoryaccounting.R
import com.magjed.inventoryaccounting.database.ProductEntity
import com.magjed.inventoryaccounting.database.ProductsDao
import com.magjed.inventoryaccounting.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
  private val mProductsDao: ProductsDao,
  private val mGson: Gson
) : ViewModel() {

  private val _mItems = MutableLiveData<List<ProductEntity>>()
  private val mItems: LiveData<List<ProductEntity>> = _mItems
  private val _mFiltered = MutableLiveData(false)
  private val mFiltered: LiveData<Boolean> = _mFiltered

  fun loadItems() {
    viewModelScope.launch {
      mProductsDao.getProducts().onEach { _mItems.value = it }
        .launchIn(this)
    }
  }

  fun removeItem(id: String) {
    viewModelScope.launch {
      mProductsDao.removeProduct(id)
      loadItems()
    }
  }

  fun addItem(item: ProductEntity) {
    viewModelScope.launch {
      mProductsDao.addProduct(item)
      loadItems()
    }
  }

  fun addItem(json: String) {
    try {
      addItem(mGson.fromJson(json, ProductEntity::class.java))
    } catch (e: Exception) {
      loadItems()
    }
  }

  fun filter(
    id: String?,
    type: String?,
    model: String?,
    manufacturer: String?,
    location: String?,
    amount: Int?
  ) {
    _mFiltered.value = true
    viewModelScope.launch {
      mProductsDao.filter(id, type, model, manufacturer, location, amount)
    }
  }

  fun closeFilter() {
    _mFiltered.value = false
    loadItems()
  }
}

class MainActivity : AppCompatActivity(R.layout.activity_main) {

  companion object {
    private const val EXTRA_SCANNED_TEXT = "scanned_txt"

    @JvmStatic
    fun packResult(result: String): Intent {
      return Intent().apply { putExtra(EXTRA_SCANNED_TEXT, result) }
    }

    private fun unpackResult(intent: Intent?): String? {
      return intent?.getStringExtra(EXTRA_SCANNED_TEXT)
    }
  }

  private val mBinding: ActivityMainBinding by viewBinding()
}