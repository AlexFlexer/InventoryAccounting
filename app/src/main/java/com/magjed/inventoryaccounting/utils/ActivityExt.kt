@file:Suppress("unused")

package com.magjed.inventoryaccounting.utils

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment

/**
 * Tries to turn user's lock screen off to show [this] activity.
 * Don't forget to add [android.Manifest.permission.DISABLE_KEYGUARD] permission
 * in the manifest!
 */
@Suppress("deprecation")
fun Activity.turnScreenOnAndKeyguardOff() {
  window.addFlags(
    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
      WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
      WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
      WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
  )
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    getSystemService<KeyguardManager>()?.requestDismissKeyguard(this, null)
  }
}

/**
 * Enforces the system to keep the screen on when the user is having the app in foreground.
 *
 * @param keep determines whether the screen should be enforced to keep awaken.
 */
fun Activity.keepScreenOn(keep: Boolean) {
  if (keep) window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
  else window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

/**
 * Enforces the system to keep the screen on when the user is having the app in foreground.
 *
 * @param keep determines whether the screen should be enforced to keep awaken.
 */
fun Fragment.keepScreenOn(keep: Boolean) = activity?.keepScreenOn(keep)

/**
 * Shows soft input for the main activity, focusing the input on currently focused view, if any.
 */
fun Activity.showKeyboard() = getInputManagerAndFocus { inputMethodManager, view ->
  inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * Hides soft input from the main activity.
 */
fun Activity.hideKeyboard() = getInputManagerAndFocus { inputMethodManager, view ->
  inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Starts activity of the type [A] using [Context] of [this] [Fragment]. Also passes the given
 * [extras] to the called activity.
 */
inline fun <reified A : Activity> Fragment.startActivity(extras: Bundle? = null) {
  context?.startActivity<A>(extras)
}

/**
 * Starts activity of the type [A] using [this] [Context] object. Also passes the given
 * [extras] to the called activity.
 */
inline fun <reified A : Activity> Context.startActivity(extras: Bundle? = null) {
  startActivity(Intent(this, A::class.java).apply { putExtras(extras ?: return@apply) })
}

private fun Activity.getInputManagerAndFocus(onGotFocus: (InputMethodManager, View) -> Unit) {
  this.currentFocus?.let {
    onGotFocus(it.context.getSystemServiceNotNull(), it)
  }
}