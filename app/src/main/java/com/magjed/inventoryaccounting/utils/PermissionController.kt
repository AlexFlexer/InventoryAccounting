package com.magjed.inventoryaccounting.utils

import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * This class controls the necessary permission [mPermissionName] to be given before executing
 * [mOnPermissionGranted] function.
 * Important: this class should be instantiated in either [Fragment.onAttach] or
 * [Fragment.onCreate] method since it uses [ActivityResultLauncher] under the hood.
 *
 * @param mFragment is the fragment to lifecycle of which this class will be tied.
 * @param mPermissionName is the permission you want to request.
 * @param mOnPermissionGranted is the action you want to perform (invoke) after the
 * permission is granted.
 * @param mOnPermissionDenied is the action you want to be performed when the permission
 * is denied.
 */
class PermissionsController(
  private val mFragment: Fragment,
  private val mPermissionName: String,
  private val mOnPermissionGranted: () -> Unit,
  private val mOnPermissionDenied: () -> Unit = {}
) : DefaultLifecycleObserver, ActivityResultCallback<Boolean> {

  private lateinit var mPermissionRequestLauncher: ActivityResultLauncher<String>

  init {
    mFragment.lifecycle.addObserver(this)
  }

  override fun onCreate(owner: LifecycleOwner) {
    mPermissionRequestLauncher =
      mFragment.registerForActivityResult(ActivityResultContracts.RequestPermission(), this)
  }

  /**
   * Call this method to request the [mPermissionName] permission. After the permission is
   * granted, [mOnPermissionGranted] function is executed.
   */
  fun requestPermission() {
    mPermissionRequestLauncher.launch(mPermissionName)
  }

  /**
   * Checks if the given permission is already granted, and calls [mOnPermissionGranted]
   * callback if so.
   *
   * @return true if the permission is granted and the callback is called, false otherwise.
   */
  fun checkPermissionOrIgnore(): Boolean {
    val permGranted =
      mFragment.context?.checkSelfPermission(mPermissionName) == PackageManager.PERMISSION_GRANTED
    if (permGranted) mOnPermissionGranted()
    return permGranted
  }

  override fun onActivityResult(result: Boolean?) {
    if (result == true) mOnPermissionGranted()
    else mOnPermissionDenied()
  }
}