@file:Suppress("unused")

package com.magjed.inventoryaccounting.utils

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.annotation.CheckResult
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.magjed.inventoryaccounting.R

@CheckResult
fun createNetworkingMessage(
  context: Context,
  message: String?,
): AlertDialog {
  val resolvedTitle = context.resources.getString(R.string.alert_error)
  val resolvedBtnText = context.resources.getString(R.string.alert_ok)
  return createAlertDialogWithMessage(
    context, resolvedTitle, message.orEmpty(), resolvedBtnText
  )
}

@CheckResult
fun createNetworkingMessage(
  context: Context,
  @StringRes message: Int,
): AlertDialog = createAlertDialogWithMessage(
  context, R.string.alert_error, message, R.string.alert_ok
)

@CheckResult
fun createAlertDialogWithButtonsAndMessage(
  context: Context,
  title: String,
  message: String,
  posBtnTitle: String,
  negBtnTitle: String,
  onPositiveButtonClicked: (dialog: DialogInterface) -> Unit,
  onNegativeButtonClicked: (dialog: DialogInterface) -> Unit,
  isCancellable: Boolean = true,
): AlertDialog {
  val builder = AlertDialog.Builder(context)
    .setTitle(title)
    .setMessage(message)
  return completeAlertDialogInternal(
    builder,
    negBtnTitle,
    posBtnTitle,
    onPositiveButtonClicked,
    onNegativeButtonClicked,
    isCancellable
  )
}

@CheckResult
fun createAlertDialogWithButtonsAndMessage(
  context: Context,
  @StringRes title: Int,
  @StringRes message: Int,
  @StringRes posBtnTitle: Int,
  @StringRes negBtnTitle: Int,
  onPositiveButtonClicked: (dialog: DialogInterface) -> Unit,
  onNegativeButtonClicked: (dialog: DialogInterface) -> Unit,
  isCancellable: Boolean = true,
): AlertDialog {
  val builder = AlertDialog.Builder(context)
    .setTitle(title)
    .setMessage(message)
  val resolvedPosTitle = context.getString(posBtnTitle)
  val resolvedNegTitle = context.getString(negBtnTitle)
  return completeAlertDialogInternal(
    builder,
    resolvedNegTitle,
    resolvedPosTitle,
    onPositiveButtonClicked,
    onNegativeButtonClicked,
    isCancellable
  )
}

@CheckResult
fun createAlertDialogWithMessage(
  context: Context,
  title: String,
  message: String,
  buttonText: String,
  isCancellable: Boolean = true,
): AlertDialog {
  return AlertDialog.Builder(context)
    .setCancelable(isCancellable)
    .setTitle(title)
    .setMessage(message)
    .setPositiveButton(buttonText) { dialog, _ ->
      dialog.dismiss()
    }
    .create()
}

@CheckResult
fun createAlertDialogWithMessage(
  context: Context,
  @StringRes title: Int,
  @StringRes message: Int,
  @StringRes buttonText: Int,
  isCancellable: Boolean = true,
): AlertDialog {
  val resolvedMessage = context.resources.getString(message)
  val resolvedTitle = context.resources.getString(title)
  val resolvedBtnText = context.resources.getString(buttonText)
  return createAlertDialogWithMessage(
    context, resolvedTitle, resolvedMessage, resolvedBtnText, isCancellable
  )
}

@CheckResult
fun createAlertDialogWithCustomLayout(
  context: Context,
  view: View,
  negBtnTitle: String,
  posBtnTitle: String,
  onPositiveButtonClicked: (dialog: DialogInterface) -> Unit,
  onNegativeButtonClicked: (dialog: DialogInterface) -> Unit,
  isCancellable: Boolean = true,
): AlertDialog {
  val builder = AlertDialog.Builder(context)
    .setView(view)
  return completeAlertDialogInternal(
    builder,
    negBtnTitle,
    posBtnTitle,
    onPositiveButtonClicked,
    onNegativeButtonClicked,
    isCancellable
  )
}

@CheckResult
fun createAlertDialogWithCustomLayout(
  context: Context,
  @LayoutRes layout: Int,
  @StringRes negBtnTitle: Int,
  @StringRes posBtnTitle: Int,
  onPositiveButtonClicked: (dialog: DialogInterface) -> Unit,
  onNegativeButtonClicked: (dialog: DialogInterface) -> Unit,
  isCancellable: Boolean = true,
): AlertDialog {
  val builder = AlertDialog.Builder(context)
    .setView(layout)
  val resolvedNegTitle = context.resources.getString(negBtnTitle)
  val resolvedPosTitle = context.resources.getString(posBtnTitle)
  return completeAlertDialogInternal(
    builder,
    resolvedNegTitle,
    resolvedPosTitle,
    onPositiveButtonClicked,
    onNegativeButtonClicked,
    isCancellable
  )
}

@CheckResult
fun createAlertDialogWithCustomLayout(
  context: Context,
  view: View,
  @StringRes negBtnTitle: Int,
  @StringRes posBtnTitle: Int,
  onPositiveButtonClicked: (dialog: DialogInterface) -> Unit,
  onNegativeButtonClicked: (dialog: DialogInterface) -> Unit,
  isCancellable: Boolean = true,
): AlertDialog {
  val builder = AlertDialog.Builder(context)
    .setView(view)
  val resolvedNegTitle = context.resources.getString(negBtnTitle)
  val resolvedPosTitle = context.resources.getString(posBtnTitle)
  return completeAlertDialogInternal(
    builder, resolvedNegTitle, resolvedPosTitle,
    onPositiveButtonClicked, onNegativeButtonClicked, isCancellable
  )
}

@CheckResult
private fun completeAlertDialogInternal(
  builder: AlertDialog.Builder,
  negBtnTitle: String,
  posBtnTitle: String,
  onPositiveButtonClicked: (dialog: DialogInterface) -> Unit,
  onNegativeButtonClicked: (dialog: DialogInterface) -> Unit,
  isCancellable: Boolean,
): AlertDialog {
  return builder
    .setCancelable(isCancellable)
    .setNegativeButton(negBtnTitle) { dialog, which ->
      if (which == AlertDialog.BUTTON_NEGATIVE)
        onNegativeButtonClicked(dialog)
    }.setPositiveButton(posBtnTitle) { dialog, which ->
      if (which == AlertDialog.BUTTON_POSITIVE)
        onPositiveButtonClicked(dialog)
    }.create()
}


/**
 * Creates an [AlertDialog], assigns a custom view to it represented by [viewBinding]'s root,
 * makes [AlertDialog]'s window background transparent so you can show dialogs with rounded
 * corners, and shows this dialog to the user.
 */
fun <VB : ViewBinding> Context.createAndShowDialogWithCustomView(
  viewBinding: VB,
  bindingSetup: (b: VB, dialog: AlertDialog) -> Unit,
  cancelable: Boolean = true,
) {
  val dialog = AlertDialog.Builder(this)
    .setView(viewBinding.root)
    .setCancelable(cancelable)
    .create()
  bindingSetup(viewBinding, dialog)
  dialog.makeBackgroundTransparent()
  dialog.show()
}