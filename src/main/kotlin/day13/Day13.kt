package day13

import loadResource
import java.io.File
import java.util.*

data class Fold(val direction: FoldDirection, val position: Int)

enum class FoldDirection {
    VERTICAL, HORIZONTAL
}


class Foldables {
    private val _page = HashSet<Pair<Int, Int>>()
    private val _folds = LinkedList<Fold>()
    private val _foldResults = LinkedList<Set<Pair<Int, Int>>>()

    val page: Set<Pair<Int, Int>>
        get() = _page.toSet()

    val folds: List<Fold>
        get() = _folds.toList()

    val foldResults: List<Set<Pair<Int, Int>>>
        get() = _foldResults.toList()

    fun addLine(line: String) {
        if (line.isBlank()) return
        if (line.startsWith("fold")) addFoldLine(line)
        else addMarkerLine(line)
    }

    private fun addMarkerLine(line: String) {
        val (x, y) = line.split(",").map { it.toInt() }
        _page.add(Pair(x, y))
    }

    private fun addFoldLine(line: String) {
        val (direction, position) = line.removePrefix("fold along ").split("=")
        _folds += Fold(if (direction == "x") FoldDirection.VERTICAL else FoldDirection.HORIZONTAL, position.toInt())
    }

    fun performFolds() {
        var lastFoldResult = page
        folds.forEach {
            lastFoldResult = performFold(it, lastFoldResult)
            _foldResults.add(lastFoldResult)
        }
    }

    private fun performFold(fold: Fold, pageIn: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> =
        when (fold.direction){
            FoldDirection.VERTICAL -> performVerticalFold(fold.position, pageIn)
            else -> performHorizontalFold(fold.position, pageIn)
        }

    private fun performHorizontalFold(position: Int, pageIn: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        val upperPart=pageIn.filter { it.second<position }.toSet()
        val lowerPart=pageIn.filter{it.second>position}
        val modifiedLower=lowerPart.map{it.first to 2*position-it.second}.toSet()
        return upperPart+modifiedLower
    }

    private fun performVerticalFold(position: Int, pageIn: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> {
        val leftPart=pageIn.filter { it.first<position }.toSet()
        val rightPart=pageIn.filter{it.first>position}
        val modifiedRight=rightPart.map{2*position-it.first to it.second}.toSet()
        return leftPart+modifiedRight
    }

    fun lastFold():List<String>{
        val result=LinkedList<String>()
        val lastFoldResult=_foldResults.last()
        val lastlineNumber=lastFoldResult.maxOf { it.second }
        val lastColumnNumber=lastFoldResult.maxOf{it.first}
        for (line in 0..lastlineNumber){
            val itemsOnLine=lastFoldResult.filter { it.second==line }
            val x=" ".repeat(lastColumnNumber+1).toCharArray()
            itemsOnLine.forEach { x[it.first]='#' }
            result.add(String(x))
        }
        return result.toList()
    }
}

fun main() {
    val foldables = Foldables()
    File(loadResource("day13/input.txt")).forEachLine { foldables.addLine(it) }
    foldables.performFolds()
    foldables.foldResults.forEachIndexed { index, pairs -> println("Fold ${index+1} has ${pairs.size}") }
    println("RESULT:")
    foldables.lastFold().forEach { println(it) }
}
