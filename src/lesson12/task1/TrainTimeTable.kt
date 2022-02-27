@file:Suppress("UNUSED_PARAMETER")

package lesson12.task1

import java.lang.IllegalArgumentException

/**
 * Класс "расписание поездов".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 16.
 * Объект класса хранит расписание поездов для определённой станции отправления.
 * Для каждого поезда хранится конечная станция и список промежуточных.
 * Поддерживаемые методы:
 * добавить новый поезд, удалить поезд,
 * добавить / удалить промежуточную станцию существующему поезду,
 * поиск поездов по времени.
 *
 * В конструктор передаётся название станции отправления для данного расписания.
 */
class TrainTimeTable(val baseStationName: String) {
//    private val listOfTrains = mutableListOf<Train>()

    private val listOfTrains = mutableMapOf<String, MutableMap<String, Time>>()


    /**
     * Добавить новый поезд.
     *
     * Если поезд с таким именем уже есть, следует вернуть false и ничего не изменять в таблице
     *
     * @param train название поезда
     * @param depart время отправления с baseStationName
     * @param destination конечная станция
     * @return true, если поезд успешно добавлен, false, если такой поезд уже есть
     */
    fun addTrain(train: String, depart: Time, destination: Stop): Boolean {
        return if (!listOfTrains.containsKey(train)) {
            listOfTrains[train] = mutableMapOf(baseStationName to depart, destination.name to destination.time)
            true
        } else false
    }



//        val newTrain = Train(train, Stop(baseStationName, depart), destination)
//        return if (!listOfTrains.any { it.name == newTrain.name }) {
//            listOfTrains.add(newTrain)
//            true
//        } else false
//    }

    /**
     * Удалить существующий поезд.
     *
     * Если поезда с таким именем нет, следует вернуть false и ничего не изменять в таблице
     *
     * @param train название поезда
     * @return true, если поезд успешно удалён, false, если такой поезд не существует
     */
    fun removeTrain(train: String): Boolean {
        return if (listOfTrains.containsKey(train)) {
            listOfTrains.remove(train)
            true
        } else false
    }

//        return if (listOfTrains.any { it.name == train }) {
//            listOfTrains.remove(listOfTrains.find { it.name == train })
//            true
//        } else false
//    }

    /**
     * Добавить/изменить начальную, промежуточную или конечную остановку поезду.
     *
     * Если у поезда ещё нет остановки с названием stop, добавить её и вернуть true.
     * Если stop.name совпадает с baseStationName, изменить время отправления с этой станции и вернуть false.
     * Если stop совпадает с destination данного поезда, изменить время прибытия на неё и вернуть false.
     * Если stop совпадает с одной из промежуточных остановок, изменить время прибытия на неё и вернуть false.
     *
     * Функция должна сохранять инвариант: время прибытия на любую из промежуточных станций
     * должно находиться в интервале между временем отправления с baseStation и временем прибытия в destination,
     * иначе следует бросить исключение IllegalArgumentException.
     * Также, время прибытия на любую из промежуточных станций не должно совпадать с временем прибытия на другую
     * станцию или с временем отправления с baseStation, иначе бросить то же исключение.
     *
     * @param train название поезда
     * @param stop начальная, промежуточная или конечная станция
     * @return true, если поезду была добавлена новая остановка, false, если было изменено время остановки на старой
     */
    fun addStop(train: String, stop: Stop): Boolean {
        if (listOfTrains.containsKey(train)) {
            val stops = listOfTrains[train]!!
            //val intermediateStops = stops - listOf(stops.keys.last(), stops.keys.first())
            if (stops.containsValue(stop.time)) throw IllegalArgumentException("Incorrect time")
            if (!stops.containsKey(stop.name)) {
                stops[stop.name] = stop.time
                listOfTrains[train] = stops.toList().sortedBy { (name, time) -> time }.toMap() as MutableMap<String, Time>
                return true
            }
            if (stops.containsKey(stop.name)) {
                stops[stop.name] = stop.time
                listOfTrains[train] = stops.toList().sortedBy { (name, time) -> time }.toMap() as MutableMap<String, Time>
                return false
            }
        }
        throw IllegalArgumentException("There is no such train")
    }


//        val currentTrain = listOfTrains.find { it.name == train }
//        if (currentTrain != null) {
//            val trainStops = currentTrain.stops.toMutableList()
//
//            if (stop.name != trainStops.first().name && stop.name != trainStops.last().name)
//                if (stop.time > trainStops.last().time || stop.time < trainStops.first().time)
//                    throw IllegalArgumentException("Incorrect time")
//            if (trainStops.any { it.time == stop.time }) throw IllegalArgumentException("The train with such time is already included")
//            if (stop.name == trainStops.first().name )
//                if (trainStops.dropLast(1).drop(1).any { it.time < stop.time }) throw IllegalArgumentException("Incorrect time")
//            if (stop.name == trainStops.last().name)
//                if (trainStops.dropLast(1).drop(1).any { it.time > stop.time }) throw IllegalArgumentException("Incorrect time")
//
//            listOfTrains.remove(currentTrain)
//            if (trainStops.all { it.name != stop.name }) {
//                trainStops.add(1, stop)
//                listOfTrains.add(Train(train, trainStops.sortedBy { it.time }))
//                return true
//            }
//            if (trainStops.any { it.name == stop.name }) {
//                val ind = trainStops.indexOf(trainStops.find { it.name == stop.name })
//                trainStops[ind] = Stop(trainStops.find { it.name == stop.name }!!.name, stop.time)
//                listOfTrains.add(Train(train, trainStops.sortedBy { it.time }))
//                return false
//            }
//        }
//        throw IllegalArgumentException()
//    }

