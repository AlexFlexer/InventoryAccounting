@file:Suppress("unused")

package com.magjed.inventoryaccounting.utils

import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @return true if [this] is null or [value], false otherwise.
 */
fun <T> T?.isNullOr(value: T): Boolean = this == null || this == value

/**
 * @return true if [this] is one of given [values].
 */
fun <T> T?.isOneOf(vararg values: T?): Boolean {
  values.forEach { if (this == it) return true }
  return false
}

/**
 * @return if [values] does not contain [this].
 */
fun <T> T?.isNotOneOf(vararg values: T?): Boolean {
  values.forEach { if (this == it) return false }
  return true
}

/**
 * Tries to perform some [actionToTry], and if it throws an exception, returns [defaultVal].
 */
fun <T> tryOrGiveUp(defaultVal: T, actionToTry: () -> T): T =
  try {
    actionToTry()
  } catch (t: Throwable) {
    defaultVal
  }

/**
 * Just like tryOrGiveUp(defaultVal: T, actionToTry: () -> T): T, except it returns result
 * of execution of [attemptFailed] as default value.
 */
fun <T> tryOrGiveUp(actionToTry: () -> T, attemptFailed: () -> T): T =
  try {
    actionToTry()
  } catch (t: Throwable) {
    attemptFailed()
  }

/**
 * Creates [DateTimeFormatter] from given [format] and using [locale].
 */
fun dateTimeFormatterOf(format: String, locale: Locale = Locale.ENGLISH): DateTimeFormatter =
  DateTimeFormatter.ofPattern(format, locale)