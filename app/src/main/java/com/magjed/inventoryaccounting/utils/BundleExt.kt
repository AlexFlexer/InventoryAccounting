package com.magjed.inventoryaccounting.utils

import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import java.io.Serializable
import kotlin.reflect.KProperty

/**
 * Puts the given [enum] class instance in [this] bundle, associating it with the given key.
 */
fun Bundle.putEnum(key: String, enum: Enum<*>) {
  putString(key, enum.name)
}

/**
 * Extracts [Enum] class from [this] bundle with help of [enumValueOf].
 */
inline fun <reified T : Enum<T>> Bundle.getEnum(key: String): T {
  return enumValueOf(getString(key, ""))
}

/**
 * Puts the given [pairs] Pairs of [KProperty] and [Any]
 */
fun Bundle.putPairs(vararg pairs: Pair<KProperty<*>, Any?>): Bundle {
  pairs.forEach { (property, value) -> this.put(property.name, value) }
  return this
}

/**
 * Shortcut for [Bundle.putPairs], first crates a new [Bundle], then fills it with [pairs].
 */
fun createBundleAndPut(vararg pairs: Pair<KProperty<*>, Any?>): Bundle = Bundle().putPairs(*pairs)

/**
 * Puts in [this] bundle any supported by it type, or throws [IllegalArgumentException]
 * if [value] is a class of unsupported by [Bundle] type.
 *
 * Supported types: [Boolean], [BooleanArray] [String], [Int], [IntArray],  [Long], [LongArray],
 * [Float], [FloatArray], [Short], [ShortArray], [Byte], [ByteArray], [Char], [CharArray],
 * [CharSequence], [Bundle], [Enum], [IBinder], [Parcelable], [Serializable].
 */
fun <T> Bundle.put(key: String, value: T?) {
  when (value) {
    is Boolean -> putBoolean(key, value)
    is BooleanArray -> putBooleanArray(key, value)
    is String -> putString(key, value)
    is Int -> putInt(key, value)
    is IntArray -> putIntArray(key, value)
    is Short -> putShort(key, value)
    is ShortArray -> putShortArray(key, value)
    is Long -> putLong(key, value)
    is LongArray -> putLongArray(key, value)
    is Float -> putFloat(key, value)
    is FloatArray -> putFloatArray(key, value)
    is Byte -> putByte(key, value)
    is ByteArray -> putByteArray(key, value)
    is Char -> putChar(key, value)
    is CharArray -> putCharArray(key, value)
    is CharSequence -> putCharSequence(key, value)
    is Bundle -> putBundle(key, value)
    is Enum<*> -> putEnum(key, value)
    is IBinder -> putBinder(key, value)
    is Parcelable -> putParcelable(key, value)
    is Serializable -> putSerializable(key, value)
    else -> throw IllegalArgumentException("Type of property $key is not supported")
  }
}