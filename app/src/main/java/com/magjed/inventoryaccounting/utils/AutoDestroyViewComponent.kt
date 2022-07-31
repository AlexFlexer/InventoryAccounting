@file:Suppress("unused")

package com.magjed.inventoryaccounting.utils

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * This is a delegate for [LifecycleOwner] to get your component of type [T] when the lifecycle
 * owner is not destroyed and at least created.
 *
 * @param componentInitializer is a function that returns desired instance of [T].
 * @param lifecycleStateChecker is a predicate to check whether the given component can be
 * instantiated and used when the lifecycle is in the given state.
 * @param onViewDestroyedCallback is a callback to be invoked when the lifecycle owner is
 * about to be destroyed and your [T] instance is about to become unavailable (i.e. null).
 * When this callback is invoked, [T] instance is not null yet, you can dispose it if needed.
 */
fun <T> LifecycleOwner.autoDestroyLifecycleComponent(
  componentInitializer: () -> T,
  onViewDestroyedCallback: ((T) -> Unit)? = null,
  lifecycleStateChecker: (Lifecycle.State) -> Boolean = {
    it.isAtLeast(Lifecycle.State.CREATED) && it != Lifecycle.State.DESTROYED
  }
): ReadOnlyProperty<LifecycleOwner, T> {
  return AutoDestroyComponent(
    this.lifecycle,
    componentInitializer,
    onViewDestroyedCallback,
    lifecycleStateChecker
  )
}

/**
 * This is a delegate for [LifecycleOwner] to get your component of type [T] when the activity
 * is not destroyed and at least initialized.
 *
 * @param componentInitializer is a function that returns desired instance of [T].
 * @param onViewDestroyedCallback is a callback to be invoked when the lifecycle owner is
 * about to be destroyed and your [T] instance is about to become unavailable (i.e. null).
 * When this callback is invoked, [T] instance is not null yet, you can dispose it if needed.
 */
fun <T> ComponentActivity.autoDestroyLifecycleComponent(
  componentInitializer: () -> T,
  onViewDestroyedCallback: ((T) -> Unit)? = null
): ReadOnlyProperty<LifecycleOwner, T> {
  return autoDestroyLifecycleComponent(
    componentInitializer, onViewDestroyedCallback,
    lifecycleStateChecker = {
      // note: you can access activity's view in onCreate() method, thus
      // the minimum lifecycle state is INITIALIZED
      it.isAtLeast(Lifecycle.State.INITIALIZED) && it != Lifecycle.State.DESTROYED
    }
  )
}

/**
 * This is a delegate for [Fragment] to get your component of type [T] when Fragment's
 * view is available.
 *
 * @param componentInitializer is a function that returns desired instance of [T].
 * @param onViewDestroyedCallback is a callback to be invoked when the fragment's view is
 * about to be destroyed and your [T] instance is about to become unavailable (i.e. null).
 * When this callback is invoked, [T] instance is not null yet, you can dispose it if needed.
 */
fun <T> Fragment.autoDestroyViewComponent(
  componentInitializer: () -> T,
  onViewDestroyedCallback: ((T) -> Unit)? = null
): ReadOnlyProperty<Fragment, T> {
  return AutoDestroyOnViewDestroyedComponent(this, componentInitializer, onViewDestroyedCallback)
}

private class AutoDestroyOnViewDestroyedComponent<I, T>(
  private val mFragment: Fragment,
  private val mComponentInitializer: () -> T,
  private val onViewDestroyedCallback: ((T) -> Unit)?
) : ReadOnlyProperty<I, T>, DefaultLifecycleObserver {

  init {
    mFragment.lifecycle.addObserver(this)
  }

  private var mComponentCreator: AutoDestroyComponent<I, T>? = null

  override fun onDestroy(owner: LifecycleOwner) {
    // keep in mind that this method is called twice:
    // the first call happens when fragment's view is destroyed and the second one happens
    // when the fragment is destroyed itself
    mComponentCreator = null
  }

  override fun getValue(thisRef: I, property: KProperty<*>): T {
    val state = mFragment.lifecycle.currentState
    require(state.isAtLeast(Lifecycle.State.CREATED) && state != Lifecycle.State.DESTROYED) {
      "Accessing lifecycle-aware component beyond lifecycle bounds"
    }
    // looks like we can safely access viewLifecycleOwner of mFragment here
    if (mComponentCreator == null) {
      val viewLifecycle = mFragment.viewLifecycleOwner.lifecycle
      // by doing so, we avoid leaking mComponentCreator instance,
      // so when the fragment's view is destroyed, mComponentCreator is destroyed too
      viewLifecycle.addObserver(this)
      // no need to explicit subscribe mComponentCreator instance to fragment's view
      // lifecycle: it's done under the hood
      mComponentCreator = AutoDestroyComponent(
        viewLifecycle,
        mComponentInitializer,
        onViewDestroyedCallback,
        mLifecycleStateChecker = {
          // checking INITIALIZED state is needed here since it is totally okay
          // to access fragment's view and viewLifecycleOwner in onViewCreated callback,
          // but fragment's view state is INITIALIZED there, not "created"
          it.isAtLeast(Lifecycle.State.INITIALIZED) && it != Lifecycle.State.DESTROYED
        }
      )
    }
    // don't worry about !!: at this line of the code, it is guaranteed that
    // mComponentCreator is not null, even though Kotlin compiler can't perform
    // smart cast here
    return mComponentCreator!!.getValue(thisRef, property)
  }
}

private class AutoDestroyComponent<I, T>(
  private val mLifecycle: Lifecycle,
  private val mComponentInitializer: () -> T,
  private val onViewDestroyedCallback: ((T) -> Unit)?,
  private val mLifecycleStateChecker: (Lifecycle.State) -> Boolean = {
    it.isAtLeast(Lifecycle.State.CREATED) && it != Lifecycle.State.DESTROYED
  }
) : ReadOnlyProperty<I, T>, DefaultLifecycleObserver {

  private var mComponent: T? = null

  init {
    mLifecycle.addObserver(this)
  }

  override fun getValue(thisRef: I, property: KProperty<*>): T {
    return getComponentOrThrow()
  }

  override fun onDestroy(owner: LifecycleOwner) {
    onViewDestroyedCallback?.invoke(mComponent!!)
    mComponent = null
  }

  private fun getComponentOrThrow(): T {
    require(mLifecycleStateChecker(mLifecycle.currentState)) {
      "Accessing view-dependent component before it is created or after it was destroyed"
    }
    if (mComponent == null) {
      mComponent = mComponentInitializer()
    }
    return mComponent!!
  }
}