package com.magjed.inventoryaccounting.ui

import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import by.kirich1409.viewbindingdelegate.viewBinding
import com.magjed.inventoryaccounting.R
import com.magjed.inventoryaccounting.database.LogDao
import com.magjed.inventoryaccounting.database.LoggingEntity
import com.magjed.inventoryaccounting.database.ModificationType
import com.magjed.inventoryaccounting.databinding.ActivityLogBinding
import com.magjed.inventoryaccounting.databinding.ItemLogBinding
import com.magjed.inventoryaccounting.utils.autoDestroyLifecycleComponent
import com.magjed.inventoryaccounting.utils.layoutInflater
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

private class LogsDiffCallback : DiffUtil.ItemCallback<LoggingEntity>() {
  override fun areItemsTheSame(oldItem: LoggingEntity, newItem: LoggingEntity): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: LoggingEntity, newItem: LoggingEntity): Boolean {
    return oldItem == newItem
  }
}

private class LogsAdapter : ListAdapter<LoggingEntity, LogsAdapter.LogHolder>(LogsDiffCallback()) {
  class LogHolder(val mBinding: ItemLogBinding) : ViewHolder(mBinding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogHolder {
    return LogHolder(ItemLogBinding.inflate(parent.layoutInflater, parent, false))
  }

  override fun onBindViewHolder(holder: LogHolder, position: Int) {
    val item = getItem(position)
    holder.mBinding.txtTime.text = item.time
    holder.mBinding.txtEventName.setText(getEventName(item.type))
    holder.mBinding.txtBefore.apply {
      text = item.oldProduct.toString()
      isVisible = item.oldProduct != null
    }
    holder.mBinding.txtAfter.apply {
      text = item.newProduct.toString()
      isVisible = item.newProduct != null
    }
  }

  @StringRes
  private fun getEventName(modType: ModificationType): Int {
    return when (modType) {
      ModificationType.ADDED -> R.string.event_added
      ModificationType.REMOVED -> R.string.event_removed
      ModificationType.CHANGED -> R.string.event_changed
    }
  }
}

class LogsActivity : AppCompatActivity(R.layout.activity_log) {
  private val mBinding: ActivityLogBinding by viewBinding()
  private val mAdapter by autoDestroyLifecycleComponent({ LogsAdapter() })
  private val mLogsDao: LogDao by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mBinding.recyclerLogs.adapter = mAdapter
    mLogsDao.getAllLogs().onEach { mAdapter.submitList(it) }
      .launchIn(lifecycleScope)
  }
}