    /**
     * Удалить одну из промежуточных остановок.
     *
     * Если stopName совпадает с именем одной из промежуточных остановок, удалить её и вернуть true.
     * Если у поезда нет такой остановки, или stopName совпадает с начальной или конечной остановкой, вернуть false.
     *
     * @param train название поезда
     * @param stopName название промежуточной остановки
     * @return true, если удаление успешно
     */
    fun removeStop(train: String, stopName: String): Boolean {
        if (listOfTrains.containsKey(train)) {
            val stops = listOfTrains[train]!!
            return if ((stops - listOf(stops.keys.last(), stops.keys.first())).containsKey(stopName)) {
                listOfTrains[train]!!.remove(stopName)
                true
            } else false
        }
        throw IllegalArgumentException("There is no such train")
    }

//        if (listOfTrains.find { it.name == train }!!.stops.dropLast(1).drop(1).any { it.name == stopName }) {
//            listOfTrains.remove(listOfTrains.find { it.stops.dropLast(1).drop(1).any { it.name == stopName }})
//            return true
//        }
//        return false
//    }

    /**
     * Вернуть список всех поездов, упорядоченный по времени отправления с baseStationName
     */
    fun trains(): List<Train> {
        val list = mutableListOf<Train>()
        listOfTrains.
    }


        //listOfTrains.sortedBy { it.stops[0].time }

    /**
     * Вернуть список всех поездов, отправляющихся не ранее currentTime
     * и имеющих остановку (начальную, промежуточную или конечную) на станции destinationName.
     * Список должен быть упорядочен по времени прибытия на станцию destinationName
     */
    fun trains(currentTime: Time, destinationName: String): List<Train> = TODO()
        //listOfTrains.filter { it.stops[0].time >= currentTime && it.stops.any { it.name == destinationName } }.sortedBy { it.stops[0].time }.asReversed()

    /**
     * Сравнение на равенство.
     * Расписания считаются одинаковыми, если содержат одинаковый набор поездов,
     * и поезда с тем же именем останавливаются на одинаковых станциях в одинаковое время.
     */
    override fun equals(other: Any?): Boolean = TODO()


      //  other is TrainTimeTable && listOfTrains == other.listOfTrains

    override fun hashCode(): Int {
        var result = baseStationName.hashCode()
        result = 31 * result + listOfTrains.hashCode()
        return result
    }

}

/**
 * Время (часы, минуты)
 */
data class Time(val hour: Int, val minute: Int) : Comparable<Time> {
    /**
     * Сравнение времён на больше/меньше (согласно контракту compareTo)
     */
    override fun compareTo(other: Time): Int {
        if (hour > other.hour || hour == other.hour && minute > other.minute) return 1
        return if (hour == other.hour && minute == other.minute) 0
        else -1
    }
}

/**
 * Остановка (название, время прибытия)
 */
data class Stop(val name: String, val time: Time)

/**
 * Поезд (имя, список остановок, упорядоченный по времени).
 * Первой идёт начальная остановка, последней конечная.
 */
data class Train(val name: String, val stops: List<Stop>) {
    constructor(name: String, vararg stops: Stop) : this(name, stops.asList())
}
