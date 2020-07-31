import kotlin.random.Random

//https://gist.github.com/flashingpumpkin/91e2a5527a40602f5c0ba57845191f51

class Game(val monsters: List<Monster>, val cities: List<City>, val round: Int) {
    fun findCityByName(name: Name): City {
        val y = cities.find { x ->
            x.name.value == name.value
        }
        return y!!
    }
}

class Monster(val id: Int, val currentCity: City, val lastDirectionTravelled: Direction) {
    override fun toString(): String {
        return "Monster Number " + id + " travelled " + lastDirectionTravelled + " and is now currently in " + currentCity
    }

    fun move(game: Game): Monster {
        val travel = currentCity.nextRandomCityFromHere(game)
        return Monster(id, travel.first, travel.second)
    }

    fun removeBadDirections(badCities: List<City>): Monster {
        val newDirections: Map<Direction, Name> = currentCity.neighbouringCities.filterNot { e ->
            badCities.map { it.name.value }.contains(e.value.value)
        }
        return Monster(id, City(currentCity.name, newDirections), lastDirectionTravelled)
    }
}

class City(val name: Name, val neighbouringCities: Map<Direction, Name>) {

    override fun toString(): String {
        return name.value
    }

    //todo this is awful
    fun nextRandomCityFromHere(game: Game): Pair<City, Direction> {
        val x: List<Name> = neighbouringCities.map { v -> v.value }
        val nextCityName = x.random()
        val directionTravelled: Direction = neighbouringCities.filter { m -> m.value == nextCityName }.keys.first()
        return Pair(game.findCityByName(nextCityName), directionTravelled)
    }

    fun removeBadDirections(badCities: List<City>): City {
        val done: Map<Direction, Name> = neighbouringCities.filterNot { e ->
            badCities.map { x -> x.name.value }.contains(e.value.value)
        }
        return City(name, done)
    }
}

fun round(game: Game): Game {
    println("\n")
    println("Round ${game.round}")
    println("=============================")

    val monsterTravel = Game(game.monsters.map { it.move(game) }, game.cities, game.round + 1)

    monsterTravel.monsters.forEach { l ->
        println(l)
    }

    return fight(monsterTravel)
}

fun fight(game: Game): Game {

    val monstersToFight = game.monsters.groupBy { y -> y.currentCity }.filter { y -> y.value.size > 1 }

    val citiesToRemove = monstersToFight.keys.toList()
    val monsterToRemove = monstersToFight.values.flatten()

    val survivingMonsters = game.monsters.minus(monsterToRemove)
    val survivingCities = game.cities.minus(citiesToRemove)

    val monsters = survivingMonsters.map { m ->
        m.removeBadDirections(citiesToRemove)
    }
    val cities = survivingCities.map { c ->
        c.removeBadDirections(citiesToRemove)
    }

    monstersToFight.map {
        print("${it.key.name} has been destroyed by ${it.value.map { "monster ${it.id}" }.joinToString(" and ")}! \n")
    }

    return Game(monsters, cities, game.round)
}

fun main(args: Array<String>) {
    println("=============================")
    println("Please enter how many monsters you need: ")
    val noOfMonsters = readLine()?.toInt()
    val initialCities = Initialisation.listOfCities
    val initialGame = Game(
        List(noOfMonsters!!) { index -> index + 1 }.map { x ->
            Monster(
                x,
                initialCities.random(),
                Direction("from home")
            )
        },
        initialCities,
        0
    )

    println("Here is your starting status: ")
    println("==============================")
    initialGame.monsters.forEach { l ->
        println(l)
    }

    return run(round(initialGame))

}

fun run(game: Game) {
    if (game.monsters.size == 2) return println("All monsters have been DESTROYED\nEnd of Game")
    else if (game.monsters.size < 2) return println("Monster Number ${game.monsters.first().id} won!\nEnd of Game")
    else if (game.round > 10000) return println("You've run the game ${game.round} times. End of Game")
    else return run(round(game))
}

object Initialisation {
    val listOfCities: List<City> = this.javaClass.getResource("/map-small.txt").readText().split("\n").map { s ->
        val a = s.split(" ")
        val name = Name(a.get(0))

        val neighbouringCities: Map<Direction, Name> = a.subList(1, a.size).map { x ->
            val splittingDirectionAndName = x.split("=")
            Direction(splittingDirectionAndName.first()) to Name(splittingDirectionAndName.last())
        }.toMap()

        City(name, neighbouringCities)
    }
}

class Direction(val value: String) {
    override fun toString(): String {
        return value
    }
}

class Name(val value: String) {
    override fun toString(): String {
        return value
    }
}
