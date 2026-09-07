package com.example.game

import com.example.data.models.ButtonItem
import com.example.data.models.PolaritySwitch
import com.example.data.models.PuzzleSymbol
import com.example.data.models.WireItem
import kotlin.random.Random

object LogicClueEngine {

    // -------------------------------------------------------------
    // 1. WIRE CUTTING CLUES
    // -------------------------------------------------------------
    fun generateWireClue(
        wires: List<WireItem>,
        correctIndex: Int,
        tier: Int,
        isTutorial: Boolean,
        rng: Random
    ): Pair<String, String> {
        val targetWire = wires[correctIndex]
        val targetColor = targetWire.colorName

        if (isTutorial) {
            return Pair(
                "DIRECT INSTRUCTION: Cut the ${targetColor.uppercase()} wire at position #${correctIndex + 1}.",
                "Direct clue: Tap the ${targetColor.uppercase()} wire at slot #${correctIndex + 1}."
            )
        }

        val colorsPresent = wires.map { it.colorName }.distinct()
        val hasRed = wires.any { it.colorName.equals("Red", ignoreCase = true) }
        val hasBlue = wires.any { it.colorName.equals("Blue", ignoreCase = true) }
        val hasGreen = wires.any { it.colorName.equals("Green", ignoreCase = true) }
        val redCount = wires.count { it.colorName.equals("Red", ignoreCase = true) }
        val blueCount = wires.count { it.colorName.equals("Blue", ignoreCase = true) }

        return when (tier) {
            1 -> {
                // Tier 1: Single condition rule
                if (hasRed && targetColor.equals("Blue", ignoreCase = true)) {
                    Pair(
                        "If a RED wire is present, cut the BLUE wire. Otherwise, cut the FIRST wire.",
                        "Analysis: Check if Red is installed on the rack to satisfy the primary clause."
                    )
                } else if (hasBlue && targetColor.equals("Green", ignoreCase = true)) {
                    Pair(
                        "If a BLUE wire is present, cut GREEN. Otherwise, cut YELLOW.",
                        "Analysis: Inspect the rack for Blue conductivity to determine Green vs Yellow."
                    )
                } else {
                    val posWord = when (correctIndex) {
                        0 -> "first"
                        wires.size - 1 -> "last"
                        else -> "slot #${correctIndex + 1}"
                    }
                    Pair(
                        "If rack has ${wires.size} conductors, cut the wire in the $posWord position.",
                        "Analysis: Count total conductors on the panel ($posWord)."
                    )
                }
            }
            2 -> {
                // Tier 2: Conditional branches based on counts or striped state
                val stripedIndex = wires.indexOfFirst { it.isStriped }
                if (stripedIndex != -1) {
                    val adjacentIndex = if (stripedIndex > 0) stripedIndex - 1 else stripedIndex + 1
                    if (adjacentIndex == correctIndex) {
                        Pair(
                            "Cut the conductor directly adjacent to the STRIPED wire.",
                            "Analysis: Locate the wire with hazard stripes and cut its neighbor."
                        )
                    } else {
                        Pair(
                            "If any wire is STRIPED, cut the ${targetColor.uppercase()} wire. Otherwise cut wire #1.",
                            "Analysis: Confirm striped insulation on rack to validate the ${targetColor} cut."
                        )
                    }
                } else if (blueCount >= 2) {
                    Pair(
                        "If Blue count is 2 or more, cut the ${targetColor.uppercase()} wire. Otherwise cut wire #2.",
                        "Analysis: Tally the Blue wires to determine which branch applies."
                    )
                } else {
                    Pair(
                        "If wire #1 is NOT ${wires.first().colorName.uppercase()}, cut #1. Otherwise cut the ${targetColor.uppercase()} wire.",
                        "Analysis: Invert the first condition: evaluate wire #1 color carefully."
                    )
                }
            }
            3 -> {
                // Tier 3: Deductive multi-condition chain
                val otherColor = colorsPresent.firstOrNull { it != targetColor } ?: "White"
                Pair(
                    "RULE 1: If Red count ($redCount) exceeds Blue count ($blueCount), cut $targetColor.\n" +
                    "RULE 2: Else if total wires == ${wires.size}, cut the conductor that is NOT $otherColor.",
                    "Analysis: Compare Red and Blue counts first; if tied, rule 2 designates the target."
                )
            }
            else -> {
                // Tier 4: Multi-clause logic gate deduction
                val lastWireColor = wires.last().colorName
                Pair(
                    "PROTOCOL ALPHA: Disarm safe fuse.\n" +
                    "1. If wire #1 matches wire #${wires.size}, cut center wire.\n" +
                    "2. Else if $targetColor wire is installed, cut $targetColor.\n" +
                    "3. Otherwise cut $lastWireColor.",
                    "Analysis: Check rule 1 symmetry. If false, proceed to rule 2 for safe conductor."
                )
            }
        }
    }

