package com.magjed.inventoryaccounting.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.magjed.inventoryaccounting.databinding.DialogFilterBinding
import com.magjed.inventoryaccounting.utils.getContent
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FilterDialog : BottomSheetDialogFragment() {
  private val mBinding: DialogFilterBinding by viewBinding()
  private val mViewModel: MainViewModel by sharedViewModel()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return mBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mBinding.btnApplyFilter.setOnClickListener {
      mViewModel.filter(
        getTextOf(mBinding.inputId),
        getTextOf(mBinding.inputType),
        getTextOf(mBinding.inputModel),
        getTextOf(mBinding.inputManufacturer),
        getTextOf(mBinding.inputLocation),
        getTextOf(mBinding.inputAmount)?.toIntOrNull() ?: Int.MIN_VALUE
      )
      dismiss()
    }
  }

  private fun getTextOf(editText: EditText): String? {
    val result = editText.getContent()
    return result.ifBlank { null }
  }
}