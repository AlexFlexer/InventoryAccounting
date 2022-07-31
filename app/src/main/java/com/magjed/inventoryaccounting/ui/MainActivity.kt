package com.magjed.inventoryaccounting.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.magjed.inventoryaccounting.R
import com.magjed.inventoryaccounting.databinding.ActivityMainBinding

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