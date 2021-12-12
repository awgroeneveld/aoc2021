package day12

import loadResource
import java.io.File
import java.util.*

data class Cave(val name: String, val type: CaveType){
    val connectedCaves= LinkedList<Cave>()
    companion object{
        private fun deduceCaveType(name: String) =
            when (name){
                "start" -> CaveType.START
                "end" -> CaveType.END
                else -> if (name.uppercase() == name) CaveType.BIG else CaveType.SMALL
        }
    }
    constructor(name:String):this(name, deduceCaveType(name))

    fun addConnectedCave(cave:Cave)=connectedCaves.add(cave)

}

class CaveVisitor(private val caves:List<Cave>, private val singleDoubleAllowed:Boolean){

    val start=caves.first{it.type==CaveType.START}

    fun canVisit(cave: Cave, previousVisits: List<Cave>)=
            cave.type==CaveType.END ||
                    cave.type==CaveType.BIG ||
                    (cave.type==CaveType.START && !previousVisits.contains(cave)) ||
                    (
                            cave.type==CaveType.SMALL && (
                                    !previousVisits.contains(cave) ||
                                            (singleDoubleAllowed && !hasPreviousDoubleVisits(previousVisits))
                                    )
                            )

    private fun hasPreviousDoubleVisits(previousVisits: List<Cave>): Boolean {
        val smallOnes=previousVisits.filter { it.type==CaveType.SMALL }
        return smallOnes.size>smallOnes.distinct().size
    }


    fun determinePathsFrom(startFromCave: Cave, previousVisits:List<Cave>):List<List<Cave>>{
        if (!canVisit(startFromCave, previousVisits)){
            return listOf()
        }
        val result=LinkedList<List<Cave>>()
        val visits=previousVisits+startFromCave
        startFromCave.connectedCaves.forEach(){ cave ->
            if (cave.type==CaveType.END) {
                result+=visits+cave
            }
            else {
                result.addAll(determinePathsFrom(cave, visits))
            }
        }
        return result.toList()
    }


    fun determinePaths():List<List<Cave>>  =
        determinePathsFrom(start, listOf())
}

enum class CaveType {
    BIG,SMALL,START,END
}


class CaveSystem {
    private val cavesByName=HashMap<String,Cave>()

    fun addLine(line: String){
        val caveLines=line.split("-")
        addCaves(Cave(caveLines[0]),Cave(caveLines[1]))
    }

    fun addCaves(cave1:Cave, cave2:Cave){
        val from=addCave(cave1)
        val to=addCave(cave2)
        from.addConnectedCave(to)
        to.addConnectedCave(from)
    }
    fun addCave(cave:Cave):Cave{
        val existing=cavesByName[cave.name]
        if (existing!=null)
            return existing
        cavesByName[cave.name]=cave
        return cave
    }

    fun allCaves()=cavesByName.values.toList()
}

fun main() {
    val caveSystem = CaveSystem()
    File(loadResource("day12/input.txt")).forEachLine { caveSystem.addLine(it) }
    val visitor=CaveVisitor(caveSystem.allCaves(),true)
    val paths=visitor.determinePaths()
    println("Found ${paths.size} paths:")
//    paths.forEach { path -> println("FOUND: ${path.joinToString { cave -> cave.name }}") }
}
