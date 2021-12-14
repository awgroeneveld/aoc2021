package day14

import loadResource
import java.io.File
import java.util.*
import kotlin.collections.HashMap

data class Rule(val combi:String, val result:Char)

class PolymerProcessor(){
    var lastCharacter: Char?=null
    private val rules= LinkedList<Rule>()
    var occurencesByCombination:MutableMap<String,Long> =HashMap()

    fun addLine(line: String){
        if (line.isBlank())
            return
        if (line.contains("->"))
            addRule(line)
        else
            addInitializer(line)
    }

    private fun addRule(line: String) {
        val (combi,letter)=line.split("->").map{it.trim()}
        rules.add(Rule(combi, letter[0]))
    }

    private fun addInitializer(line: String) {
        lastCharacter=line.last()
        for (i in 0..line.length-2){
            val combi=StringBuilder()
            combi.append(line[i])
            combi.append(line[i+1])
            val combiItem=combi.toString()
            val current=(occurencesByCombination[combiItem]?:0L)+1L
            occurencesByCombination[combiItem]=current
        }
    }

    fun processRule(rule:Rule, combiCounts:MutableMap<String,Long>):Map<String,Long>{
        val additions=HashMap<String,Long>()
        occurencesByCombination.filter{(combi, _) -> combi==rule.combi}.forEach{ (combi,count) ->
            combiCounts.remove(combi)
            additions["${combi[0]}${rule.result}"]=count
            additions["${rule.result}${combi[1]}"]=count
        }
        return additions
    }

    fun processRules(){
        val workingItems = occurencesByCombination.toMutableMap()
        val additions=rules.map { rule ->
            processRule(rule, workingItems )
        }
        occurencesByCombination=workingItems
        additions.forEach {
            it.forEach { (combi, count) ->
                val total=(occurencesByCombination[combi]?:0L)+count
                occurencesByCombination[combi]=total
            }
        }
    }
}


fun main() {
    val processor=PolymerProcessor()
    File(loadResource("day14/input.txt")).forEachLine { processor.addLine(it) }
    for (i in 1..40) {
        processor.processRules()
        val charCountsPre=processor.occurencesByCombination.map{Pair(it.key[0],it.value)}.groupBy { it.first }
        val charCounts=charCountsPre.map{(key,value)-> key to value.sumOf{it.second}}.toMap().toMutableMap()
        val endCharCount=(charCounts[processor.lastCharacter!!]?:0L)+1
        charCounts[processor.lastCharacter!!]=endCharCount

         println(charCounts.size)
        val minLetterCount = charCounts.minByOrNull { (_,count) -> count }!!
        val maxLetterCount = charCounts.maxByOrNull { (_,count) -> count }!!
        val value=maxLetterCount.value-minLetterCount.value

        println("RESULT $i: $maxLetterCount-$minLetterCount: $value Length: ${charCounts.values.sum()}" )
    }
}
