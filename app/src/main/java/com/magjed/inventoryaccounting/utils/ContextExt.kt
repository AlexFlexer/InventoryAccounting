@file:Suppress("unused")

package com.magjed.inventoryaccounting.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.hardware.display.DisplayManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Display
import androidx.annotation.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import java.util.regex.Pattern
import kotlin.random.Random


/**
 * @return [Uri] to the ringtone set by the user.
 */
fun Context.getDeviceRingtoneUri(): Uri {
  return RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE)
}

/**
 * @return currently set by the user ringtone.
 */
fun Context.getDeviceRingtone(): Ringtone {
  val defaultRingtoneUri: Uri = getDeviceRingtoneUri()
  return RingtoneManager.getRingtone(this, defaultRingtoneUri)
}

/**
 * Turns screen on from any place: services, activities, fragments.
 *
 * @param acquireTimeout is a millis value during which the screen wakelock is acquired.
 */
@Suppress("deprecation")
fun Context.turnScreenOn(acquireTimeout: Long = 3000L) {
  getWakeLockByLevel(
    PowerManager.FULL_WAKE_LOCK
      or PowerManager.ACQUIRE_CAUSES_WAKEUP
      or PowerManager.SCREEN_DIM_WAKE_LOCK
  )?.acquire(acquireTimeout)
}

/**
 * This function allows you to get a system service of the given type [T].
 *
 * @return the service of type [T] or null if the service is not supported by the system.
 */
inline fun <reified T> Context.getSystemServiceCompat(): T? {
  return ContextCompat.getSystemService(this, T::class.java)
}

/**
 * This function allows you to get a system service of the given type [T].
 *
 * @return the service of type [T] or an exception will be thrown if the service
 * is not supported by the system.
 */
inline fun <reified T> Context.getSystemServiceNotNull(): T {
  return try {
    getSystemServiceCompat()!!
  } catch (e: NullPointerException) {
    throw IllegalArgumentException("The service of class ${T::class.simpleName} is not supported!")
  }
}

/**
 * @return app's name as the user sees it on the launcher.
 */
fun Context.getLauncherAppName(): String {
  @IdRes val nameStrId = applicationInfo?.labelRes
  return if (nameStrId == null || nameStrId == 0)
    applicationInfo?.nonLocalizedLabel?.toString().orEmpty()
  else getString(nameStrId)
}

/**
 * Gets the given string (referred to by [resId] ID) formatted appropriately for
 * the given [count].
 */
fun Context.plurals(@PluralsRes resId: Int, count: Int) =
  resources.getQuantityString(resId, count, count)

/**
 * Gets a string by the given resource ID and turns it into [Pattern].
 */
fun Context.getStringAsPattern(@StringRes res: Int, flags: Int = 0): Pattern =
  getString(res).toPattern(flags)

/**
 * Gets [Drawable] by the given [id] with help of [ResourcesCompat].
 */
fun Context.getDrawableCompat(@DrawableRes id: Int, theme: Resources.Theme? = null): Drawable? =
  ResourcesCompat.getDrawable(resources, id, theme)

/**
 * Gets color by the given [id] with help of [ResourcesCompat].
 */
@ColorInt
fun Context.getColorCompat(@ColorRes id: Int, theme: Resources.Theme? = null): Int =
  ResourcesCompat.getColor(resources, id, theme)

/**
 * Checks if the screen is on. This function handles Samsung's Always-On display thing.
 *
 * @return true if the screen is on and false otherwise.
 */
@Suppress("ObsoleteSdkInt", "deprecation")
fun Context.isScreenOn(): Boolean {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
    // no display service means the device has no displays and this the screen
    // can't be turned on
    val displayService = getSystemServiceCompat<DisplayManager>() ?: return false
    for (display in displayService.displays) {
      if (display.state == Display.STATE_ON) {
        return true
      }
    }
    false
  } else {
    val pm = getSystemServiceCompat<PowerManager>()
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
      pm?.isScreenOn == true
    else pm?.isInteractive == true
  }
}

/**
 * Turns vector image into a [Bitmap].
 *
 * @param drawableId is a drawable resource ID that needs to be resolved and turned into [Bitmap].
 */
fun Context.vectorToBitmap(@DrawableRes drawableId: Int): Bitmap? {
  val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
  val bitmap = createBitmap(
    drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
  )
  val canvas = Canvas(bitmap)
  drawable.setBounds(0, 0, canvas.width, canvas.height)
  drawable.draw(canvas)
  return bitmap
}

/**
 * Makes the phone vibrate [millis] milliseconds.
 */
@Suppress("deprecation")
fun Context.vibrate(millis: Long) {
  val vibrator = getSystemServiceCompat<Vibrator>()
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    vibrator?.vibrate(
      VibrationEffect.createOneShot(
        millis,
        VibrationEffect.DEFAULT_AMPLITUDE
      )
    )
  } else vibrator?.vibrate(millis)
}

/**
 * Vibrates with the given pattern (all in millis): [delay_before_start, vibro, relax, vibro, relax].
 *
 * @param pattern
 * @param repeat determines whether the vibration should repeat.
 */
@Suppress("deprecation")
fun Context.vibrate(pattern: LongArray, repeat: Boolean) {
  val vibrator = getSystemServiceCompat<Vibrator>()
  val repeatIndex = if (repeat) 0 else -1
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    vibrator?.vibrate(
      VibrationEffect.createWaveform(
        pattern,
        VibrationEffect.DEFAULT_AMPLITUDE
      )
    )
  } else vibrator?.vibrate(pattern, repeatIndex)
}

/**
 * Copies the given piece of text in the clipboard.
 */
fun Context.copyText(text: CharSequence, appName: String) {
  val clip = ClipData.newPlainText("Copied from $appName", text)
  getSystemServiceCompat<ClipboardManager>()?.setPrimaryClip(clip)
}

/**
 * Checks whether all the permissions are granted by the user.
 *
 * @param permissions are the permissions to check.
 */
fun Context.allGranted(vararg permissions: String) = permissions.all {
  ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

/**
 * Checks whether at least one the permissions is granted by the user.
 *
 * @param permissions are the permissions to check.
 */
fun Context.isSomeGranted(vararg permissions: String) = permissions.any {
  ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

/**
 * Checks whether any of the permissions needs to be explained to the user.
 *
 * @param permissions are the permissions to check.
 */
fun Activity.isNeedRationale(vararg permissions: String) = permissions.any {
  ActivityCompat.shouldShowRequestPermissionRationale(this, it)
}

/**
 * Gets [PowerManager]'s wakelock by it's level.
 *
 * @param wakelockLevel is an Integer representing WakeLock's level.
 * @param wakeLockRefCounted is a value to be passed to [PowerManager.WakeLock.setReferenceCounted].
 * @param tag is a tag string that is used as name for the newly created [PowerManager.WakeLock].
 */
fun Context.getWakeLockByLevel(
  wakelockLevel: Int,
  wakeLockRefCounted: Boolean = false,
  tag: String = "ProximityWakeLockTag${Random.nextInt()}"
): PowerManager.WakeLock? {
  val pm: PowerManager = getSystemService() ?: return null
  return if (pm.isWakeLockLevelSupported(wakelockLevel)) {
    pm.newWakeLock(wakelockLevel, tag)
      .apply { setReferenceCounted(wakeLockRefCounted) }
  } else null
}
