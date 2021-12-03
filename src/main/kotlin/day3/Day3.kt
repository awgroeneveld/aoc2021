package day3

import loadResource
import java.io.File

data class Line(val numbers: List<Int>) {
    companion object {
        fun toList(s: String) =
            s.toList().map { it.digitToInt() }
    }

    constructor(s: String) : this(toList(s))
}

class LineProcessor(private val size: Int) {
    private val allLines = ArrayList<List<Int>>()
    fun processLine(numbers: List<Int>) {
        allLines += numbers
    }

    fun getGamma() = toInt(getMostCounts(allLines,1))
    fun getEpsilon() = toInt(getLeastCounts(allLines,1))
    fun getEnergy() = getGamma() * getEpsilon()

    fun getOxygenRating(): Int = toInt(getRating(allLines){x->getMostCounts(x, 1)})
    fun getCO2ScrubberRating(): Int = toInt(getRating(allLines){x->getLeastCounts(x,0)})
    fun getLifeSupportRating() = getOxygenRating() * getCO2ScrubberRating()

    fun getCounts(lookFor: Int, lines: List<List<Int>>): List<Int> {
        val counts = Array(size) { 0 }
        counts.forEachIndexed { index, count -> lines.forEach { counts[index] = count + if (it[index] == lookFor) 1 else 0 } }
        return counts.toList()
    }

    fun getZeroCounts(lines: List<List<Int>>) = getCounts(0, lines)
    fun getOneCounts(lines: List<List<Int>>) = getCounts(1, lines)

    fun getMostCounts(lines: List<List<Int>>, equalCountsAs: Int): List<Int> {
        val oneCounts = getOneCounts(lines)
        return getZeroCounts(lines).mapIndexed { index, zeroCount ->
            val oneCount = oneCounts[index]
            if (zeroCount > oneCount) 0 else if (zeroCount == oneCount) equalCountsAs else 1
        }
    }

    fun getLeastCounts(lines: List<List<Int>>, equalCountsAs: Int): List<Int> {
        val oneCounts = getOneCounts(lines)
        return getZeroCounts(lines).mapIndexed { index, zeroCount ->
            val oneCount = oneCounts[index]
            if (zeroCount < oneCount) 0 else if (zeroCount == oneCount) equalCountsAs else 1
        }
    }

    private fun getRating(source: List<List<Int>>, mostOrLeastCounted: (List<List<Int>>) -> List<Int>): List<Int> {
        val counted = mostOrLeastCounted(source)
        val filteredLines = Array(size + 1) { i -> if (i == 0) source else listOf() }
        counted.forEachIndexed { index, i ->
            val filteredCount = mostOrLeastCounted(filteredLines[index])
            filteredLines[index + 1] = filteredLines[index].filter { it[index] == filteredCount[index] }
        }
        return filteredLines.first { it.size == 1 }.first()
    }

    fun toInt(ints: List<Int>) = ints.joinToString("").toInt(2)
}

fun main() {
    val lineProcessor = LineProcessor(12)
    File(loadResource("day3/input.txt")).forEachLine { lineProcessor.processLine(Line(it).numbers) }
    println("Epsilon ${lineProcessor.getEpsilon()}, Gamma ${lineProcessor.getGamma()}, Energy ${lineProcessor.getEnergy()}")
    println("Oxygen rating ${lineProcessor.getOxygenRating()}, CO2 ScrubberRating: ${lineProcessor.getCO2ScrubberRating()}, Life Support Rating: ${lineProcessor.getLifeSupportRating()}")
}

