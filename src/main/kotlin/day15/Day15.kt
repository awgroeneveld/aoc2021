package day15

import loadResource
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

data class Chiton(val row: Int, val col: Int, val risk: Int) {
    val coordinates = Pair(row, col)
    var shortest = false
    fun copy(amount: Pair<Int, Int>, riskIncrease: Int): Chiton {
        val newRiskUncorrected = risk + riskIncrease
        val newRiskCorrected = if (newRiskUncorrected > 9) newRiskUncorrected % 9 else newRiskUncorrected
        return Chiton(row + amount.first, col + amount.second, newRiskCorrected)
    }


}

data class ChitonRisk(val riskBySourceChiton: Map<Chiton, Int>) {
    fun getRiskFrom(sourceChiton: Chiton) = riskBySourceChiton[sourceChiton]
}

class Path(var visitedChitons: Array<Chiton>, var totalRisk: Int) {

    constructor(initialChiton: Chiton) : this(arrayOf(initialChiton), 0)

    fun addChiton(chiton: Chiton): Path {
        val copy= visitedChitons.copyOf(visitedChitons.size+1)
        copy[visitedChitons.size]=chiton
        return Path(copy as Array<Chiton>, totalRisk + chiton.risk)
    }
    fun lastVisited() = visitedChitons.last()


}

class ChitonProcessor() {
    private var lastChiton: Chiton? = null
    private var neighbours: Array<Array<List<Chiton>>> = arrayOf()
    private var colSize: Int = 0
    val grid = ArrayList<List<Chiton>>()

    private fun getNeighbourCoordinates(row: Int, col: Int): LinkedList<Pair<Int, Int>> {
        val result = LinkedList<Pair<Int, Int>>()
        if (row < grid.size - 1)
            result.add(Pair(row + 1, col))
        if (col < colSize - 1)
            result.add(Pair(row, col + 1))
        if (col > 0)
            result.add(Pair(row, col - 1))
        if (row > 0)
            result.add(Pair(row - 1, col))
        return result
    }

    fun createShortestPathsArray(): Array<Array<Path?>> = Array(colSize) { Array(colSize) { null } }

    private fun createDistances(): Array<Array<Path?>> {
        val result:Array<Array<Path?>> = Array(colSize) { Array(colSize) { null } }
        grid.forEach { row -> row.forEach{chiton -> result[chiton.row][chiton.col]=Path(arrayOf(),Int.MAX_VALUE)}}
        return result
    }


    fun findLeastRiskPath(): Path {
        this.neighbours = createNeighbours()
        val distances = createDistances()
        var node = grid[0][0]
        val shortestPaths = createShortestPathsArray()
        var path = Path(node)
        shortestPaths[node.row][node.col] = path
        var iter = 0
        var ready = false
        while (!ready) {
            iter++
            if (iter % 1000 == 0) {
                println("Iteration $iter, shortest ${shortestPaths.flatMap{row -> row.filterNotNull()}.size}," +
                        " ${distances.flatMap { row -> row.filterNotNull() }.size} ")
            }
            this.neighbours[node.row][node.col].forEach { chiton ->
                if (!chiton.shortest) {
                    val curPath = distances[chiton.row][chiton.col]
                    val newPath = path.addChiton(chiton)
                    if (curPath!!.totalRisk > newPath.totalRisk) {
                        curPath.totalRisk = newPath.totalRisk
                        curPath.visitedChitons = newPath.visitedChitons
                    }
                }
            }
            path = distances.flatMap { row -> row.filterNotNull() }.minByOrNull { it.totalRisk }!!
            node = path.lastVisited()
            distances[node.row][node.col]=null
            shortestPaths[node.row][node.col] = path
            node.shortest = true
            if (node == lastChiton) {
                println("Found path with ${path.visitedChitons.size} and risk ${path.totalRisk}")
            }
            ready = node == lastChiton
        }
        return shortestPaths[lastChiton!!.row][lastChiton!!.col]!!
    }

    private fun createNeighbours() = grid.mapIndexed { row, items ->
        items.mapIndexed { col, _ ->
            getNeighbourCoordinates(
                row,
                col
            ).map { grid[it.first][it.second] }
        }.toTypedArray()
    }.toTypedArray()

    fun addLine(line: String) {
        val row = grid.size
        val chitons = line.mapIndexed { col, c -> Chiton(row, col, c - '0') }
        this.grid.add(chitons)
        this.lastChiton = chitons.last()
        if (row == 0) {
            this.colSize = grid[0].size
        }
    }

    fun phase2(): Path {
        val rowCount = grid.size
        val newItems = (0..4).flatMap { phaseRow ->
            grid.map { row ->
                (0..4).flatMap { phaseCol ->
                    row.map { chiton ->
                        chiton.shortest = false
                        chiton.copy(Pair(phaseRow * rowCount, phaseCol * colSize), phaseCol + phaseRow)
                    }
                }
            }
        }
        grid.clear()
        newItems.forEach { grid.add(it) }
        this.colSize *= 5
        this.lastChiton = this.grid.last().last()
        return findLeastRiskPath()
    }
}

fun main() {
    val processor = ChitonProcessor()
    File(loadResource("day15/input.txt")).forEachLine { processor.addLine(it) }
    val path = processor.findLeastRiskPath()
    println("Found path with ${path.visitedChitons.size} and risk ${path.totalRisk}")
    val path2 = processor.phase2()
    println("Found path with ${path2.visitedChitons.size} and risk ${path2.totalRisk}")
}
