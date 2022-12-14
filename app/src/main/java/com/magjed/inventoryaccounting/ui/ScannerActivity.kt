package com.magjed.inventoryaccounting.ui

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.budiyev.android.codescanner.*
import com.magjed.inventoryaccounting.R
import com.magjed.inventoryaccounting.databinding.ActivityScannerBinding
import com.magjed.inventoryaccounting.utils.ActivityPermissionsController
import com.magjed.inventoryaccounting.utils.autoDestroyLifecycleComponent
import com.magjed.inventoryaccounting.utils.openAppSettings
import com.magjed.inventoryaccounting.utils.toast

/**
 * This activity is responsible for scanning the QR-codes of the products.
 */
class ScannerActivity : AppCompatActivity(R.layout.activity_scanner) {
  private val mBinding: ActivityScannerBinding by viewBinding()
  private val mCodeScanner by autoDestroyLifecycleComponent({
    CodeScanner(this, mBinding.codeScanner)
  })
  private val mCameraPermissionController by autoDestroyLifecycleComponent({
    ActivityPermissionsController(this,
      mPermissionName = Manifest.permission.CAMERA,
      mOnPermissionGranted = { mCodeScanner.startPreview() },
      mOnPermissionDenied = {
        toast(R.string.permission_denied_camera)
        openAppSettings()
        finish()
      })
  })

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // instantiating the controller
    mCameraPermissionController
    mCodeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
    mCodeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
    // ex. listOf(BarcodeFormat.QR_CODE)
    mCodeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
    mCodeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
    mCodeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
    mCodeScanner.isFlashEnabled = false // Whether to enable flash or not
    mCodeScanner.decodeCallback = DecodeCallback { decodedText ->
      runOnUiThread {
        setResult(RESULT_OK, MainActivity.packResult(decodedText.text))
        finish()
      }
    }
    mCodeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
      runOnUiThread {
        Toast.makeText(
          this, "Camera initialization error: ${it.message}",
          Toast.LENGTH_LONG
        ).show()
        finish()
      }
    }
  }

  override fun onResume() {
    super.onResume()
    mCameraPermissionController.requestPermission()
  }

  override fun onPause() {
    super.onPause()
    mCodeScanner.releaseResources()
  }
}