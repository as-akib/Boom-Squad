package com.example.game

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

enum class SoundType {
    BUTTON_TAP,
    WIRE_CUT_SUCCESS,
    WIRE_CUT_FAIL,
    TIMER_TICK,
    LEVEL_COMPLETE,
    EXPLOSION,
    HOORAY,
    APPLAUSE,
    FUNNY_BOING,
    FUNNY_FAIL
}

class SoundManager(private val context: Context) {

    private var isEnabled: Boolean = true
    private var toneGenerator: ToneGenerator? = null
    private val audioScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    companion object {
        private const val SAMPLE_RATE = 22050
    }

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (e: Exception) {
            Log.w("SoundManager", "ToneGenerator init failed: ${e.message}")
        }
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    /**
     * Plays sound effect corresponding to the gameplay event.
     * Incorporates funny and celebratory sounds like Hooray cheering and Applause handclaps.
     */
    fun playSound(type: SoundType) {
        if (!isEnabled) return

        try {
            when (type) {
                SoundType.LEVEL_COMPLETE, SoundType.HOORAY -> {
                    // Play celebratory Hooray Fanfare followed by joyful crowd clapping
                    playPcmSound(generateHoorayAndApplause())
                }
                SoundType.APPLAUSE -> {
                    playPcmSound(generateApplause(durationMs = 900))
                }
                SoundType.WIRE_CUT_SUCCESS -> {
                    // Play cheerful funny boing ping
                    playPcmSound(generateFunnyBoing())
                }
                SoundType.WIRE_CUT_FAIL -> {
                    // Play funny cartoon sad trombone wah-wah
                    playPcmSound(generateFunnySadWah())
                }
                SoundType.FUNNY_BOING -> {
                    playPcmSound(generateFunnyBoing())
                }
                SoundType.FUNNY_FAIL -> {
                    playPcmSound(generateFunnySadWah())
                }
                SoundType.BUTTON_TAP -> {
                    playPcmSound(generateBubblePop())
                }
                SoundType.TIMER_TICK -> {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 35)
                }
                SoundType.EXPLOSION -> {
                    playPcmSound(generateExplosionBoom())
                }
            }
        } catch (e: Exception) {
            Log.w("SoundManager", "Error playing sound $type: ${e.message}")
        }
    }

    private fun playPcmSound(samples: ShortArray) {
        audioScope.launch {
            var track: AudioTrack? = null
            try {
                val bufferSize = samples.size * 2
                track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(samples, 0, samples.size)
                track.play()

                // Wait for playback duration then release
                val durationMs = (samples.size * 1000L) / SAMPLE_RATE
                kotlinx.coroutines.delay(durationMs + 100)
            } catch (e: Exception) {
                Log.w("SoundManager", "AudioTrack playback error: ${e.message}")
            } finally {
                try {
                    track?.stop()
                    track?.release()
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Synthesizes a funny and triumphant Hooray fanfare combined with energetic crowd handclaps!
     */
    private fun generateHoorayAndApplause(): ShortArray {
        val totalDurationMs = 1500
        val totalSamples = (SAMPLE_RATE * totalDurationMs) / 1000
        val buffer = ShortArray(totalSamples)

        // Part 1: Joyful triumphant Fanfare (0ms to 700ms)
        // Chord sequence: C5 (523Hz), E5 (659Hz), G5 (784Hz) -> High C6 (1046Hz) with brassy vibrato
        val notes = listOf(
            Triple(0, 140, 523.25),   // C5
            Triple(140, 280, 659.25),  // E5
            Triple(280, 440, 783.99),  // G5
            Triple(440, 750, 1046.50)  // C6 (Triumphant hold)
        )

        for ((startMs, endMs, freq) in notes) {
            val startIdx = (SAMPLE_RATE * startMs) / 1000
            val endIdx = (SAMPLE_RATE * endMs) / 1000
            val length = endIdx - startIdx
            for (i in 0 until length) {
                val idx = startIdx + i
                if (idx >= totalSamples) break
                val t = i.toDouble() / SAMPLE_RATE
                // Brass harmonics + funny vibrato
                val vibrato = sin(2.0 * PI * 6.0 * t) * 8.0
                val f = freq + vibrato
                val wave = 0.55 * sin(2.0 * PI * f * t) +
                        0.25 * sin(2.0 * PI * (f * 2) * t) +
                        0.15 * sin(2.0 * PI * (f * 3) * t)
                val envelope = when {
                    i < length * 0.1 -> i / (length * 0.1)
                    i > length * 0.7 -> (length - i) / (length * 0.3)
                    else -> 1.0
                }
                val sampleVal = (wave * envelope * 20000).toInt().coerceIn(-32768, 32767)
                buffer[idx] = sampleVal.toShort()
            }
        }

        // Part 2: Funny Crowd Clapping / Applause (starts at 500ms and rolls through 1500ms)
        val clapStartSample = (SAMPLE_RATE * 500) / 1000
        val clapTimesMs = listOf(500, 560, 620, 670, 720, 780, 830, 890, 950, 1010, 1080, 1150, 1230, 1310, 1400)
        val random = Random(42)

        for (clapMs in clapTimesMs) {
            // Jitter for natural clapping
            val jitterMs = random.nextInt(-15, 15)
            val actualClapMs = (clapMs + jitterMs).coerceAtLeast(450)
            val clapIdx = (SAMPLE_RATE * actualClapMs) / 1000
            val clapDuration = (SAMPLE_RATE * 0.035).toInt() // 35ms burst

            for (i in 0 until clapDuration) {
                val idx = clapIdx + i
                if (idx in 0 until totalSamples) {
                    val progress = i.toDouble() / clapDuration
                    val env = exp(-progress * 5.0)
                    // High-passed noise for crisp handclap
                    val noise = (random.nextDouble() * 2.0 - 1.0)
                    val clapSample = (noise * env * 14000).toInt()
                    val mixed = (buffer[idx] + clapSample).coerceIn(-32768, 32767)
                    buffer[idx] = mixed.toShort()
                }
            }
        }

        return buffer
    }

    /**
     * Synthesizes lively handclaps
     */
    private fun generateApplause(durationMs: Int): ShortArray {
        val totalSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(totalSamples)
        val random = Random(77)
        val numClaps = (durationMs / 60)

        for (c in 0 until numClaps) {
            val clapMs = (c * (durationMs.toDouble() / numClaps)).toInt() + random.nextInt(-20, 20)
            val clapIdx = ((SAMPLE_RATE * clapMs) / 1000).coerceIn(0, totalSamples - 1)
            val clapDuration = (SAMPLE_RATE * 0.04).toInt()

            for (i in 0 until clapDuration) {
                val idx = clapIdx + i
                if (idx < totalSamples) {
                    val env = exp(-(i.toDouble() / clapDuration) * 4.5)
                    val noise = (random.nextDouble() * 2.0 - 1.0)
                    val sample = (noise * env * 16000).toInt()
                    val current = buffer[idx].toInt()
                    buffer[idx] = (current + sample).coerceIn(-32768, 32767).toShort()
                }
            }
        }
        return buffer
    }

    /**
     * Synthesizes a funny cartoon spring "boing!"
     */
    private fun generateFunnyBoing(): ShortArray {
        val durationMs = 380
        val samplesCount = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(samplesCount)

        for (i in 0 until samplesCount) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / samplesCount

            // Exponential frequency sweep upward + funny pitch wobble
            val baseFreq = 180.0 + 700.0 * (progress * progress)
            val wobble = sin(2.0 * PI * 18.0 * t) * 45.0
            val f = baseFreq + wobble

            val wave = sin(2.0 * PI * f * t)
            val env = exp(-progress * 3.5)
            buffer[i] = (wave * env * 24000).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    /**
     * Synthesizes a funny sad cartoon wah-wah trombone
     */
    private fun generateFunnySadWah(): ShortArray {
        val durationMs = 600
        val samplesCount = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(samplesCount)

        for (i in 0 until samplesCount) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / samplesCount

            // Descending sliding pitch with raspy brass harmonics
            val freq = 290.0 - (progress * 130.0) + sin(2.0 * PI * 7.0 * t) * 6.0
            val wave = 0.6 * sin(2.0 * PI * freq * t) +
                    0.25 * sin(2.0 * PI * freq * 2 * t) +
                    0.15 * sin(2.0 * PI * freq * 3 * t)

            val env = (1.0 - progress * 0.7) * (0.8 + 0.2 * sin(2.0 * PI * 9.0 * t))
            buffer[i] = (wave * env * 22000).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    /**
     * Cute bubble pop sound for button presses
     */
    private fun generateBubblePop(): ShortArray {
        val durationMs = 60
        val samplesCount = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(samplesCount)

        for (i in 0 until samplesCount) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / samplesCount
            // Rapid pitch rise then decay
            val freq = 400.0 + (1.0 - progress) * 850.0
            val wave = sin(2.0 * PI * freq * t)
            val env = exp(-progress * 7.0)
            buffer[i] = (wave * env * 18000).toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    /**
     * Low cartoon boom for explosions
     */
    private fun generateExplosionBoom(): ShortArray {
        val durationMs = 700
        val samplesCount = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(samplesCount)
        val random = Random(123)

        for (i in 0 until samplesCount) {
            val progress = i.toDouble() / samplesCount
            val env = exp(-progress * 4.0)
            val rumble = sin(2.0 * PI * (65.0 - progress * 30.0) * (i.toDouble() / SAMPLE_RATE))
            val noise = (random.nextDouble() * 2.0 - 1.0)
            val sample = (0.6 * rumble + 0.4 * noise) * env * 25000
            buffer[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
        }
        return buffer
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
