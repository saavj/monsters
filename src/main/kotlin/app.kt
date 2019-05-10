
//https://gist.github.com/flashingpumpkin/91e2a5527a40602f5c0ba57845191f51

class Game(val monsters: List<Monster>, val cities: List<City>) {
    fun findCityByName(name: String): City {
        val x = cities.find { x -> x.name == name }
        return x!!
    }
}

class Monster(val number: Int, val currentCity: City, val lastDirectionTravelled: String) {
    override fun toString(): String {
        return "Monster Number " + number+ " travelled " + lastDirectionTravelled + " and is now currently in " + currentCity
    }

    fun move(game: Game): Monster {
        val travel = currentCity.nextRandomCityFromHere(game)
        return Monster(number, travel.first, travel.second)
    }
}

class City(val name: String, private val neighbouringCities: Map<String, String>) {

    override fun toString(): String {
        return name
    }

    //todo this is awful
    fun nextRandomCityFromHere(game: Game): Pair<City, String> {
        val x: List<String> = neighbouringCities.map { v -> v.value }
        val nextCityName = x.random()
        val directionTravelled: String = neighbouringCities.filter { m -> m.value == nextCityName }.keys.first()
        return Pair(game.findCityByName(nextCityName), directionTravelled)
    }

    companion object {

    }
}

fun round(game: Game): Game {
    println("\n")
    println("Next Round: ")
    println("=============================")

    val nextState: Game = Game(game.monsters.map { it.move(game) }, game.cities)

    nextState.monsters.forEach { l ->
        println(l)
    }

    return nextState
}

fun fight(game: Game): Game {
    val cities = game.monsters.map { it.currentCity.name to it }.toMap()
    return game
}

fun main(args: Array<String>) {
    println("=============================")
    println("Please enter how many monsters you need: ")
    val noOfMonsters = readLine()?.toInt()
    val initialCities = Initialisation.listOfCities
    val initialGame = Game(List(noOfMonsters!!) { index ->
        index + 1
    }.map { x -> Monster(x, initialCities.random(), "from home") }, initialCities)

    println("Here is your starting status: ")
    println("==============================")
    initialGame.monsters.forEach { l ->
        println(l)
    }

    round(initialGame)

    return
}

object Initialisation {
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