    // -------------------------------------------------------------
    // 2. KEYPAD CODE CLUES
    // -------------------------------------------------------------
    fun generateCodeClue(
        targetCode: String,
        tier: Int,
        isTutorial: Boolean,
        rng: Random
    ): Triple<String, String, List<String>> {
        if (isTutorial) {
            return Triple(
                "DIRECT PASSCODE: Enter '$targetCode' on numeric keypad.",
                "Direct clue: Enter digits $targetCode in order.",
                listOf("SERIAL: #9900", "PASSCODE: $targetCode")
            )
        }

        return when (tier) {
            1 -> {
                val codeInt = targetCode.toIntOrNull() ?: 1234
                val offset = rng.nextInt(5, 30)
                val base = codeInt - offset
                Triple(
                    "SECURITY ALGORITHM: Code = Chassis Value ($base) + Frequency Offset ($offset).",
                    "Analysis: Add $offset to the chassis base reading $base.",
                    listOf("CHASSIS BASE: $base", "FREQ DELTA: +$offset", "REG: 04")
                )
            }
            2 -> {
                val d1 = targetCode.take(2)
                val d2 = targetCode.takeLast(2)
                val v1 = d1.toIntOrNull() ?: 24
                val v2 = d2.toIntOrNull() ?: 18
                Triple(
                    "TWO-STAGE RELAY:\nPrefix = Valve A ($v1). Suffix = Valve B ($v2).",
                    "Analysis: Combine Prefix $d1 and Suffix $d2 into a 4-digit sequence.",
                    listOf("VALVE A: $v1 PSI", "VALVE B: $v2 PSI", "GRID: SEC-02")
                )
            }
            3 -> {
                // Arithmetic split: (A * B) + (C - D)
                val firstTwo = targetCode.take(2).toIntOrNull() ?: 45
                val lastTwo = targetCode.takeLast(2).toIntOrNull() ?: 89
                val factor1 = 5
                val factor2 = firstTwo / factor1
                val remainder = firstTwo % factor1
                Triple(
                    "SCATTERED INTELLIGENCE:\n• Digits 1-2: ($factor1 × $factor2 + $remainder)\n• Digits 3-4: Measured Suffix from Relay Plate B ($lastTwo).",
                    "Analysis: Solve the arithmetic expression ($factor1 × $factor2 + $remainder) for the first half, then append $lastTwo.",
                    listOf("RELAY PLATE B: #$lastTwo", "MODULUS: 0x$firstTwo", "VOLT: 12V")
                )
            }
            else -> {
                // Tier 4: Cipher mapping
                val digits = targetCode.map { it.digitToIntOrNull() ?: 1 }
                val letters = listOf("A", "B", "C", "D", "E", "F", "G", "H", "J", "K")
                val cipherWord = digits.map { letters[it % letters.size] }.joinToString("-")
                val legend = letters.mapIndexed { idx, s -> "$s=$idx" }.take(6).joinToString(", ")
                Triple(
                    "CRYPTOGRAPHIC CIPHER: Enter cipher key '$cipherWord'.\nDecryption index: $legend...",
                    "Analysis: Map each letter in '$cipherWord' to its single numeric digit value.",
                    listOf("CIPHER KEY: $cipherWord", "DECODER: $legend", "CORE: ACTIVE")
                )
            }
        }
    }

