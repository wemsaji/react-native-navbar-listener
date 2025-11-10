package com.reactnativenavbarlistener

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE
import androidx.core.view.WindowInsetsCompat
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule

/**
 * React Native bridge module for detecting Android navigation bar height changes.
 *
 * ## Features
 * - Emits `NavBarHeightChanged` event whenever the navigation bar height changes.
 * - Supports Android 11+ via [WindowInsetsAnimationCompat].
 *
 * ## Exposed methods (for JS)
 * - `getNavBarHeight()`: Returns the current navigation bar height (in dp).
 * - `addListener()` / `removeListeners()`: Required stubs for React Native's event system.
 */
class NavBarListenerModule(
    private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val NAME = "NavBarListener"
        private const val EVENT_NAME = "NavBarHeightChanged"
    }

    override fun getName(): String = NAME

    /**
     * Returns the current navigation bar height (in dp) as a Promise to JS.
     */
    @ReactMethod
    fun getNavBarHeight(promise: Promise) {
        val navBarHeight = getCurrentNavBarHeight()
        promise.resolve(navBarHeight)
    }

    /**
     * Called when the module is initialized.
     * Attaches appropriate listeners based on Android API version.
     */
    override fun initialize() {
        super.initialize()
        reactContext.addLifecycleEventListener(
            object : LifecycleEventListener {
                override fun onHostResume() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        attachInsetsAnimationCallback()
                    }
                }
                override fun onHostPause() {}
                override fun onHostDestroy() {}
            }
        )
    }

    /**
     * Android 11+ listener using [WindowInsetsAnimationCompat].
     * Detects changes in navigation bar height during animations (e.g., gesture navigation).
     */
    @RequiresApi(Build.VERSION_CODES.R)
    private fun attachInsetsAnimationCallback() {
        val activity = currentActivity ?: return
        val rootView = activity.window.decorView

        ViewCompat.setWindowInsetsAnimationCallback(
            rootView,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    sendNavBarHeightChangedEvent()
                    return insets
                }
            }
        )
    }

    /**
     * Calculates the current navigation bar height in dp.
     */
    private fun getCurrentNavBarHeight(): Int {
        val activity = currentActivity ?: return 0
        val insets = ViewCompat.getRootWindowInsets(activity.window.decorView)
        val heightPx = insets?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
        val density = reactContext.resources.displayMetrics.density
        return (heightPx / density).toInt()
    }

    /**
     * Emits the `NavBarHeightChanged` event to JS with the current navigation bar height.
     */
    private fun sendNavBarHeightChangedEvent() {
        val navBarHeight = getCurrentNavBarHeight()
        sendEvent(EVENT_NAME, navBarHeight)
    }

    /**
     * Sends a device event to the JS layer if the React instance is active.
     */
    private fun sendEvent(eventName: String, height: Int) {
        if (!reactContext.hasActiveReactInstance()) {
            Log.w(NAME, "sendEvent skipped: React instance not active or null")
            return
        }

        reactContext.runOnUiQueueThread {
            try {
                reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                    .emit(eventName, height)
                Log.d(NAME, "sendEvent($eventName, $height) emitted successfully")
            } catch (e: Exception) {
                Log.e(NAME, "sendEvent failed: ${e.message}", e)
            }
        }
    }

    // Required for RN EventEmitter support (no-op)
    @ReactMethod fun addListener(eventName: String) {}
    @ReactMethod fun removeListeners(count: Int) {}
}
