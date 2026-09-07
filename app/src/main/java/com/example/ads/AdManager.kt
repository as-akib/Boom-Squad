package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log

enum class AdRewardReason {
    REFILL_LIFE,
    EXTRA_HINT,
    SKIP_LEVEL
}

/**
 * Manages rewarded actions and tactical briefings for lives and hints.
 * Operates cleanly without external WebViews or network header errors,
 * providing instant and reliable reward delivery.
 */
class AdManager(private val context: Context) {

    companion object {
        private const val TAG = "AdManager"
    }

    fun loadRewardedAd() {
        // Ready by default
    }

    fun isAdAvailable(): Boolean = true

    /**
     * Grants the reward callback seamlessly for the user action (e.g. Life Refill or Tactical Hint).
     */
    fun showRewardedAd(
        activity: Activity,
        reason: AdRewardReason,
        onRewardEarned: (AdRewardReason) -> Unit,
        onAdDismissed: () -> Unit = {}
    ) {
        Log.d(TAG, "Tactical reward granted for $reason")
        onRewardEarned(reason)
        onAdDismissed()
    }
}