    // -------------------------------------------------------------
    // 3. BUTTON SEQUENCE CLUES
    // -------------------------------------------------------------
    fun generateSequenceClue(
        buttons: List<ButtonItem>,
        correctSequence: List<Int>,
        tier: Int,
        isTutorial: Boolean,
        rng: Random
    ): Triple<String, String, Boolean> {
        val buttonMap = buttons.associateBy { it.id }
        val namesInOrder = correctSequence.mapNotNull { buttonMap[it]?.label }

        if (isTutorial) {
            val direct = namesInOrder.joinToString(" ➔ ")
            return Triple(
                "DIRECT INSTRUCTION: Tap switches in order: $direct.",
                "Direct clue: Press the buttons sequentially: $direct.",
                false
            )
        }

        return when (tier) {
            1 -> {
                val direct = namesInOrder.joinToString(" ➔ ")
                Triple(
                    "RELAY BUS: Activate terminals: $direct.",
                    "Analysis: Follow the forward order: ${namesInOrder.first()} first.",
                    false
                )
            }
            2 -> {
                // Reversed order requirement
                val reversedNames = namesInOrder.reversed().joinToString(" ➔ ")
                Triple(
                    "INVERTED LOGIC BUS:\nCommand sequence: $reversedNames\nPROTOCOL: Input in REVERSE order!",
                    "Analysis: Sequence must be entered backwards from the listed command.",
                    true
                )
            }
            3 -> {
                // Conditional shift or reverse
                val reversedNames = namesInOrder.reversed().joinToString(" ➔ ")
                Triple(
                    "POLARITY INVERSION:\nTerminal stream: $reversedNames\nNote: Ground circuit demands strict REVERSE execution.",
                    "Analysis: Execute from right-to-left: first press is ${namesInOrder.first()}.",
                    true
                )
            }
            else -> {
                // Tier 4: Cryptic priority
                val direct = namesInOrder.joinToString(" ➔ ")
                Triple(
                    "TACTICAL OVERRIDE SEQUENCE:\nEngage relays by ascending clearance: $direct\n(Decoy pulses active; follow labeled bus only).",
                    "Analysis: Follow designated primary bus labels: $direct ignoring decoys.",
                    false
                )
            }
        }
    }

    // -------------------------------------------------------------
    // 4. SYMBOL MATCHING CLUES
    // -------------------------------------------------------------
    fun generateSymbolClue(
        targetPattern: List<PuzzleSymbol>,
        availableSymbols: List<PuzzleSymbol>,
        tier: Int,
        isTutorial: Boolean,
        rng: Random
    ): Triple<String, String, Map<String, String>> {
        val directNames = targetPattern.joinToString(" ➔ ") { "${it.glyph} (${it.displayName})" }

        if (isTutorial) {
            return Triple(
                "DIRECT INSTRUCTION: Input symbols: $directNames.",
                "Direct clue: Tap glyphs in exact order: $directNames.",
                emptyMap()
            )
        }

        val codenames = mapOf(
            PuzzleSymbol.SKULL to "MORTIS",
            PuzzleSymbol.STAR to "NOVA",
            PuzzleSymbol.BOLT to "VOLT",
            PuzzleSymbol.FIRE to "PYRO",
            PuzzleSymbol.HEART to "VITA",
            PuzzleSymbol.GEAR to "MACHINA",
            PuzzleSymbol.KEY to "CIPHER",
            PuzzleSymbol.DIAMOND to "ADAMANT"
        )

        return when (tier) {
            1 -> {
                val clueStr = targetPattern.joinToString(" ➔ ") { it.displayName }
                Triple(
                    "TRANSMISSION GLYPHS: Sequence = $clueStr.",
                    "Analysis: Match the sequence of symbol names: ${targetPattern.first().displayName} first.",
                    emptyMap()
                )
            }
            2 -> {
                val codeStr = targetPattern.joinToString(" ➔ ") { codenames[it] ?: it.displayName }
                val legend = targetPattern.distinct().associate { (codenames[it] ?: it.displayName) to "${it.glyph} ${it.displayName}" }
                Triple(
                    "ENCRYPTED GLYPH CHANNEL:\nKey: $codeStr.",
                    "Analysis: Check codenames: ${codenames[targetPattern.first()]} maps to ${targetPattern.first().displayName}.",
                    legend
                )
            }
            else -> {
                val codeStr = targetPattern.joinToString(" ➔ ") { codenames[it] ?: it.displayName }
                val legend = availableSymbols.associate { (codenames[it] ?: it.displayName) to "${it.glyph} ${it.displayName}" }
                Triple(
                    "ANCIENT ENCRYPTION VECTOR:\nSignal order: $codeStr.\n(Cross-reference symbol legend below).",
                    "Analysis: Decode the codenames using the reference table: start with ${targetPattern.first().displayName}.",
                    legend
                )
            }
        }
    }

