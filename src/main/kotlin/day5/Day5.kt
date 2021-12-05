package day5

import createArray
import loadResource
import java.io.File
import kotlin.math.abs

data class Coordinates(val x: Int, val y: Int){
    companion object{
        fun toCoordinates(s:String):Coordinates{
            val coordinatesSeparated=s.split(',').map{it.toInt()}
            return Coordinates(coordinatesSeparated[0],coordinatesSeparated[1])
        }
    }
    fun toPair()=Pair(x,y)

}

data class Line(val from: Coordinates, val to: Coordinates){

    fun isHorizontal()=from.y==to.y
    fun isVertical()=from.x==to.x
    fun isStraight()=isHorizontal()||isVertical()
    fun isDiagonal():Boolean{
        val deltaX=to.x-from.x
        val deltaY=to.y-from.y
        return abs(deltaX) == abs(deltaY)
    }
    fun isStraightOrDiagonal()=isStraight()||isDiagonal()

    fun getMax(a:Int,b:Int)=if (a>b) a else b
    fun getMaxX()=getMax(from.x, to.x)
    fun getMaxY()=getMax(from.y, to.y)

    fun getAllPointsOnHorizontalLine():List<Coordinates>{
        val fromX =if (from.x<to.x) from.x else to.x
        val toX=if (from.x<to.x) to.x else from.x
        val xPositions=fromX.rangeTo(toX)
        return xPositions.map { x -> Coordinates(x,from.y) }
    }

    fun getAllPointsOnVerticalLine():List<Coordinates>{
        val fromY =if (from.y<to.y) from.y else to.y
        val toY=if (from.y<to.y) to.y else from.y
        val yPositions=fromY.rangeTo(toY)
        return yPositions.map { y -> Coordinates(from.x,y) }
    }

    fun getAllPointsOnLine():List<Coordinates>{
        if (isHorizontal()) return getAllPointsOnHorizontalLine()
        if (isVertical()) return getAllPointsOnVerticalLine()
        if (isDiagonal()) return getAllPointsOnDiagonalLine()
        return listOf()
    }

    fun getAllPointsOnDiagonalLine():List<Coordinates>{
        val f =if (from.x<to.x) from else to
        val t=if (from.x<to.x) to else from
        val xPositions=f.x.rangeTo(t.x)
        val yIncrement=if (f.y<t.y) 1 else -1
        return xPositions.mapIndexed{index, x->Coordinates(x, f.y+yIncrement*index)}
    }
}

class Matrix private constructor(private val array: Array<Array<Int>>) {
    companion object {
        fun create(dimensions:Pair<Int,Int>): Matrix {
            return Matrix(dimensions.createArray(0))
        }
    }
    fun set(x:Int, y:Int){
        array[y][x]= array[y][x]+1
    }

    fun set(c: Coordinates) {
        set(c.x,c.y)
    }

    override fun toString()=array.joinToString("\n") { row -> row.joinToString("") }

    fun getCrossCount(threshold:Int)=array.sumOf { cells -> cells.filter { it>= threshold}.count() }
}

class VentLineProcessor {
    private val lines=ArrayList<Line>()

    fun processLine(s: String){
        val coordinatesAsText=s.trim().split(" -> ")
        lines.add(Line(Coordinates.toCoordinates(coordinatesAsText[0]),Coordinates.toCoordinates(coordinatesAsText[1])))
    }

    fun getMaxCoordinatesPlus1():Coordinates = Coordinates(lines.maxOf { it.getMaxX() }+1,lines.maxOf{it.getMaxY()}+1)

    fun getMatrix(condition: (Line)->Boolean):Matrix{
        val matrix =Matrix.create(getMaxCoordinatesPlus1().toPair())
        val straightLines=lines.filter ( condition )
        straightLines.forEach { line -> line.getAllPointsOnLine().forEach { coord -> matrix.set(coord)  }}
        return matrix
    }

    fun getCrossCount(threshold:Int, condition: (Line)->Boolean)=
        getMatrix(condition).getCrossCount(threshold)
}



fun main() {
    val lineProcessor = VentLineProcessor()
    File(loadResource("day5/input.txt")).forEachLine { lineProcessor.processLine(it) }
    println(lineProcessor.getCrossCount(2,Line::isStraight))
    println(lineProcessor.getCrossCount(2,Line::isStraightOrDiagonal))
}
