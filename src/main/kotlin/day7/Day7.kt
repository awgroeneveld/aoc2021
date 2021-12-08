package day7

import loadResource
import java.io.File
import kotlin.math.abs

class Minimizer() {
    constructor(initialPositions: List<Int>):this(){
        initialPositions.forEach { addCrab(it) }
    }

    val nrCrabsByPosition = HashMap<Int, Int>()

    fun addCrab(position: Int) {
        val crabsAtPosition = nrCrabsByPosition[position] ?: 0
        nrCrabsByPosition[position] = crabsAtPosition + 1
    }

    fun minimize(): Pair<Int, Int> {
        val minValue = nrCrabsByPosition.keys.minOf { it }
        val maxValue = nrCrabsByPosition.keys.maxOf { it }
        val costByPosition= minValue.rangeTo(maxValue)
            .map { position -> position to nrCrabsByPosition.entries.sumOf { it.value * abs(it.key - position) } }
         return costByPosition.sortedBy { it.second }.first()
    }

    fun calculateEnergy(steps:Int)=
        1.rangeTo(steps).map{it}.sum()


    fun minimize2(): Pair<Int, Int> {
        val minValue = nrCrabsByPosition.keys.minOf { it }
        val maxValue = nrCrabsByPosition.keys.maxOf { it }
        val costByPosition= minValue.rangeTo(maxValue)
            .map { position -> position to nrCrabsByPosition.entries.sumOf { it.value *  calculateEnergy(abs(it.key - position)) } }
        return costByPosition.sortedBy { it.second }.first()
    }
}

fun main() {
    val initialPositions = File(loadResource("day7/input.txt")).readLines().first().split(',').map { it.toInt() }
    val minimizer=Minimizer(initialPositions)

    val (position,energy)=minimizer.minimize()
    println("Minimum energy $energy at position $position")
    val (position2,energy2)=minimizer.minimize2()
    println("Minimum energy $energy2 at position $position2")
}
