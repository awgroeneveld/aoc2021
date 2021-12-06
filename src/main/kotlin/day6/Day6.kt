package day6


import loadResource
import java.io.File

class LanternFishManager() {
    private var lanternFishes = Array(9) { 0L }
    fun createAndAddLanternFish(initialTimer: Int, count: Long = 1) {
        lanternFishes[initialTimer]=lanternFishes[initialTimer]+count
    }

    fun nextDay() {
        val newlanternFishes = lanternFishes[0]
        val updated = lanternFishes.mapIndexed { index, count -> Pair(if (index == 0) 6 else index - 1, count) }
        lanternFishes = Array(9) { 0L }
        updated.forEach { createAndAddLanternFish(it.first,it.second) }
        createAndAddLanternFish(8, newlanternFishes)
    }


    fun getTotalFishes() = lanternFishes.sum()
}


fun main() {
    val initialFishTimers = File(loadResource("day6/input.txt")).readLines().first().split(',').map { it.toInt() }
    val manager = LanternFishManager()
    initialFishTimers.forEach { manager.createAndAddLanternFish(it) }
//    println(manager.getTimers())
    1.rangeTo(256).forEach {
        manager.nextDay()
//        println(manager.getTimers())
        println("Total on day $it: ${manager.getTotalFishes()}")
    }
}
