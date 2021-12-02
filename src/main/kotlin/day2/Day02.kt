package day2

import loadResource
import java.io.File

data class Movement(val direction: Direction, val units:Int)

enum class Direction {
    UP, DOWN, FORWARD
}

class LineInterpreter{
    fun toMovement(line: String): Movement {
        val lineItems=line.split(' ')
        return Movement(toDirection(lineItems[0]),lineItems[1].toInt())
    }

    private fun toDirection(s: String): Direction {
        return when (s){
            "forward" -> Direction.FORWARD
            "down" -> Direction.DOWN
            "up" -> Direction.UP
            else -> throw IllegalArgumentException("No such direction $s")
        }
    }
}

class LinePart1Processor{
    private var horizontal=0
    private var vertical=0

    fun processLine(movement: Movement){
        when (movement.direction){
            Direction.DOWN -> {
                vertical+=movement.units
            }
            Direction.UP -> {
                vertical-=movement.units
            }
            Direction.FORWARD -> {
                horizontal+=movement.units
            }
        }
    }

    fun answer()=horizontal*vertical
}

class LinePart2Processor{
    private var horizontal=0
    private var vertical=0
    private var aim=0

    fun processLine(movement: Movement){
        when (movement.direction){
            Direction.DOWN -> {
                aim+=movement.units
            }
            Direction.UP -> {
                aim-=movement.units
            }
            Direction.FORWARD -> {
                horizontal+=movement.units
                vertical+=aim*movement.units
            }
        }
    }

    fun answer()=horizontal*vertical
}

fun main(){
    val lineInterpreter=LineInterpreter()
    val line1Processor=LinePart1Processor()
    File( loadResource("day2/input.txt")).forEachLine { line1Processor.processLine(lineInterpreter.toMovement(it)) }
    println("Answer: ${line1Processor.answer()}")

    val line2Processor=LinePart2Processor()
    File( loadResource("day2/input.txt")).forEachLine { line2Processor.processLine(lineInterpreter.toMovement(it)) }
    println("Answer 2: ${line2Processor.answer()}")
}

