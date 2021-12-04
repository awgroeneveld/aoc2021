package day4

import loadResource
import java.io.File

class BingoCard(initialCardItems: List<List<Pair<Int, Boolean>>>) {
    private val columnCount=initialCardItems.first().size
    private var cardItems = initialCardItems

    fun hasBingo() = rowsAreDone() || columnsAreDone()

    private fun columnsAreDone(): Boolean = (0..columnCount-1).any{column -> cellsAreDone(cardItems.map{row->row[column]})}

    private fun rowsAreDone(): Boolean = cardItems.any { cellsAreDone(it) }

    fun cellsAreDone(cells: List<Pair<Int, Boolean>>) = cells.all { it.second }

    fun addNumber(number: Int) {
        if (!hasBingo())
            cardItems = cardItems.map { addNumberMatchToCells(number, it) }
    }

    private fun addNumberMatchToCells(number: Int, cells: List<Pair<Int, Boolean>>) =
        cells.map { pair -> if (pair.first == number) Pair(number, true) else pair }

    fun getSumUnMarked()=cardItems.sumOf { cells -> cells.filter { !it.second }.map{it.first }.sum() }

}

class BingoLineProcessor(private val boardSize: Int) {
    private var numbersDrawn = ArrayList<Int>()
    private var cards = ArrayList<BingoCard>()
    private var currentCard: MutableList<List<Pair<Int, Boolean>>> = ArrayList()

    fun processLine(s: String) {
        if (numbersDrawn.size == 0)
            processNumbersDrawn(s)
        else
            processPotentialCardLine(s)
    }

    private fun processPotentialCardLine(s: String) {
        if (s.isBlank()) {
            processEmptyLine()
        } else {
            processCardLine(s)
        }
    }

    private fun processCardLine(s: String) {
        currentCard.add(s.trim().split(Regex("\\s+")).map { Pair(it.toInt(), false) }.toList())
    }

    private fun processEmptyLine() {
        if (currentCard.size > 0) {
            cards.add(BingoCard(currentCard.toList()))
        }
        currentCard = ArrayList()
    }

    private fun processNumbersDrawn(s: String) {
        s.split(',').forEach { numbersDrawn.add(it.toInt()) }
    }

    fun getNumbersDrawn()=numbersDrawn.toList()
    fun getCards()=cards.toList()

}


fun main() {
    val lineProcessor = BingoLineProcessor(5)
    File(loadResource("day4/input.txt")).forEachLine { lineProcessor.processLine(it) }
    val bingoesByDraw= getFullPlay(lineProcessor.getNumbersDrawn(), lineProcessor.getCards())

    val first=bingoesByDraw.first{ it.second.isNotEmpty() }
    val last=bingoesByDraw.last{ it.second.isNotEmpty() }

    println("Done. Found firsrt bingo at number ${first.first}, " +
            "resulting in ${first.second.first().getSumUnMarked()*first.first} !")
    println("Done. Found last bingo at number ${last.first}, " +
            "resulting in ${last.second.first().getSumUnMarked()*last.first} !")
}

fun addNumber(number: Int, cards:List<BingoCard>){
    cards.forEach { card->
        card.addNumber(number)
    }
}

fun getFullPlay(numbersDrawn: List<Int>, cards: List<BingoCard>): ArrayList<Pair<Int, List<BingoCard>>> {
    var filteredCards=cards
    val result=ArrayList<Pair<Int,List<BingoCard>>>()
    numbersDrawn.forEach { i ->
        addNumber(i, cards)
        result.add(Pair(i,filteredCards.filter { it.hasBingo() }))
        filteredCards=filteredCards.filter{!it.hasBingo()}
    }
    return result
}
