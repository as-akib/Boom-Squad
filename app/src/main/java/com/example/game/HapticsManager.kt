package com.example.game

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

enum class HapticType {
    TAP,
    WIRE_CUT,
    CORRECT_INPUT,
    INCORRECT_INPUT,
    LEVEL_COMPLETE,
    EXPLOSION
}

class HapticsManager(private val context: Context) {

    private var isEnabled: Boolean = true

    @Suppress("DEPRECATION")
    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            null
        }
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    @Suppress("DEPRECATION")
    fun triggerHaptic(type: HapticType) {
        if (!isEnabled || vibrator?.hasVibrator() != true) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (type) {
                    HapticType.TAP -> {
                        vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    HapticType.WIRE_CUT -> {
                        // Snappy sharp click
                        vibrator?.vibrate(VibrationEffect.createOneShot(45, 200))
                    }
                    HapticType.CORRECT_INPUT -> {
                        // Double pulse
                        val pattern = longArrayOf(0, 30, 40, 50)
                        val amplitudes = intArrayOf(0, 150, 0, 220)
                        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
                    }
                    HapticType.INCORRECT_INPUT -> {
                        // Heavy rejection buzz
                        vibrator?.vibrate(VibrationEffect.createOneShot(120, 255))
                    }
                    HapticType.LEVEL_COMPLETE -> {
                        // Celebratory rhythm pulse
                        val pattern = longArrayOf(0, 40, 50, 60, 50, 100)
                        val amplitudes = intArrayOf(0, 120, 0, 180, 0, 255)
                        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
                    }
                    HapticType.EXPLOSION -> {
                        // Long heavy rumbling pulse
                        val pattern = longArrayOf(0, 100, 30, 120, 30, 200)
                        val amplitudes = intArrayOf(0, 255, 100, 255, 150, 255)
                        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
                    }
                }
            } else {
                when (type) {
                    HapticType.TAP -> vibrator?.vibrate(20)
                    HapticType.WIRE_CUT -> vibrator?.vibrate(40)
                    HapticType.CORRECT_INPUT -> vibrator?.vibrate(longArrayOf(0, 30, 40, 50), -1)
                    HapticType.INCORRECT_INPUT -> vibrator?.vibrate(120)
                    HapticType.LEVEL_COMPLETE -> vibrator?.vibrate(longArrayOf(0, 40, 50, 60, 50, 100), -1)
                    HapticType.EXPLOSION -> vibrator?.vibrate(longArrayOf(0, 100, 30, 120, 30, 200), -1)
                }
            }
        } catch (e: Exception) {
            // Ignore haptic failure gracefully on devices with no vibrator
        }
    }
}
