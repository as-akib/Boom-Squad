package com.example.data.models

enum class MechanicType {
    WIRE_CUTTING,
    CODE_ENTRY,
    BUTTON_SEQUENCE,
    SYMBOL_MATCHING,
    SPEED_TAPPING,
    FREQUENCY_TUNING,
    POLARITY_MATCHING,
    ARITHMETIC_PUZZLE,
    ROTARY_DIAL,
    CIRCUIT_FUSES
}

data class WireItem(
    val id: Int,
    val colorHex: Long,
    val colorName: String,
    val isCut: Boolean = false,
    val isStriped: Boolean = false
)

data class WireCuttingConfig(
    val wires: List<WireItem>,
    val correctWireIndex: Int,
    val clueText: String,
    val hintClarification: String,
    val isFatalWrongCut: Boolean = true
)

data class CodeEntryConfig(
    val targetCode: String,
    val clueText: String,
    val hintClarification: String,
    val scatteredClues: List<String> = emptyList(),
    val length: Int = 4,
    val maxAttempts: Int = 3
)

enum class PuzzleSymbol(val displayName: String, val glyph: String) {
    SKULL("Skull", "💀"),
    STAR("Star", "⭐"),
    BOLT("Bolt", "⚡"),
    FIRE("Fire", "🔥"),
    HEART("Heart", "❤️"),
    GEAR("Gear", "⚙️"),
    KEY("Key", "🔑"),
    DIAMOND("Diamond", "💎")
}

data class SymbolMatchingConfig(
    val availableSymbols: List<PuzzleSymbol>,
    val targetPattern: List<PuzzleSymbol>,
    val clueText: String,
    val hintClarification: String,
    val cipherLegend: Map<String, String> = emptyMap(),
    val previewSeconds: Int = 0
)

data class ButtonItem(
    val id: Int,
    val label: String,
    val colorHex: Long
)

data class ButtonSequenceConfig(
    val buttons: List<ButtonItem>,
    val correctSequence: List<Int>, // button IDs in correct order
    val clueText: String,
    val hintClarification: String,
    val isReversed: Boolean = false,
    val hasDistractor: Boolean = false
)

data class SpeedTappingConfig(
    val targetMinPressure: Float, // e.g. 0.60f
    val targetMaxPressure: Float, // e.g. 0.85f
    val clueText: String,
    val hintClarification: String,
    val dialIndirectClue: String = "",
    val tapsRequired: Int = 4
)

data class FrequencyTuningConfig(
    val targetFrequencyKHz: Int,
    val minFrequencyKHz: Int = 80,
    val maxFrequencyKHz: Int = 200,
    val toleranceKHz: Int = 3,
    val clueText: String,
    val hintClarification: String,
    val stationCallsign: String = "SIG-RADIO"
)

data class PolaritySwitch(
    val id: Int,
    val label: String,
    val correctPositive: Boolean
)

data class PolarityMatchingConfig(
    val switches: List<PolaritySwitch>,
    val clueText: String,
    val hintClarification: String
)

data class ArithmeticPuzzleConfig(
    val equationDisplay: String,
    val options: List<Int>,
    val correctIndex: Int,
    val clueText: String,
    val hintClarification: String
)

data class RotaryDialConfig(
    val targetValue: Int, // e.g. 0..99
    val tolerance: Int = 1,
    val clueText: String,
    val hintClarification: String,
    val dialLabel: String = "SAFE TUMBLER"
)

data class FuseItem(
    val id: Int,
    val label: String,
    val amperage: Int,
    val colorHex: Long,
    val isRequired: Boolean
)

data class CircuitFusesConfig(
    val fuses: List<FuseItem>,
    val targetAmperage: Int,
    val clueText: String,
    val hintClarification: String
)

sealed class MechanicData {
    data class Wires(val config: WireCuttingConfig) : MechanicData()
    data class Code(val config: CodeEntryConfig) : MechanicData()
    data class Sequence(val config: ButtonSequenceConfig) : MechanicData()
    data class Symbols(val config: SymbolMatchingConfig) : MechanicData()
    data class Tapping(val config: SpeedTappingConfig) : MechanicData()
    data class Frequency(val config: FrequencyTuningConfig) : MechanicData()
    data class Polarity(val config: PolarityMatchingConfig) : MechanicData()
    data class Arithmetic(val config: ArithmeticPuzzleConfig) : MechanicData()
    data class Rotary(val config: RotaryDialConfig) : MechanicData()
    data class Fuses(val config: CircuitFusesConfig) : MechanicData()
}

data class MechanicInstance(
    val type: MechanicType,
    val title: String,
    val data: MechanicData
)

data class ChapterConfig(
    val id: Int,
    val name: String,
    val subtitle: String,
    val themeColorHex: Long
)

data class DifficultyProfile(
    val sector: Int,
    val levelInSector: Int,
    val mechanicsCount: Int,
    val timeLimitSeconds: Int,
    val clueTrickinessTier: Int,
    val decoyDensity: Float,
    val freeHintsCount: Int
)

data class LevelConfig(
    val levelNumber: Int,
    val chapterId: Int, // Represents sector number
    val levelInSector: Int = 1,
    val title: String,
    val timeLimitSeconds: Int,
    val mechanics: List<MechanicInstance>,
    val freeHintsCount: Int = 1,
    val isTutorial: Boolean = false,
    val tutorialClue: String? = null
)

object SectorNavigationUtils {
    const val TUTORIAL_SECTOR_LEVELS = 10
    const val PROCEDURAL_SECTOR_LEVELS = 5

    fun getSectorAndLevel(globalLevel: Int): Pair<Int, Int> {
        return if (globalLevel <= TUTORIAL_SECTOR_LEVELS) {
            Pair(1, globalLevel)
        } else {
            val offset = globalLevel - (TUTORIAL_SECTOR_LEVELS + 1)
            val sector = 2 + (offset / PROCEDURAL_SECTOR_LEVELS)
            val levelInSector = 1 + (offset % PROCEDURAL_SECTOR_LEVELS)
            Pair(sector, levelInSector)
        }
    }

    fun getGlobalLevelNumber(sector: Int, levelInSector: Int): Int {
        return if (sector == 1) {
            levelInSector.coerceIn(1, TUTORIAL_SECTOR_LEVELS)
        } else {
            TUTORIAL_SECTOR_LEVELS + ((sector - 2) * PROCEDURAL_SECTOR_LEVELS) + levelInSector
        }
    }

    fun getLevelsCountForSector(sector: Int): Int {
        return if (sector == 1) TUTORIAL_SECTOR_LEVELS else PROCEDURAL_SECTOR_LEVELS
    }
}