    // -------------------------------------------------------------
    // 5. SPEED TAPPING / PRESSURE GAUGE CLUES
    // -------------------------------------------------------------
    fun generateTappingClue(
        minPressure: Float,
        maxPressure: Float,
        tier: Int,
        isTutorial: Boolean,
        rng: Random
    ): Triple<String, String, String> {
        val minPct = (minPressure * 100).toInt()
        val maxPct = (maxPressure * 100).toInt()

        if (isTutorial) {
            return Triple(
                "DIRECT INSTRUCTION: Tap STABILIZE when needle enters the highlighted GREEN zone ($minPct% - $maxPct%).",
                "Direct clue: Wait for the pressure needle to enter $minPct% - $maxPct% and tap quickly.",
                "SAFE RANGE: $minPct% - $maxPct%"
            )
        }

        return when (tier) {
            1 -> {
                Triple(
                    "PRESSURE HYDRAULICS: Vent chamber when gauge is between $minPct% and $maxPct%.",
                    "Analysis: Tap the relief button as the needle swings into the $minPct% - $maxPct% band.",
                    "OPERATING LIMIT: $minPct - $maxPct PSI"
                )
            }
            2 -> {
                val center = (minPct + maxPct) / 2
                Triple(
                    "CALIBRATION DIAL: Target harmonic pressure is approximately $center PSI (±12 PSI).",
                    "Analysis: Safe zone is centered around $center PSI ($minPct% to $maxPct%).",
                    "INDEX #4: TARGET $center PSI"
                )
            }
            else -> {
                val offset = 100 - maxPct
                Triple(
                    "INDIRECT RELIEF GAUGE:\nSafe threshold = (Chamber Max 100 - Vent Delta $offset) ± 10.",
                    "Analysis: Calculate $minPct% to $maxPct% from the formula and tap during peak oscillation.",
                    "VALVE RATIO: 100 - $offset"
                )
            }
        }
    }

    // -------------------------------------------------------------
    // 6. FREQUENCY TUNING CLUES
    // -------------------------------------------------------------
    fun generateFrequencyClue(
        targetFreq: Int,
        minFreq: Int,
        maxFreq: Int,
        tier: Int,
        isTutorial: Boolean,
        rng: Random
    ): Pair<String, String> {
        if (isTutorial) {
            return Pair(
                "DIRECT INSTRUCTION: Drag slider to tune receiver exactly to $targetFreq kHz.",
                "Direct clue: Move frequency slider until dial displays $targetFreq kHz."
            )
        }

        return when (tier) {
            1 -> {
                val delta = rng.nextInt(4, 15)
                val base = targetFreq - delta
                Pair(
                    "RADIO HARMONIC: Broadcast frequency = Base ($base kHz) + Carrier ($delta kHz).",
                    "Analysis: Add $delta to $base to find target tuning (~$targetFreq kHz)."
                )
            }
            2 -> {
                val half = targetFreq / 2
                val rem = targetFreq % 2
                Pair(
                    "RESONANT BEACON: Carrier modulation equals 2 × ($half) + $rem kHz.",
                    "Analysis: Compute (2 × $half + $rem) to align the radio receiver dial."
                )
            }
            else -> {
                val offset = rng.nextInt(20, 50)
                val sum = targetFreq + offset
                Pair(
                    "CRYPTOGRAPHIC FREQ:\nChannel balance = ($sum kHz - Satellite Offset $offset kHz).",
                    "Analysis: Target frequency is roughly $sum minus $offset ($targetFreq kHz)."
                )
            }
        }
    }

