package com.magjed.inventoryaccounting.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


//todo add enum support

/**
 * Retrieves the given argument from [Fragment.getArguments] associated with the [key].
 * If [key] is null, property's name will be used as the key. This argument must not be null,
 * or an exception will be thrown.
 *
 * @throws IllegalStateException if an argument associated with the given [key] is not found or
 * is not type of [T].
 */
fun <T : Any> argument(key: String? = null): ReadWriteProperty<Fragment, T> =
  FragmentArgumentDelegate(key)

/**
 * Retrieves the given argument from [Fragment.getArguments] associated with the [key].
 * If [key] is null, property's name will be used as the key. The argument can be null.
 */
fun <T : Any> argumentNullable(key: String? = null): ReadWriteProperty<Fragment, T?> =
  FragmentNullableArgumentDelegate(key)


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Internal thing ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

class FragmentArgumentDelegate<T : Any>(private val key: String?) :
  ReadWriteProperty<Fragment, T> {

  @Suppress("UNCHECKED_CAST")
  override fun getValue(
    thisRef: Fragment,
    property: KProperty<*>,
  ): T {
    val argKey = key ?: property.name
    return thisRef.arguments
      ?.get(argKey) as? T
      ?: throw IllegalArgumentException("Property ${property.name} could not be read")
  }

  override fun setValue(
    thisRef: Fragment,
    property: KProperty<*>, value: T,
  ) {
    val args = thisRef.arguments
      ?: Bundle().also(thisRef::setArguments)
    val argKey = key ?: property.name
    args.put(argKey, value)
  }
}

class FragmentNullableArgumentDelegate<T : Any?>(private val key: String?) :
  ReadWriteProperty<Fragment, T?> {

  @Suppress("UNCHECKED_CAST")
  override fun getValue(
    thisRef: Fragment,
    property: KProperty<*>,
  ): T? {
    val argKey = key ?: property.name
    return thisRef.arguments?.get(argKey) as? T
  }

  override fun setValue(
    thisRef: Fragment,
    property: KProperty<*>, value: T?,
  ) {
    val args = thisRef.arguments
      ?: Bundle().also(thisRef::setArguments)
    val argKey = key ?: property.name
    value?.let { args.put(argKey, it) } ?: args.remove(argKey)
  }
}