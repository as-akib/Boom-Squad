package com.example.data

import com.example.data.models.ArithmeticPuzzleConfig
import com.example.data.models.ButtonItem
import com.example.data.models.ButtonSequenceConfig
import com.example.data.models.ChapterConfig
import com.example.data.models.CircuitFusesConfig
import com.example.data.models.CodeEntryConfig
import com.example.data.models.DifficultyProfile
import com.example.data.models.FrequencyTuningConfig
import com.example.data.models.FuseItem
import com.example.data.models.LevelConfig
import com.example.data.models.MechanicData
import com.example.data.models.MechanicInstance
import com.example.data.models.MechanicType
import com.example.data.models.PolarityMatchingConfig
import com.example.data.models.PolaritySwitch
import com.example.data.models.PuzzleSymbol
import com.example.data.models.RotaryDialConfig
import com.example.data.models.SectorNavigationUtils
import com.example.data.models.SpeedTappingConfig
import com.example.data.models.SymbolMatchingConfig
import com.example.data.models.WireCuttingConfig
import com.example.data.models.WireItem
import com.example.game.LogicClueEngine
import kotlin.random.Random

object LevelRepository {

    // Wire palette colors
    private val WIRE_COLORS = listOf(
        Pair(0xFFE53935, "Red"),
        Pair(0xFF1E88E5, "Blue"),
        Pair(0xFF43A047, "Green"),
        Pair(0xFFFDD835, "Yellow"),
        Pair(0xFFFFFFFF, "White"),
        Pair(0xFF212121, "Black")
    )

    private val BUTTON_PALETTE = listOf(
        Triple(1, "ALPHA", 0xFFE53935),
        Triple(2, "BETA", 0xFF1E88E5),
        Triple(3, "GAMMA", 0xFF43A047),
        Triple(4, "DELTA", 0xFFFDD835),
        Triple(5, "EPSILON", 0xFF8E24AA)
    )