    // -------------------------------------------------------------
    // 7. BATTERY / POLARITY MATCHING CLUES
    // -------------------------------------------------------------
    fun generatePolarityClue(
        switches: List<PolaritySwitch>,
        tier: Int,
        isTutorial: Boolean,
        rng: Random
    ): Pair<String, String> {
        val states = switches.joinToString(", ") { "${it.label}=${if (it.correctPositive) "+" else "-"}" }

        if (isTutorial) {
            return Pair(
                "DIRECT INSTRUCTION: Set polarity terminals: $states.",
                "Direct clue: Toggle switches to match $states."
            )
        }

        return when (tier) {
            1 -> {
                Pair(
                    "TERMINAL POLARITY: Balance power gates: $states.",
                    "Analysis: Configure each switch position to match indicated charge ($states)."
                )
            }
            2 -> {
                val posCount = switches.count { it.correctPositive }
                val negCount = switches.size - posCount
                Pair(
                    "CIRCUIT EQUILIBRIUM: Grid demands $posCount POSITIVE (+) and $negCount NEGATIVE (-) cells.\nFirst gate (${switches.first().label}) must be ${if (switches.first().correctPositive) "POSITIVE (+)" else "NEGATIVE (-)"}.",
                    "Analysis: Note gate 1 is ${if (switches.first().correctPositive) "+" else "-"} and total positive terminals = $posCount."
                )
            }
            else -> {
                val rule = if (switches.all { it.correctPositive }) {
                    "All circuits must be set to POSITIVE (+)."
                } else if (switches.none { it.correctPositive }) {
                    "All circuits must be set to NEGATIVE (-)."
                } else {
                    "Alternate polarity between adjacent nodes starting with ${switches.first().label} as ${if (switches.first().correctPositive) "(+)" else "(-)"}."
                }
                Pair(
                    "LOGIC GATE FLOW:\n$rule",
                    "Analysis: Observe the alternating polarity rule beginning at the primary gate."
                )
            }
        }
    }

    // -------------------------------------------------------------
    // 8. ARITHMETIC PUZZLE CLUES
    // -------------------------------------------------------------
    fun generateArithmeticClue(
        equation: String,
        answer: Int,
        tier: Int,
        isTutorial: Boolean,
        rng: Random
    ): Pair<String, String> {
        if (isTutorial) {
            return Pair(
                "DIRECT INSTRUCTION: Balance equation '$equation'. Correct answer is $answer.",
                "Direct clue: Select the node with value $answer."
            )
        }

        return when (tier) {
            1 -> {
                Pair(
                    "CAPACITOR MATRIX: Solve balance formula: $equation.",
                    "Analysis: Compute the missing integer that satisfies $equation."
                )
            }
            2 -> {
                Pair(
                    "SHUNT DISCHARGE EQUATION:\nEvaluate: $equation to neutralize residual voltage.",
                    "Analysis: Work backwards from the result to deduce the missing term."
                )
            }
            else -> {
                Pair(
                    "QUANTUM CORE STABILIZER:\nCalculate core constant: $equation\n(Selecting incorrect shunt triggers surge).",
                    "Analysis: Verify arithmetic carefully before tapping the shunt node."
                )
            }
        }
    }
}
