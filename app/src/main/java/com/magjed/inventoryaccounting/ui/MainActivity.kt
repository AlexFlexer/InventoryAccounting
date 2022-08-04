package com.magjed.inventoryaccounting.ui

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.gson.Gson
import com.magjed.inventoryaccounting.R
import com.magjed.inventoryaccounting.database.*
import com.magjed.inventoryaccounting.databinding.ActivityMainBinding
import com.magjed.inventoryaccounting.databinding.ItemProductBinding
import com.magjed.inventoryaccounting.utils.autoDestroyLifecycleComponent
import com.magjed.inventoryaccounting.utils.createBundleAndPut
import com.magjed.inventoryaccounting.utils.layoutInflater
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime

class MainItemsDiffCallback : ItemCallback<ProductEntity>() {
  override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
    return oldItem == newItem
  }
}

class MainItemsAdapter(
  private val mOnQrClicked: (item: ProductEntity) -> Unit,
  private val mOnRemoveClicked: (item: ProductEntity) -> Unit
) : ListAdapter<ProductEntity, MainItemsAdapter.MainItemHolder>(MainItemsDiffCallback()) {
  class MainItemHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainItemHolder {
    return MainItemHolder(
      ItemProductBinding.inflate(parent.layoutInflater, parent, false)
    )
  }

  override fun onBindViewHolder(holder: MainItemHolder, position: Int) {
    val item = getItem(position)
    holder.binding.btnQr.setOnClickListener { mOnQrClicked(item) }
    holder.binding.btnRemove.setOnClickListener { mOnRemoveClicked(item) }
    holder.binding.txtNameModel.text = item.type + item.model
    holder.binding.txtManufacturer.text = item.manufacturer
    holder.binding.txtAmount.text = item.amount.toString()
  }
}

class MainViewModel(
  private val mProductsDao: ProductsDao,
  private val mGson: Gson,
  private val mLogDao: LogDao
) : ViewModel() {

  private val _mItems = MutableLiveData<List<ProductEntity>>()
  val mItems: LiveData<List<ProductEntity>> = _mItems
  private val _mFiltered = MutableLiveData(false)
  val mFiltered: LiveData<Boolean> = _mFiltered
  private val _mScannedItem = MutableLiveData<ProductEntity?>()
  val mScannedItem: LiveData<ProductEntity?> = _mScannedItem

  fun getItemFromString(itemStr: String) {
    var item = try {
      mGson.fromJson(itemStr, ProductEntity::class.java)
    } catch (e: Exception) {
      null
    }
    if (item == null)
      item = _mItems.value?.firstOrNull { it.id == itemStr }
    _mScannedItem.value = item
  }

  fun loadItems() {
    viewModelScope.launch {
      _mItems.value = mProductsDao.getProducts()
    }
  }

  fun removeItem(id: String) {
    viewModelScope.launch {
      val product = _mItems.value?.firstOrNull { it.id == id }
      mProductsDao.removeProduct(id)
      mLogDao.addLog(
        LoggingEntity(
          LocalDateTime.now().toString(),
          ModificationType.REMOVED,
          product,
          null
        )
      )
      loadItems()
    }
  }

  fun addItem(item: ProductEntity) {
    viewModelScope.launch {
      mProductsDao.addProduct(item)
      mLogDao.addLog(
        LoggingEntity(
          LocalDateTime.now().toString(),
          ModificationType.ADDED,
          null,
          item
        )
      )
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
      _mItems.value = mProductsDao.filter(id, type, model, manufacturer, location, amount)
    }
  }

  fun closeFilter() {
    _mFiltered.value = false
    loadItems()
  }

  fun forgetScannedItem() {
    _mScannedItem.value = null
  }
}

class MainActivity : AppCompatActivity(R.layout.activity_main) {

  companion object {
    private const val EXTRA_SCANNED_TEXT = "scanned_txt"
    private val TAG = MainActivity::class.java.name

    @JvmStatic
    fun packResult(result: String): Intent {
      return Intent().apply { putExtra(EXTRA_SCANNED_TEXT, result) }
    }

    private fun unpackResult(intent: Intent?): String? {
      return intent?.getStringExtra(EXTRA_SCANNED_TEXT)
    }
  }

  private val mBinding: ActivityMainBinding by viewBinding()
  private val mViewModel: MainViewModel by viewModel()
  private lateinit var mScannerResultHandle: ActivityResultLauncher<Intent>
  private val mAdapter by autoDestroyLifecycleComponent({
    MainItemsAdapter(
      mOnQrClicked = { startScanner() },
      mOnRemoveClicked = { mViewModel.removeItem(it.id) }
    )
  })

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mBinding.recyclerMain.adapter = mAdapter
    mScannerResultHandle = registerForActivityResult(
      ActivityResultContracts.StartActivityForResult()
    ) { deserializeItem(unpackResult(it?.data)) }
    mBinding.btnFilter.setOnClickListener {
      if (mViewModel.mFiltered.value == true) mViewModel.closeFilter()
      else startFilterDialog()
    }
    mBinding.btnLogs.setOnClickListener {
      startActivity(Intent(this, LogsActivity::class.java))
    }
    mBinding.btnCamera.setOnClickListener {
      startScanner()
    }
    mBinding.btnPlus.setOnClickListener {
      startItemInfoDialog(null)
    }
    mViewModel.mFiltered.observe(this) {
      @DrawableRes val imgRes = if (it) R.drawable.ico_close else R.drawable.ico_filter
      mBinding.btnFilter.setImageResource(imgRes)
    }
    mViewModel.mScannedItem.observe(this) { item ->
      startItemInfoDialog(item)
    }
    mViewModel.mItems.observe(this) {
      mAdapter.submitList(it.orEmpty())
    }
  }

  private fun startFilterDialog() {
    FilterDialog().show(supportFragmentManager, TAG)
  }

  private fun startItemInfoDialog(item: ProductEntity?) {
    ItemInfoDialog().apply {
      if (item != null) arguments = createBundleAndPut(ItemInfoDialog::mItem to item)
    }.show(supportFragmentManager, TAG)
  }

  private fun startScanner() {
    mScannerResultHandle.launch(Intent(this, ScannerActivity::class.java))
  }

  private fun deserializeItem(scannedText: String?) {
    if (scannedText == null) return
    mViewModel.getItemFromString(scannedText)
  }
}