    // Pre-authored Sector 1 Tutorial Levels for each of the 8 mechanics
    private val SECTOR_1_TUTORIALS: List<LevelConfig> = listOf(
        // Level 1: Wire Cutting Tutorial
        LevelConfig(
            levelNumber = 1,
            chapterId = 1,
            levelInSector = 1,
            title = "Conductor Isolation",
            timeLimitSeconds = 90,
            freeHintsCount = 99,
            isTutorial = true,
            tutorialClue = "Tap the designated wire to sever it safely.",
            mechanics = listOf(
                MechanicInstance(
                    type = MechanicType.WIRE_CUTTING,
                    title = "Primary Wire Rack",
                    data = MechanicData.Wires(
                        WireCuttingConfig(
                            wires = listOf(
                                WireItem(1, 0xFFE53935, "Red"),
                                WireItem(2, 0xFF1E88E5, "Blue"),
                                WireItem(3, 0xFF43A047, "Green")
                            ),
                            correctWireIndex = 1, // Cut Blue
                            clueText = "Cut the BLUE wire (Conductor #2).",
                            hintClarification = "Direct clue: Tap the BLUE wire."
                        )
                    )
                )
            )
        ),
        // Level 2: Keypad Code Entry Tutorial
        LevelConfig(
            levelNumber = 2,
            chapterId = 1,
            levelInSector = 2,
            title = "Keypad Passcode",
            timeLimitSeconds = 90,
            freeHintsCount = 99,
            isTutorial = true,
            tutorialClue = "Enter the 4-digit code using the numeric keypad.",
            mechanics = listOf(
                MechanicInstance(
                    type = MechanicType.CODE_ENTRY,
                    title = "Digital Security Lock",
                    data = MechanicData.Code(
                        CodeEntryConfig(
                            targetCode = "4821",
                            clueText = "Standard maintenance code: 4821",
                            hintClarification = "Direct clue: Type 4 - 8 - 2 - 1.",
                            scatteredClues = listOf("RELAY: 48", "TERMINAL: 21")
                        )
                    )
                )
            )
        ),
        // Level 3: Button Sequence Tutorial
        LevelConfig(
            levelNumber = 3,
            chapterId = 1,
            levelInSector = 3,
            title = "Relay Sequence",
            timeLimitSeconds = 90,
            freeHintsCount = 99,
            isTutorial = true,
            tutorialClue = "Press buttons in the sequence indicated by the clue.",
            mechanics = listOf(
                MechanicInstance(
                    type = MechanicType.BUTTON_SEQUENCE,
                    title = "Primary Bus Switch",
                    data = MechanicData.Sequence(
                        ButtonSequenceConfig(
                            buttons = listOf(
                                ButtonItem(1, "ALPHA", 0xFFE53935),
                                ButtonItem(2, "BETA", 0xFF1E88E5),
                                ButtonItem(3, "GAMMA", 0xFF43A047)
                            ),
                            correctSequence = listOf(1, 3, 2),
                            clueText = "Activate: ALPHA ➔ GAMMA ➔ BETA",
                            hintClarification = "Direct clue: Press Alpha, then Gamma, then Beta."
                        )
                    )
                )
            )
        ),
        // Level 4: Symbol Matching Tutorial
        LevelConfig(
            levelNumber = 4,
            chapterId = 1,
            levelInSector = 4,
            title = "Glyph Decryption",
            timeLimitSeconds = 90,
            freeHintsCount = 99,
            isTutorial = true,
            tutorialClue = "Tap the matching symbols in the order specified.",
            mechanics = listOf(
                MechanicInstance(
                    type = MechanicType.SYMBOL_MATCHING,
                    title = "Glyph Matrix",
                    data = MechanicData.Symbols(
                        SymbolMatchingConfig(
                            availableSymbols = listOf(
                                PuzzleSymbol.STAR,
                                PuzzleSymbol.BOLT,
                                PuzzleSymbol.SKULL,
                                PuzzleSymbol.FIRE
                            ),
                            targetPattern = listOf(PuzzleSymbol.STAR, PuzzleSymbol.BOLT, PuzzleSymbol.FIRE),
                            clueText = "Cipher order: Star ⭐ ➔ Bolt ⚡ ➔ Fire 🔥",
                            hintClarification = "Direct clue: Tap Star, then Bolt, then Fire."
                        )
                    )
                )
            )
        ),
        // Level 5: Speed Tapping / Pressure Gauge Tutorial
        LevelConfig(
            levelNumber = 5,
            chapterId = 1,
            levelInSector = 5,
            title = "Pressure Calibration",
            timeLimitSeconds = 90,
            freeHintsCount = 99,
            isTutorial = true,
            tutorialClue = "Tap STABILIZE when the oscillating needle is in the target green zone.",
            mechanics = listOf(
                MechanicInstance(
                    type = MechanicType.SPEED_TAPPING,
                    title = "Hydraulic Relief Valve",
                    data = MechanicData.Tapping(
                        SpeedTappingConfig(
                            targetMinPressure = 0.50f,
                            targetMaxPressure = 0.85f,
                            clueText = "Vent pressure when needle swings into the GREEN sector (50% - 85%).",
                            hintClarification = "Direct clue: Wait for needle to enter safe green arc and tap STABILIZE.",
                            dialIndirectClue = "NORMAL SAFE RANGE: 50-85 PSI",
                            tapsRequired = 3
                        )
                    )
                )
            )
        ),
        // Level 6: Radio Frequency Tuning Tutorial
        LevelConfig(
            levelNumber = 6,
            chapterId = 1,
            levelInSector = 6,
            title = "Frequency Intercept",
            timeLimitSeconds = 90,
            freeHintsCount = 99,
            isTutorial = true,
            tutorialClue = "Adjust the frequency slider to match the broadcast carrier wave.",
            mechanics = listOf(
                MechanicInstance(
                    type = MechanicType.FREQUENCY_TUNING,
                    title = "Carrier Receiver",
                    data = MechanicData.Frequency(
                        FrequencyTuningConfig(
                            targetFrequencyKHz = 142,
                            minFrequencyKHz = 100,
                            maxFrequencyKHz = 180,
                            toleranceKHz = 4,
                            clueText = "Calibrate radio dial to 142 kHz to lock resonance.",
                            hintClarification = "Direct clue: Move slider until dial reads 142 kHz, then press ENGAGE.",
                            stationCallsign = "TRAIN-BEACON"
                        )
                    )
                )
            )
        ),
        // Level 7: Battery Polarity Tutorial
        LevelConfig(
            levelNumber = 7,
            chapterId = 1,
            levelInSector = 7,
            title = "Polarity Shunt",
            timeLimitSeconds = 90,
            freeHintsCount = 99,
            isTutorial = true,
            tutorialClue = "Toggle each terminal to the correct positive (+) or negative (-) state.",
            mechanics = listOf(
                MechanicInstance(
                    type = MechanicType.POLARITY_MATCHING,
                    title = "Power Cell Gate",
                    data = MechanicData.Polarity(
                        PolarityMatchingConfig(
                            switches = listOf(
                                PolaritySwitch(1, "GATE-A", true),
                                PolaritySwitch(2, "GATE-B", false),
                                PolaritySwitch(3, "GATE-C", true)
                            ),
                            clueText = "GATE-A = Positive (+), GATE-B = Negative (−), GATE-C = Positive (+)",
                            hintClarification = "Direct clue: Set A to (+), B to (−), C to (+), then energize."
                        )
                    )
                )
            )
        ),
        // Level 8: Arithmetic Defusal Tutorial
        LevelConfig(
            levelNumber = 8,
            chapterId = 1,
            levelInSector = 8,
            title = "Capacitor Matrix",
            timeLimitSeconds = 90,
            freeHintsCount = 99,
            isTutorial = true,
            tutorialClue = "Solve the balance equation and select the matching shunt value.",
            mechanics = listOf(
                MechanicInstance(
                    type = MechanicType.ARITHMETIC_PUZZLE,
                    title = "Capacitor Discharge",
                    data = MechanicData.Arithmetic(
                        ArithmeticPuzzleConfig(
                            equationDisplay = "15 + [ ? ] = 38",
                            options = listOf(18, 23, 27, 33),
                            correctIndex = 1, // 23
                            clueText = "Neutralize capacitor: 15 + [ ? ] = 38. Choose 23.",
                            hintClarification = "Direct clue: 38 minus 15 is 23. Tap shunt 23."
                        )
                    )
                )
            )
        ),
        // Level 9: Rotary Dial / Safe Tumbler Tutorial
        LevelConfig(
            levelNumber = 9,
            chapterId = 1,
            levelInSector = 9,
            title = "Rotary Tumbler",
            timeLimitSeconds = 90,
            freeHintsCount = 99,
            isTutorial = true,
            tutorialClue = "Adjust the rotary dial to align with the target calibration value.",
            mechanics = listOf(
                MechanicInstance(
                    type = MechanicType.ROTARY_DIAL,
                    title = "Safe Tumbler Lock",
                    data = MechanicData.Rotary(
                        RotaryDialConfig(
                            targetValue = 64,
                            clueText = "Align tumbler dial to calibration angle 64.",
                            hintClarification = "Direct clue: Use buttons or slider to set dial to 64, then engage lock."
                        )
                    )
                )
            )
        ),
        // Level 10: Circuit Fuses Tutorial
        LevelConfig(
            levelNumber = 10,
            chapterId = 1,
            levelInSector = 10,
            title = "Fuse Array Breaker",
            timeLimitSeconds = 90,
            freeHintsCount = 99,
            isTutorial = true,
            tutorialClue = "Toggle the correct combination of fuses to balance the target amperage.",
            mechanics = listOf(
                MechanicInstance(
                    type = MechanicType.CIRCUIT_FUSES,
                    title = "Surge Fuse Matrix",
                    data = MechanicData.Fuses(
                        CircuitFusesConfig(
                            fuses = listOf(
                                FuseItem(1, "RELAY-1", 5, 0xFF00E5FF, true),
                                FuseItem(2, "RELAY-2", 10, 0xFFFFD600, false),
                                FuseItem(3, "RELAY-3", 15, 0xFF00E676, true),
                                FuseItem(4, "RELAY-4", 20, 0xFFFF6D00, false),
                                FuseItem(5, "RELAY-5", 30, 0xFFFF2A85, false)
                            ),
                            targetAmperage = 20, // 5 + 15 = 20
                            clueText = "Route exactly 20A through the breaker without blowing fuses.",
                            hintClarification = "Direct clue: Toggle 5A (RELAY-1) and 15A (RELAY-3) to make 20A, then engage breaker."
                        )
                    )
                )
            )
        )
    )

