package com.magjed.inventoryaccounting.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.magjed.inventoryaccounting.database.ProductEntity
import com.magjed.inventoryaccounting.databinding.DialogQrGeneratorBinding
import com.magjed.inventoryaccounting.utils.argument
import org.koin.android.ext.android.inject

class ItemQRGeneratorDialog: BottomSheetDialogFragment() {

  private val mBinding: DialogQrGeneratorBinding by viewBinding()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return mBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

  }
}