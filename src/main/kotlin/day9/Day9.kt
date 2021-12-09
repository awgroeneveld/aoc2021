package day9

import loadResource
import java.io.File
import java.util.*

class HeightMap {
    private var columnCount: Int=0
    private val rows= LinkedList<List<Int>>()

    fun addLine(line:String){
        this.rows.add(line.map{it.toString().toInt()})
        if (this.rows.size==1) this.columnCount=rows.first.size
    }

    fun isMinimum(row:Int, col:Int, cell:Int):Boolean = getAdjacents(row, col).all { it.third > cell }

    private fun getAdjacents(row: Int, col: Int): List<Triple<Int,Int,Int>> {
        val adjacents = LinkedList<Triple<Int,Int,Int>>()
        if (row > 0) {
            val cellRow=row-1
            adjacents.add(Triple(cellRow,col,rows[cellRow][col]))
        }
        if (col > 0) {
            val cellCol=col-1
            adjacents.add(Triple(row,cellCol, rows[row][cellCol]))
        }
        if (row < rows.size - 1) {
            val cellRow=row+1
            adjacents.add(Triple(cellRow, col, rows[cellRow][col]))
        }
        if (col < columnCount - 1) {
            val cellCol=col+1
            adjacents.add(Triple(row, cellCol, rows[row][cellCol]))
        }
        return adjacents
    }

    fun getAdjacentsSmallerThan9(row:Int, col: Int)=getAdjacents(row,col).filter { it.third<9 }

    fun getLowPoints():List<Triple<Int,Int,Int>> =
        rows.flatMapIndexed { row, cells ->
            cells
                .mapIndexedNotNull { column, cell ->
                if (isMinimum(row,column,cell))
                    Pair(column,cell)
                else null
            }
                .map{Triple(row,it.first,it.second)} }

    fun getRiskLevels()=getLowPoints().map{it.third+1}

    fun getBasins():List<List<Triple<Int,Int,Int>>> {
        val lowPoints=getLowPoints()
        return lowPoints.map{
            println("Doing lowpoint: $it")
            getBasin(it.first, it.second, getLowPoints())+it
        }
    }

    private fun getBasin(row: Int, col: Int, excludes:List<Triple<Int,Int,Int>>):List<Triple<Int,Int,Int>> {
        val items=getAdjacentsSmallerThan9(row, col).filterNot{it in excludes}
        var newExcludes= items+excludes
        val adjacentBasins=LinkedList<Triple<Int,Int,Int>>()
            items.forEach {
                val adjacentBasin=getBasin(it.first, it.second, newExcludes)
                newExcludes+=adjacentBasin
                adjacentBasins+=adjacentBasin
            }
        return items+adjacentBasins
    }
}


fun main() {
    val heightMap=HeightMap()
    File(loadResource("day9/input.txt")).forEachLine { heightMap.addLine(it) }
    println("Sum of rtisk levels: ${heightMap.getRiskLevels().sum()}")
    val maxBasins=heightMap.getBasins().sortedByDescending { it.size }.take(3)
    println("3 max basins with total size: ${maxBasins.fold(1){agg, basin->agg*basin.size}}")
}