    /**
     * Calculates the difficulty profile for any given sector and level.
     */
    fun calculateDifficultyProfile(sector: Int, levelInSector: Int): DifficultyProfile {
        if (sector == 1) {
            return DifficultyProfile(
                sector = 1,
                levelInSector = levelInSector,
                mechanicsCount = 1,
                timeLimitSeconds = 90,
                clueTrickinessTier = 1,
                decoyDensity = 0.0f,
                freeHintsCount = 99
            )
        }

        // Mechanics per bomb scales gracefully:
        // Sector 2-3: 1 mechanic
        // Sector 4-7: 2 mechanics
        // Sector 8-11: 3 mechanics
        // Sector 12-14: 4 mechanics
        // Sector 15+: 5 mechanics (capped)
        val mechanicsCount = when {
            sector in 2..3 -> 1
            sector in 4..7 -> 2
            sector in 8..11 -> 3
            sector in 12..14 -> 4
            else -> 5
        }

        // Base time per mechanic: starts ~68s in early sector 2, decreases with diminishing returns, floor 18s
        val baseTimePerMech = (68.0 - (sector * 2.2) - (levelInSector * 0.4)).coerceIn(18.0, 75.0)
        // For multiple mechanics, total time is scaled with synergy discount
        val totalTime = (baseTimePerMech * (1.0 + (mechanicsCount - 1) * 0.55)).toInt().coerceIn(18, 90)

        // Clue trickiness tier increases every 2-3 sectors:
        // Sector 2-3: Tier 1
        // Sector 4-6: Tier 2
        // Sector 7-9: Tier 3
        // Sector 10+: Tier 4
        val trickinessTier = when {
            sector in 2..3 -> 1
            sector in 4..6 -> 2
            sector in 7..9 -> 3
            else -> 4
        }

        // Decoy density scales from 10% up to 45%
        val decoyDensity = (0.10f + (sector * 0.03f) + (levelInSector * 0.01f)).coerceIn(0.10f, 0.45f)

        return DifficultyProfile(
            sector = sector,
            levelInSector = levelInSector,
            mechanicsCount = mechanicsCount,
            timeLimitSeconds = totalTime,
            clueTrickinessTier = trickinessTier,
            decoyDensity = decoyDensity,
            freeHintsCount = 1
        )
    }

