package com.magjed.inventoryaccounting.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.magjed.inventoryaccounting.database.ProductEntity
import com.magjed.inventoryaccounting.databinding.DialogQrGeneratorBinding
import com.magjed.inventoryaccounting.utils.argument
import com.magjed.inventoryaccounting.utils.toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.resume

data class BitmapResult(val mBitmap: Bitmap?, val errMsg: String?)

class ItemQrGenViewModel(private val mGson: Gson) : ViewModel() {
  private val _readyQr = MutableLiveData(BitmapResult(null, null))
  val readyQr: LiveData<BitmapResult> = _readyQr

  fun generateQrCode(product: ProductEntity) {
    _readyQr.value = BitmapResult(null, null)
    viewModelScope.launch {
      suspendCancellableCoroutine { continuation ->
        try {
          val prodJson = mGson.toJson(product)
          val encoder = QRGEncoder(prodJson, QRGContents.Type.TEXT)
          _readyQr.value = BitmapResult(encoder.bitmap, null)
          continuation.resume(Unit)
        } catch (e: Exception) {
          _readyQr.value = BitmapResult(null, e.message)
          continuation.resume(Unit)
        }
      }
    }
  }
}

class ItemQRGeneratorDialog : BottomSheetDialogFragment() {

  private val mBinding: DialogQrGeneratorBinding by viewBinding(CreateMethod.INFLATE)
  private val mItem: ProductEntity by argument()
  private val mViewModel: ItemQrGenViewModel by viewModel()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return mBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mViewModel.readyQr.observe(viewLifecycleOwner) {
      mBinding.progressQrGen.isVisible = it?.errMsg == null && it?.mBitmap == null
      mBinding.imgQr.setImageBitmap(it?.mBitmap)
      if (it?.errMsg != null) {
        toast(it.errMsg)
        dismiss()
      }
    }
    mViewModel.generateQrCode(mItem)
  }
}