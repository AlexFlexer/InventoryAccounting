package com.magjed.inventoryaccounting.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Function for creating [RoomDatabase] of type [D] with help of [this] [Context].
 *
 * @param D is type of the database returned.
 * @param dbName is the database name. By default, it is [Class.getName].
 * @param customBuilderSetup is a function to be invoked for custom parameters application
 * on the given [RoomDatabase.Builder] object.
 */
inline fun <reified D : RoomDatabase> Context.getOrCreateDatabase(
  dbName: String = D::class.java.name,
  customBuilderSetup: RoomDatabase.Builder<D>.() -> Unit = {}
): D {
  return Room.databaseBuilder(this, D::class.java, dbName)
    .apply(customBuilderSetup)
    .build()
}

/**
 * Shows the given [textRes] as toast message.
 */
fun Activity.toast(@StringRes textRes: Int) {
  Toast.makeText(this, textRes, Toast.LENGTH_LONG).show()
}

/**
 * Shows the given [text] as toast message.
 */
fun Activity.toast(text: CharSequence) {
  Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

/**
 * Shows the given [text] as toast message.
 */
fun Fragment.toast(text: CharSequence) = activity?.toast(text)

/**
 * Shows the given [textRes] as toast message.
 */
fun Fragment.toast(@StringRes textRes: Int) = activity?.toast(textRes)

/**
 * Gets [LayoutInflater] service from [View] context.
 */
val View.layoutInflater: LayoutInflater
  get() = this.context.getSystemService()!!

/**
 * Gets the text the user has entered in [this] EditText.
 */
fun EditText.getContent(): String {
  return this.editableText.toString()
}

/**
 * Converts pixels to density-independent pixels.
 */
fun Int.toDp(context: Context): Int =
  TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
  ).toInt()

/**
 * Converts text size to scaled pixels.
 */
fun Int.toSp(context: Context): Float =
  TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this.toFloat(),
    context.resources.displayMetrics
  )

/**
 * Converts density-independent pixels to pixels.
 */
val Int.toPx: Int
  get() = (this * Resources.getSystem().displayMetrics.density).toInt()