    /**
     * Primary retrieval method for any level 1..Infinity.
     * Guaranteed deterministic and non-crashing.
     */
    fun getLevel(levelNumber: Int): LevelConfig {
        val safeLevel = levelNumber.coerceAtLeast(1)
        val (sector, levelInSector) = SectorNavigationUtils.getSectorAndLevel(safeLevel)

        if (sector == 1 && levelInSector in 1..SectorNavigationUtils.TUTORIAL_SECTOR_LEVELS) {
            return SECTOR_1_TUTORIALS[levelInSector - 1]
        }

        return generateProceduralLevel(safeLevel, sector, levelInSector)
    }

    /**
     * Procedurally generates a level for Sector 2+ using deterministic seeding and the Logic Clue Engine.
     */
    private fun generateProceduralLevel(
        globalLevel: Int,
        sector: Int,
        levelInSector: Int
    ): LevelConfig {
        // Seed deterministically based on sector and level
        val seed = (sector.toLong() * 10007L) + (levelInSector.toLong() * 137L)
        val rng = Random(seed)

        val profile = calculateDifficultyProfile(sector, levelInSector)

        // Select distinct mechanics weighted by variety
        val chosenMechanicTypes = pickMechanicsForLevel(profile.mechanicsCount, sector, levelInSector, rng)

        val mechanics = chosenMechanicTypes.mapIndexed { index, mechType ->
            generateMechanicInstance(mechType, index, profile.clueTrickinessTier, profile.decoyDensity, rng)
        }

        val levelTitles = listOf(
            "Omega Contingency", "Resonance Lock", "Thermal Surge", "Pulse Decryption",
            "Logic Fracture", "Quantum Equilibrium", "Static Overload", "Cipher Breach"
        )
        val title = levelTitles[rng.nextInt(levelTitles.size)]

        return LevelConfig(
            levelNumber = globalLevel,
            chapterId = sector,
            levelInSector = levelInSector,
            title = title,
            timeLimitSeconds = profile.timeLimitSeconds,
            mechanics = mechanics,
            freeHintsCount = profile.freeHintsCount,
            isTutorial = false,
            tutorialClue = null
        )
    }

