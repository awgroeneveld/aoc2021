package day11

import loadResource
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class Octopus(initialEnergyLevel: Int) {
    private var hasFlashed = false
    private var energyLevel = initialEnergyLevel
    private var flashCount = 0

    fun increaseEnergy() {
        energyLevel++
    }

    fun currentEnergyLevel() = energyLevel

    private fun canFlash() = energyLevel > 9

    fun flash(): Boolean {
        if (canFlash() && !hasFlashed) {
            hasFlashed = true
            flashCount++
            return true
        }
        return false
    }

    fun resetIfFlashed() {
        if (hasFlashed) {
            energyLevel = 0
            hasFlashed = false
        }
    }

    fun flashCount()=flashCount

}

class OctopusGrid(private val log: Boolean = true) {
    private var columnCount=0
    private var stepCount = 0
    private val octopusGrid = ArrayList<List<Octopus>>()
    private var row = 0
    private val flashCounts=LinkedList<Int>()

    fun addLine(s: String) {
        val octopuses=s.map { Octopus("$it".toInt()) }
        octopusGrid.add(octopuses)
        if (row==0){
            columnCount=octopuses.size
        }
        row++
    }

    fun performStep():Boolean {
        stepCount++
        increaseEnergy()
        val flashed=performFlashes()
        flashCounts.add(flashed.size)
        resetFlashes()
        performLogging()
        return flashed.size==columnCount*octopusGrid.size
    }

    fun performSteps(maxSteps: Int) {
        for (i in 1..maxSteps)
            performStep()
    }

    private fun resetFlashes() {
        octopusGrid.forEach { row -> row.forEach { octopus -> octopus.resetIfFlashed() } }
    }


    private fun performFlashes(): List<Pair<Int, Int>> {
        val flashed = octopusGrid
            .flatMapIndexed { row, octopuses ->
                octopuses
                    .mapIndexedNotNull { col, octopus ->
                        if (octopus.flash()) Pair(row, col) else null
                    }
            }
        val consecutiveFlashes = flashAdjacents(flashed)
        return flashed + consecutiveFlashes
    }

    private fun performLogging() {
        if (!log)
            return

        println("After step $stepCount")
        octopusGrid.forEach { row -> println(row.joinToString("") { if (it.currentEnergyLevel() == 0) "0" else "-" }) }
        println()
    }

    private fun flashAdjacents(flashedBefore: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        val allAdjacents = flashedBefore.flatMap { getAdjacentsCoordinates(it.first, it.second) }
        allAdjacents.forEach { (row, col) -> octopusGrid[row][col].increaseEnergy() }
        val flashedAdjacents = allAdjacents.filter { (row, col) -> octopusGrid[row][col].flash() }
        val nextFlashes = if (flashedAdjacents.isNotEmpty()) flashAdjacents(flashedAdjacents) else emptyList()
        return flashedAdjacents + nextFlashes
    }

    private fun increaseEnergy() {
        octopusGrid.forEach { row -> row.forEach { octopus -> octopus.increaseEnergy() } }
    }

    fun getHorizontalAdjacents(row: Int, col:Int): List<Pair<Int,Int>>{
        val adjacents=LinkedList<Pair<Int,Int>>()
        if (col > 0) {
            adjacents.add(Pair(row, col - 1))
        }
        if (col < columnCount - 1) {
            adjacents.add(Pair(row, col + 1))
        }
        return adjacents
    }

    fun getVerticalAdjacents(row: Int, col:Int): List<Pair<Int,Int>>{
        val adjacents=LinkedList<Pair<Int,Int>>()
        if (row > 0) {
            adjacents.add(Pair(row - 1, col))
        }
        if (row < octopusGrid.size - 1) {
            adjacents.add(Pair(row + 1, col))
        }
        return adjacents
    }

    fun getDiagonalAdjacents(row: Int, col:Int): List<Pair<Int,Int>>{
        val adjacents=LinkedList<Pair<Int,Int>>()
        if (row > 0) {
            if (col>0)
                adjacents.add(Pair(row-1,col-1))
            if (col<columnCount-1)
                adjacents.add(Pair(row-1,col+1))
        }
        if (row < octopusGrid.size - 1) {
            if (col>0)
                adjacents.add(Pair(row+1,col-1))
            if (col<columnCount-1)
                adjacents.add(Pair(row+1,col+1))
        }
        return adjacents
    }

    fun getTotalFlashCount()=octopusGrid.sumOf{it.sumOf { it.flashCount() }}

    fun getAdjacentsCoordinates(row: Int, col: Int): List<Pair<Int, Int>> {
        return getHorizontalAdjacents(row,col)+getVerticalAdjacents(row,col)+getDiagonalAdjacents(row,col)
    }
    fun getStepsWithAllFlashes()=flashCounts.mapIndexedNotNull{index, it -> if (it==columnCount*octopusGrid.size) index+1 else null}

    fun performStepsUntilAllFlash(){
        var ready=false
        while (!ready && stepCount<1000){
            ready=performStep()
        }
    }
}

fun main() {
    val grid = OctopusGrid(false)
    File(loadResource("day11/input.txt")).forEachLine { grid.addLine(it) }
    grid.performStepsUntilAllFlash()
    println("Flash count: ${grid.getTotalFlashCount()}")

    println("Steps with all flashes: ${grid.getStepsWithAllFlashes()}")
}
