import java.util.*

/**
 * Created by iman.
 * iman.neofight@gmail.com
 */

fun main(args: Array<String>) {
    solveProblem()
}

fun <K> List<K>.print() {
    forEach {
        print("$it, ")
    }
}

fun hasConflict(positions: List<Int>): Pair<Boolean, Int> {
    if (positions.size != 8) throw RuntimeException()

    positions.forEach { pos ->
        if (pos < 0 || pos > 7) RuntimeException()
    }

    fun checkFirst(positions: List<Int>): Pair<Boolean, Int> {
        var hasConflict = false
        var conflictsCount = 0
        positions.groupBy { it }
                .forEach { key, value ->
                    if (value.size > 1) {
                        hasConflict = true
                        conflictsCount += value.size
                    }
                }
        return Pair(hasConflict, conflictsCount)
    }

    val firstResult = checkFirst(positions)

    fun checkSecond(positions: List<Int>): Pair<Boolean, Int> {
        var conflictsCount = 0
        var hasConflict = false
        for (i in 0 until positions.size) {
            var counts = 0
            for (j in i downTo 0) {
                if (positions[j] == i - j) {
                    counts++
                }
            }
            if (counts > 1) {
                hasConflict = true
                conflictsCount += counts
            }


            counts = 0
            val upper = positions.size - 1
            val downer = positions.size - 1 - i
            for (j in upper downTo downer) {
                val ss = (j - downer)
                val checking = upper - ss
                if (positions[j] == checking) {
                    counts++
                }
            }
            if (counts > 1) {
                hasConflict = true
                conflictsCount += counts
            }
        }

        return Pair(hasConflict, conflictsCount)
    }

    val secondResult = checkSecond(positions)

    fun checkThird(positions: List<Int>): Pair<Boolean, Int> {
        var hasConflict = false
        var conflictsCount = 0

        for (i in 0 until positions.size) {
            var counts = 0
            for (j in i until positions.size) {
                if (positions[j] == j - i) {
                    counts++
                }
            }
            if (counts > 1) {
                hasConflict = true
                conflictsCount += counts
            }


            counts = 0
            val upper = positions.size - 1
            val downer = 0
            for (j in upper downTo downer) {
                val ss = j + (positions.size - 1 - i)
                if (positions[j] == ss) {
                    counts++
                }
            }
            if (counts > 1) {
                hasConflict = true
                conflictsCount += counts
            }
        }

        return Pair(hasConflict, conflictsCount)
    }

    val thirdResult = checkThird(positions)

    if (firstResult.first || secondResult.first || thirdResult.first) {
        return Pair(true, firstResult.second + secondResult.second + thirdResult.second)
    } else {
        return Pair(false, firstResult.second + secondResult.second + thirdResult.second)
    }
}

fun solveProblem() {

    val firstGeneration = List(100) { makeRandomNode() }

    var generation: List<Node> = firstGeneration
    var previousGeneration : List<Node>? = null

    var generationCounter = 1
    while (true) {
        val needToMutation = previousGeneration?.conflictsCount() ?: 0 == generation.conflictsCount()

        generation = generation.sortedBy { it.conflict.second }

        val bestNode = generation[0]
        if (!bestNode.conflict.first) {
            print("Solved on ${generationCounter}th generation\n")
            print("Best is ")
            bestNode.list.print()
            print(" With ${bestNode.conflict.second} Conflicts\n")
            bestNode.printBoardStyle()
            return
        } else {
            val sumConflicts = generation.sumBy { it.conflict.second }
            print("Generation $generationCounter failed with $sumConflicts conflicts\t\t")
            print("Best is ")
            generation[0].list.print()
            print(" With ${generation[0].conflict.second} Conflicts\n")
        }

        val newGeneration = mutableListOf<Node>()

        generation.subList(0, generation.size / 2)
                .forEach { node ->
                    val leftSide = node.list.subList(0, node.list.size / 2)
                    val rightSide = node.list.subList(node.list.size / 2, node.list.size)

                    val firstList = mutableListOf<Int>()
                    firstList.addAll(leftSide)
                    firstList.addAll(rightSide)
                    if (needToMutation) {
                        firstList[Math.abs(Random().nextInt() % 8)] = Math.abs(Random().nextInt() % 8)
                    }
                    val firstChild = Node(firstList)
                    newGeneration.add(firstChild)

                    val secondList = mutableListOf<Int>()
                    secondList.addAll(rightSide)
                    secondList.addAll(leftSide)
                    if (needToMutation) {
                        secondList[Math.abs(Random().nextInt() % 8)] = Math.abs(Random().nextInt() % 8)
                    }
                    val secondChild = Node(secondList)
                    newGeneration.add(secondChild)
                }

        previousGeneration = generation
        generation = newGeneration

        generationCounter++
    }

}

fun List<Node>.conflictsCount() {
    this.sumBy { it.conflict.second }
}

fun makeRandomNode() = Node(List(8) { Math.abs(Random().nextInt() % 8) })

fun Node.printBoardStyle() {
    for (i in 0..7) {
        for (j in 0..7) {
            if (i == list[j]) {
                print("  Q  ")
            } else {
                print("  -  ")
            }
        }
        print('\n')
    }
}

data class Node(val list : List<Int>, val conflict : Pair<Boolean, Int> = hasConflict(list))