    /**
     * Picks mechanic types ensuring good variety and preventing repeating the same 1-2 back-to-back.
     */
    private fun pickMechanicsForLevel(
        count: Int,
        sector: Int,
        levelInSector: Int,
        rng: Random
    ): List<MechanicType> {
        val allTypes = MechanicType.values().toList()

        // Deterministically calculate which mechanics were used in the previous stage
        val prevSeed = (sector.toLong() * 10007L) + ((levelInSector - 1).toLong() * 137L)
        val prevRng = Random(prevSeed)
        val prevMechanics = allTypes.shuffled(prevRng).take(count).toSet()

        // Prioritize mechanics not in previous level
        val freshMechanics = allTypes.filterNot { prevMechanics.contains(it) }.shuffled(rng)
        val fallbackMechanics = prevMechanics.shuffled(rng)

        val selection = (freshMechanics + fallbackMechanics).take(count)
        return selection
    }

    /**
     * Builds a single MechanicInstance with procedural data and deductive clues.
     */
    private fun generateMechanicInstance(
        type: MechanicType,
        index: Int,
        tier: Int,
        decoyDensity: Float,
        rng: Random
    ): MechanicInstance {
        return when (type) {
            MechanicType.WIRE_CUTTING -> {
                val wireCount = if (tier >= 3) 5 else if (tier == 2) 4 else 3
                val shuffledColors = WIRE_COLORS.shuffled(rng).take(wireCount)
                val stripedIndex = if (rng.nextFloat() < decoyDensity) rng.nextInt(wireCount) else -1

                val wires = shuffledColors.mapIndexed { idx, (hex, name) ->
                    WireItem(
                        id = idx + 1,
                        colorHex = hex,
                        colorName = name,
                        isStriped = (idx == stripedIndex)
                    )
                }
                val correctIndex = rng.nextInt(wireCount)
                val (clueText, hint) = LogicClueEngine.generateWireClue(wires, correctIndex, tier, false, rng)

                MechanicInstance(
                    type = type,
                    title = "Wire Rack ${index + 1}",
                    data = MechanicData.Wires(
                        WireCuttingConfig(
                            wires = wires,
                            correctWireIndex = correctIndex,
                            clueText = clueText,
                            hintClarification = hint
                        )
                    )
                )
            }

            MechanicType.CODE_ENTRY -> {
                val code = (1000 + rng.nextInt(9000)).toString()
                val (clueText, hint, scattered) = LogicClueEngine.generateCodeClue(code, tier, false, rng)

                MechanicInstance(
                    type = type,
                    title = "Security Keypad ${index + 1}",
                    data = MechanicData.Code(
                        CodeEntryConfig(
                            targetCode = code,
                            clueText = clueText,
                            hintClarification = hint,
                            scatteredClues = scattered
                        )
                    )
                )
            }

            MechanicType.BUTTON_SEQUENCE -> {
                val btnCount = if (tier >= 3) 4 else 3
                val buttons = BUTTON_PALETTE.shuffled(rng).take(btnCount).map {
                    ButtonItem(it.first, it.second, it.third)
                }
                val seqLength = btnCount
                val sequence = buttons.map { it.id }.shuffled(rng).take(seqLength)
                val (clueText, hint, isReversed) = LogicClueEngine.generateSequenceClue(buttons, sequence, tier, false, rng)

                MechanicInstance(
                    type = type,
                    title = "Relay Switch ${index + 1}",
                    data = MechanicData.Sequence(
                        ButtonSequenceConfig(
                            buttons = buttons,
                            correctSequence = sequence,
                            clueText = clueText,
                            hintClarification = hint,
                            isReversed = isReversed
                        )
                    )
                )
            }

            MechanicType.SYMBOL_MATCHING -> {
                val allSymbols = PuzzleSymbol.values().toList()
                val available = allSymbols.shuffled(rng).take(6)
                val targetLength = if (tier >= 3) 4 else 3
                val target = available.shuffled(rng).take(targetLength)
                val (clueText, hint, legend) = LogicClueEngine.generateSymbolClue(target, available, tier, false, rng)

                MechanicInstance(
                    type = type,
                    title = "Symbol Matrix ${index + 1}",
                    data = MechanicData.Symbols(
                        SymbolMatchingConfig(
                            availableSymbols = available,
                            targetPattern = target,
                            clueText = clueText,
                            hintClarification = hint,
                            cipherLegend = legend
                        )
                    )
                )
            }

            MechanicType.SPEED_TAPPING -> {
                val minP = 0.55f + (rng.nextFloat() * 0.15f)
                val maxP = (minP + 0.22f).coerceAtMost(0.95f)
                val (clueText, hint, dialClue) = LogicClueEngine.generateTappingClue(minP, maxP, tier, false, rng)

                MechanicInstance(
                    type = type,
                    title = "Pressure Stabilizer ${index + 1}",
                    data = MechanicData.Tapping(
                        SpeedTappingConfig(
                            targetMinPressure = minP,
                            targetMaxPressure = maxP,
                            clueText = clueText,
                            hintClarification = hint,
                            dialIndirectClue = dialClue,
                            tapsRequired = if (tier >= 3) 4 else 3
                        )
                    )
                )
            }

            MechanicType.FREQUENCY_TUNING -> {
                val target = rng.nextInt(95, 190)
                val (clueText, hint) = LogicClueEngine.generateFrequencyClue(target, 80, 200, tier, false, rng)

                MechanicInstance(
                    type = type,
                    title = "Radio Tuner ${index + 1}",
                    data = MechanicData.Frequency(
                        FrequencyTuningConfig(
                            targetFrequencyKHz = target,
                            minFrequencyKHz = 80,
                            maxFrequencyKHz = 200,
                            toleranceKHz = if (tier >= 3) 2 else 4,
                            clueText = clueText,
                            hintClarification = hint,
                            stationCallsign = "FREQ-SEC-${rng.nextInt(10, 99)}"
                        )
                    )
                )
            }

            MechanicType.POLARITY_MATCHING -> {
                val count = if (tier >= 3) 4 else 3
                val labels = listOf("GATE-A", "GATE-B", "GATE-C", "GATE-D")
                val switches = (0 until count).map {
                    PolaritySwitch(
                        id = it + 1,
                        label = labels[it],
                        correctPositive = rng.nextBoolean()
                    )
                }
                val (clueText, hint) = LogicClueEngine.generatePolarityClue(switches, tier, false, rng)

                MechanicInstance(
                    type = type,
                    title = "Polarity Shunt ${index + 1}",
                    data = MechanicData.Polarity(
                        PolarityMatchingConfig(
                            switches = switches,
                            clueText = clueText,
                            hintClarification = hint
                        )
                    )
                )
            }

            MechanicType.ARITHMETIC_PUZZLE -> {
                val a = rng.nextInt(12, 45)
                val b = rng.nextInt(8, 30)
                val isAddition = rng.nextBoolean()
                val result = if (isAddition) a + b else a - b
                val eqDisplay = if (isAddition) "$a + [ ? ] = $result" else "$a − [ ? ] = $result"
                val answer = b

                val options = mutableSetOf(answer)
                while (options.size < 4) {
                    val delta = rng.nextInt(-10, 11)
                    val candidate = answer + delta
                    if (candidate > 0 && candidate != answer) {
                        options.add(candidate)
                    }
                }
                val optionsList = options.toList().shuffled(rng)
                val correctIndex = optionsList.indexOf(answer)
                val (clueText, hint) = LogicClueEngine.generateArithmeticClue(eqDisplay, answer, tier, false, rng)

                MechanicInstance(
                    type = type,
                    title = "Capacitor Discharge ${index + 1}",
                    data = MechanicData.Arithmetic(
                        ArithmeticPuzzleConfig(
                            equationDisplay = eqDisplay,
                            options = optionsList,
                            correctIndex = correctIndex,
                            clueText = clueText,
                            hintClarification = hint
                        )
                    )
                )
            }

            MechanicType.ROTARY_DIAL -> {
                val targetValue = rng.nextInt(12, 88)
                val tolerance = if (tier >= 3) 0 else 1
                val clue = when (tier) {
                    1 -> "Align safe tumbler dial to calibration angle $targetValue."
                    2 -> "Tumbler angle: double ${targetValue / 2} plus ${targetValue % 2}."
                    else -> "Tumbler offset: target angle is $targetValue. Maintain high accuracy."
                }
                val hint = "Direct clue: Dial tumbler to $targetValue and lock."

                MechanicInstance(
                    type = type,
                    title = "Safe Tumbler ${index + 1}",
                    data = MechanicData.Rotary(
                        RotaryDialConfig(
                            targetValue = targetValue,
                            tolerance = tolerance,
                            clueText = clue,
                            hintClarification = hint
                        )
                    )
                )
            }

            MechanicType.CIRCUIT_FUSES -> {
                val ampOptions = listOf(5, 10, 15, 20, 25, 30)
                val fuseLabels = listOf("AUX-1", "RELAY-2", "HV-BUS", "GRID-A", "SHUNT-B")
                val fuseColors = listOf(0xFF00E5FF, 0xFFFFD600, 0xFF00E676, 0xFFFF6D00, 0xFFFF2A85)

                val selectedAmps = ampOptions.shuffled(rng).take(5)
                val chosenIndices = listOf(0, 1, 2, 3, 4).shuffled(rng).take(rng.nextInt(2, 4))
                val targetAmps = chosenIndices.sumOf { selectedAmps[it] }

                val fuses = selectedAmps.mapIndexed { idx, amp ->
                    FuseItem(
                        id = idx + 1,
                        label = fuseLabels[idx],
                        amperage = amp,
                        colorHex = fuseColors[idx],
                        isRequired = chosenIndices.contains(idx)
                    )
                }

                val clue = "Surge threshold: Route exactly ${targetAmps}A through breaker without overload."
                val hint = "Direct clue: Toggle fuses summing to ${targetAmps}A then engage breaker."

                MechanicInstance(
                    type = type,
                    title = "Fuse Array ${index + 1}",
                    data = MechanicData.Fuses(
                        CircuitFusesConfig(
                            fuses = fuses,
                            targetAmperage = targetAmps,
                            clueText = clue,
                            hintClarification = hint
                        )
                    )
                )
            }
        }
    }

