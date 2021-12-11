package day10

import day10.LineInspector.Companion.allowedCloses
import loadResource
import java.io.File
import java.util.*

data class OpenClose(val open:Char, val close: Char, val score:Int, val completionScore: Int){
    constructor(oc:String, score: Int, completeScore: Int):this(oc[0],oc[1],score, completeScore)
}

data class LineResult(val valid: Boolean, val incomplete:Boolean, val invalidScore:Int, val completionScore: Long,val line: String, val completion:String)

class LineInspector {
    companion object{
        val allowed=listOf(OpenClose("()",3,1), OpenClose("[]",57,2), OpenClose("{}",1197,3),OpenClose("<>",25137,4))
        val allowedCloses= allowed.map{it.close to it}.toMap()
        val allowedOpens= allowed.map{it.open to it}.toMap()
    }
    fun inspectLine(line:String):LineResult{
        val opens=LinkedList<Char>()
        for (index in line.indices){
            val c=line[index]
            val allowedClose= allowedCloses[c]
            if (allowedClose!=null){
                if (opens.size==0 || opens.last() != allowedClose.open) {
                    return LineResult(false,false, allowedClose.score,0,line,"")
                }
                opens.removeLast()
            } else if (c in allowedOpens){
               opens.add(c)
            }
        }
        val oc=opens.map{ allowedOpens[it]}
        val incompleteScore=oc.foldRight(0L){x,agg -> agg*5+x!!.completionScore}
        val completion=oc.joinToString("") { it!!.close.toString() }
        return LineResult(false,true,0,incompleteScore,line,completion)
    }
}


fun main() {
    val inspector=LineInspector()
    val lineResults=File(loadResource("day10/input.txt")).readLines().map { inspector.inspectLine(it)}
    println("Score: ${lineResults.sumOf { it.invalidScore }}")
    val incompletes=lineResults.filter { it.incomplete }.sortedBy { it.completionScore }
    println("Scores:")
    incompletes.forEach { println(it.completionScore) }
    println(incompletes[(incompletes.size-1)/2].completionScore)

}
