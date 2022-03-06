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

    private val listOfTrains = mutableListOf<Train>()
    private val intermediateStops = mutableMapOf<String, MutableMap<String, Time>>()
    private val eStops = mutableMapOf<String, extremeStations>()
    private var maxTime = Time(0, 0)

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
        return if (!eStops.containsKey(train)) {
            eStops[train] = extremeStations(baseStationName, depart, destination.name, destination.time)
            maxTime = depart
            true
        } else false
    }

    /**
     * Удалить существующий поезд.
     *
     * Если поезда с таким именем нет, следует вернуть false и ничего не изменять в таблице
     *
     * @param train название поезда
     * @return true, если поезд успешно удалён, false, если такой поезд не существует
     */
    fun removeTrain(train: String): Boolean {
        return if (eStops.containsKey(train)) {
            eStops.remove(train)
            true
        } else false
    }

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
        if (eStops.containsKey(train)) {
            if (eStops[train]!!.firstStName != stop.name && eStops[train]!!.lastStName != stop.name
                && stop.time < eStops[train]!!.lastStTime && stop.time > eStops[train]!!.firstStTime) {
                if (!intermediateStops.containsKey(train)) {
                    intermediateStops[train] = mutableMapOf(stop.name to stop.time)
                    maxTime = maxOf(maxTime, stop.time)
                    return true
                }
                if (intermediateStops.containsKey(train) && !intermediateStops[train]!!.containsKey(stop.name)) {
                    intermediateStops[train]!![stop.name] = stop.time
                    maxTime = maxOf(maxTime, stop.time)
                    return true
                }
            }
            if (eStops[train]!!.firstStName == stop.name && stop.time < eStops[train]!!.firstStTime) {
                eStops[train]!!.firstStTime = stop.time
                return false
            }
            if (eStops[train]!!.lastStName == stop.name && stop.time < eStops[train]!!.lastStTime && stop.time > maxTime) {
                eStops[train]!!.lastStTime = stop.time
                return false
            }
            if (intermediateStops.containsKey(train) && intermediateStops[train]!!.containsKey(stop.name)
                && stop.time > eStops[train]!!.firstStTime && stop.time < eStops[train]!!.lastStTime) {
                intermediateStops[train]!![stop.name] = stop.time
                return false
            }
        }
        throw IllegalArgumentException("The entered data is incorrect")
    }

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
        if (intermediateStops.containsKey(train)) {
            return if (intermediateStops[train]!!.containsKey(stopName)) {
                intermediateStops[train]!!.remove(stopName)
                true
            } else false
        }
        throw IllegalArgumentException("No such train")
    }

    /**
     * Вернуть список всех поездов, упорядоченный по времени отправления с baseStationName
     */
    fun trains(): List<Train> {
        val trains = mutableListOf<Train>()
        for (key in eStops.keys)
            trains.add(Train(key, listOf(Stop(eStops[key]!!.firstStName, eStops[key]!!.firstStTime),
                Stop(eStops[key]!!.lastStName, eStops[key]!!.lastStTime))))
        return trains.sortedBy { it.stops[0].time }
    }

    /**
     * Вернуть список всех поездов, отправляющихся не ранее currentTime
     * и имеющих остановку (начальную, промежуточную или конечную) на станции destinationName.
     * Список должен быть упорядочен по времени прибытия на станцию destinationName
     */
    fun trains(currentTime: Time, destinationName: String): List<Train> {
        for (key in intermediateStops.keys) {
            val stops = mutableListOf<Stop>()
            var flag = false
            if (eStops[key]!!.firstStTime >= currentTime) {
                intermediateStops[key]!![eStops[key]!!.lastStName] = eStops[key]!!.lastStTime
                intermediateStops[key]!![eStops[key]!!.firstStName] = eStops[key]!!.firstStTime
                if (eStops[key]!!.lastStName == baseStationName || eStops[key]!!.firstStName == baseStationName) flag = true
                for (nameOfStop in intermediateStops[key]!!.keys) {
                    stops.add(Stop(nameOfStop, intermediateStops[key]!![nameOfStop]!!))
                    if (nameOfStop == baseStationName) flag = true
                }
                if (flag) listOfTrains.add(Train(key, stops.sortedBy { it.time }))
            }
        }
        return listOfTrains.asReversed()
    }


    /**
     * Сравнение на равенство.
     * Расписания считаются одинаковыми, если содержат одинаковый набор поездов,
     * и поезда с тем же именем останавливаются на одинаковых станциях в одинаковое время.
     */
    override fun equals(other: Any?): Boolean =
        other is TrainTimeTable && listOfTrains == other.listOfTrains

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

data class extremeStations(val firstStName: String, var firstStTime: Time, val lastStName: String, var lastStTime: Time)