    /**
     * Returns rich narrative and theming details for any sector index (1..Infinity).
     */
    fun getSectorInfo(sector: Int): ChapterConfig {
        return when (sector) {
            1 -> ChapterConfig(1, "TRAINING FACILITY", "Operative Certification", 0xFF8E24AA)
            2 -> ChapterConfig(2, "SUB-BASE EPSILON", "Cold War Bunker Relay", 0xFF1E88E5)
            3 -> ChapterConfig(3, "NEURAL DEPTHS", "Cybernetic Firewall Terminal", 0xFF00ACC1)
            4 -> ChapterConfig(4, "ORBITAL SILO", "Low-Gravity Warhead Bay", 0xFFFB8C00)
            5 -> ChapterConfig(5, "ABYSSAL PLATFORM", "Deep-Sea Geothermal Rig", 0xFF43A047)
            6 -> ChapterConfig(6, "VOLCANIC CORE", "Magma Conduit Station", 0xFFE53935)
            7 -> ChapterConfig(7, "QUANTUM VAULT", "Temporal Containment Unit", 0xFFD81B60)
            8 -> ChapterConfig(8, "ION SPHERE", "Atmospheric Array Complex", 0xFF5E35B1)
            9 -> ChapterConfig(9, "CIPHER CITADEL", "Cryptographic Defense Grid", 0xFF00897B)
            10 -> ChapterConfig(10, "SUPERNOVA FOUNDRY", "Plasma Fusion Laboratory", 0xFFF4511E)
            else -> {
                val sectorThemes = listOf(
                    Pair("APEX MATRIX", "Classified Defense Installation"),
                    Pair("OBLIVION NODE", "Extraterrestrial Data Core"),
                    Pair("VORTEX SHIELD", "High-Energy Perimeter Array"),
                    Pair("TITANIC SILO", "Subterranean Reactor Core"),
                    Pair("CHRONOS CORE", "Singularity Research Terminal")
                )
                val theme = sectorThemes[(sector - 11) % sectorThemes.size]
                val colors = listOf(0xFF8E24AA, 0xFF1E88E5, 0xFFE53935, 0xFF43A047, 0xFFFB8C00)
                ChapterConfig(
                    id = sector,
                    name = "SECTOR $sector: ${theme.first}",
                    subtitle = theme.second,
                    themeColorHex = colors[(sector - 11) % colors.size]
                )
            }
        }
    }
}
