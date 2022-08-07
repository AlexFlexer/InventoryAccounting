package com.magjed.inventoryaccounting.utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.DialogFragment

/**
 * Makes the given [Dialog]'s background transparent,
 * basically used to correctly show dialogs with rounded corners.
 */
fun Dialog.makeBackgroundTransparent() =
  this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

/**
 * Makes the given [DialogFragment]'s background transparent.
 */
fun DialogFragment.makeBackgroundTransparent() =
  dialog?.makeBackgroundTransparent()