package com.magjed.inventoryaccounting.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.magjed.inventoryaccounting.database.ProductEntity
import com.magjed.inventoryaccounting.databinding.DialogItemInfoBinding
import com.magjed.inventoryaccounting.utils.argumentNullable
import com.magjed.inventoryaccounting.utils.getContent
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ItemInfoDialog : BottomSheetDialogFragment() {

  val mItem: ProductEntity? by argumentNullable()
  private val mBinding: DialogItemInfoBinding by viewBinding()
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
    mBinding.inputId.setText(mItem?.id)
    mBinding.inputManufacturer.setText(mItem?.manufacturer)
    mBinding.inputModel.setText(mItem?.model)
    mBinding.inputType.setText(mItem?.type)
    mBinding.inputAmount.setText(mItem?.amount?.toString())
    mBinding.inputLocation.setText(mItem?.location)
    mBinding.btnSave.setOnClickListener { mViewModel.addItem(collectItemInfo()); dismiss() }
    mBinding.btnDelete.setOnClickListener { mViewModel.removeItem(mItem?.id ?: "0"); dismiss() }
  }

  private fun collectItemInfo(): ProductEntity {
    return ProductEntity(
      mBinding.inputId.getContent(),
      mBinding.inputType.getContent(),
      mBinding.inputModel.getContent(),
      mBinding.inputManufacturer.getContent(),
      mBinding.inputLocation.getContent(),
      mBinding.inputAmount.getContent().toIntOrNull() ?: 0
    )
  }
}