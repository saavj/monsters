
//https://gist.github.com/flashingpumpkin/91e2a5527a40602f5c0ba57845191f51

data class Game(val monsters: Map<Int, City>)

class City(val name: String, val neighbouringCities: Map<String, String>) {

    override fun toString(): String {
        return name
    }

    fun nextRandomCityFromHere(): City {
        val x = neighbouringCities.map { v -> v.value }
        val nextCityName = x.random()
        return findCityByName(nextCityName)
    }

    companion object {

        fun findCityByName(name: String): City {
            val x = listOfCities.find { x -> x.name == name }
            return x!!
        }

        val listOfCities: List<City> = this.javaClass.getResource("/map-small.txt").readText().split("\n").map { s ->
            val a = s.split(" ")
            val name = a.get(0)

            val neighbouringCities: Map<String, String> = a.subList(1, a.size).map { x ->
                val splittingDirectionAndName = x.split("=")
                splittingDirectionAndName.first() to splittingDirectionAndName.last()
            }.toMap()

            City(name, neighbouringCities)
        }
    }
}

fun round(game: Game): Game {
    println("\n")
    println("Next Round: ")
    println("=============================")

    val nextState = game.monsters.mapValues { v ->
        v.value.nextRandomCityFromHere()
    }

    nextState.forEach { l ->
        println("Monster Number " + l.key + " is now currently in " + l.value)
    }

    return Game(nextState)
}

fun main(args: Array<String>) {
    println("=============================")
    println("Please enter how many monsters you need: ")
    val noOfMonsters = readLine()?.toInt()
    val initialList = Game(List(noOfMonsters!!) { index ->
        index + 1
    }.map { it to City.listOfCities.random() }.toMap())

    println("Here is your starting status: ")
    println("==============================")
    initialList.monsters.forEach { l ->
        println("Monster Number " + l.key + " is currently in " + l.value)
    }

    round(initialList)

    return
}
