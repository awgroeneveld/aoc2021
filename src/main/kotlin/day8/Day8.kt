package day8

import loadResource
import java.io.File
import java.util.*

enum class Digits(val value: Int, val segments: String) {
    ZERO(0, "abcefg"),
    ONE(1, "cf"),
    TWO(2, "acdeg"),
    THREE(3, "acdfg"),
    FOUR(4, "bcdf"),
    FIVE(5, "abdfg"),
    SIX(6, "abdefg"),
    SEVEN(7, "acf"),
    EIGHT(8, "abcdefg"),
    NINE(9, "abcdfg");

    companion object {
        val valuesBySegmentCount = values().groupBy { it.segments.length }
    }
}

class InputManager() {
    val signals = LinkedList<List<String>>()
    val output = LinkedList<List<String>>()
    val digital=LinkedList<Array<String>>()
    fun addSignalLine(line: String): List<String> {
        val result=line.split(' ')
        signals.add(result)
        return result
    }

    fun addOutputLine(line: String) {
        output.add(line.split(' '))
    }

    fun addLine(line: String) {
        val segments = line.split('|')
        val signal=addSignalLine(segments[0].trim())
        addOutputLine(segments[1].trim())
        deduceDigits(signal)
    }

    fun deduceDigits(signal: List<String>) {
        val digits = Array<String>(10) { "" }
        digits[1] = signal.first { it.length == 2 }
        digits[4] = signal.first { it.length == 4 }
        digits[7] = signal.first { it.length == 3 }
        digits[8] = signal.first { it.length == 7 }

        val zeroSixNine = signal.filter { it.length == 6 }
        digits[9] = zeroSixNine.first { segment -> digits[4].all { segment.contains(it) } }
        val zeroOrSix = zeroSixNine.filterNot { it == digits[9] }
        digits[0] = zeroOrSix.first { segment -> digits[1].all { segment.contains(it) } }
        digits[6] = zeroOrSix.first{ it != digits[0] }

        val twoThreeFive = signal.filter { it.length == 5 }
        digits[3] = twoThreeFive.first { segment -> digits[1].all { segment.contains(it) } }
        val twoOrFive = twoThreeFive.filterNot { it == digits[3] }
        digits[5] = twoOrFive.first { segment -> segment.count { digits[6].contains(it) } == 5 }
        digits[2] = twoOrFive.first { it != digits[5] }

        this.digital += digits
    }

    fun getOutputs():List<Int> =
        output.mapIndexed{index, quad->quadToNumber(digital[index],quad).toInt()}


    private fun quadToNumber(digits: Array<String>, quad: List<String>) =
        quad.joinToString("") { toNumber(digits,it).toString() }

    private fun toNumber(digits: Array<String>, segments: String):Int {
        return digits.indexOfFirst{digit -> digit.length==segments.length && segments.all{digit.contains(it)}}
    }


    fun countDistinctsInOutput(): Int {
        val distincts = Digits.valuesBySegmentCount.filter { it.value.size == 1 }.map { it.key }
        return output.sumOf { line -> line.filter { it.length in distincts }.size }
    }

}

fun main() {
    val inputManager = InputManager()
    println(Digits.valuesBySegmentCount)
    File(loadResource("day8/input.txt")).forEachLine { inputManager.addLine(it) }

    println("Distinct count: ${inputManager.countDistinctsInOutput()}")
    println("Outputs ${inputManager.getOutputs()}")
    println("Output sum: ${inputManager.getOutputs().sum()}")
}
