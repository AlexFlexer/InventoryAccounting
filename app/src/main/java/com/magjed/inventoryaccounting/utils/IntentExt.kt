/**
 * Simple intent extensions for the app.
 * Functions are named as simple as possible,
 * so I think you'll understand them.
 */
@file:Suppress("unused")

package com.magjed.inventoryaccounting.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.magjed.inventoryaccounting.R

/**
 * There is a warning about [android.app.PendingIntent] in the newer versions of Android Studio,
 * it states: Missing PendingIntent mutability flag.
 *
 * The documentation says if the flag is missing, it can cause app's crashing if they
 * are targeting on Android 12.
 */
fun getDefaultMutabilityFlagForPendingIntent(): Int {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
}

fun Context.startClearActivity(jClass: Class<*>?) =
  startActivity(Intent(this, jClass).apply {
    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
  })

fun Fragment.startClearActivity(jClass: Class<*>?) =
  startActivity(Intent(requireActivity(), jClass).apply {
    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
  })


fun Context.openGooglePlayStoreListing(packageName: String = this.packageName.orEmpty()) =
  startActivityOrFallback(prepareIntentForGooglePlayListing(packageName)) {
    notifyAboutNoGooglePlay()
  }

/**
 * This email sending shortcut requires apps that can send message/rfc822 messages.
 * There are not only email apps are suggested, but social networks apps too.
 */
fun Context.sendEmail(emailAddress: String, subject: String, message: String) =
  startActivityOrFallback(prepareIntentForEmail(emailAddress, subject, message))

/**
 * Unlike [sendEmail], this function requires apps that can send data with "mailto:" flag.
 * So, only email apps will be suggested as receivers of the intent.
 */
fun Context.sendEmail2(emailAddress: String, subject: String, message: String) =
  startActivityOrFallback(prepareIntentForEmail2(emailAddress, subject, message))

fun Context.dialPhone(phoneNumber: String) =
  startActivityOrFallback(prepareIntentToDialPhone(phoneNumber))

fun Context.openLocationSettings() = startActivityOrFallback(prepareIntentForLocationSettings())

fun Context.shareText(text: String) = startActivityOrFallback(prepareIntentForTextSharing(text))

fun Context.openBrowser(link: String) = startActivityOrFallback(prepareIntentForBrowser(link))

fun Context.openAppSettings(shouldOpenAsNewTask: Boolean = true) =
  startActivityOrFallback(prepareIntentForDetailSettings(packageName, shouldOpenAsNewTask)) {
    notifyAboutNoSettings()
  }

fun Context.startActivityOrFallback(intent: Intent, fallback: () -> Unit = { notifyAboutNoApp() }) =
  tryOrGiveUp({ startActivity(intent) }, { fallback() })

private fun prepareIntentForEmail(emailAddress: String, subject: String, message: String): Intent {
  return Intent(Intent.ACTION_SEND).also {
    it.type = "message/rfc822"
    it.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
    it.putExtra(Intent.EXTRA_SUBJECT, subject)
    it.putExtra(Intent.EXTRA_TEXT, message)
  }
}

fun prepareIntentForEmail2(emailAddress: String, subject: String, message: String): Intent {
  return Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$emailAddress")).also {
    it.putExtra(Intent.EXTRA_SUBJECT, subject)
    it.putExtra(Intent.EXTRA_TEXT, message)
  }
}


private fun prepareIntentForLocationSettings(): Intent {
  return Intent().also { it.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS }
}

private fun prepareIntentForTextSharing(text: String): Intent {
  return Intent().also {
    it.apply {
      type = "text/plain"
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, text)
    }
  }
}

private fun prepareIntentForBrowser(link: String): Intent {
  return Intent(Intent.ACTION_VIEW, Uri.parse(link))
}

private fun prepareIntentForDetailSettings(
  packageName: String,
  shouldOpenAsNewTask: Boolean
): Intent {
  val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
  if (shouldOpenAsNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  val uri = Uri.fromParts("package", packageName, null)
  intent.data = uri
  return intent
}

private fun prepareIntentToDialPhone(phone: String): Intent {
  return Intent().also {
    it.action = Intent.ACTION_DIAL
    it.data = Uri.parse("tel:$phone")
    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
  }
}

private fun prepareIntentForGooglePlayListing(packageName: String): Intent {
  return Intent(Intent.ACTION_VIEW).also {
    it.data =
      Uri.parse("http://play.google.com/store/apps/details?id=${packageName}")
  }
}

private fun Context.notifyAboutNoApp() = showNoAppDialog(R.string.no_app_for_action)

private fun Context.notifyAboutNoSettings() = showNoAppDialog(R.string.no_app_for_settings)

private fun Context.notifyAboutNoGooglePlay() =
  showNoAppDialog(R.string.profile_premium_manage_no_access)

private fun Context.showNoAppDialog(@StringRes message: Int) {
  createAlertDialogWithMessage(
    this,
    R.string.alert_error,
    message,
    R.string.alert_ok
  ).show()
}
