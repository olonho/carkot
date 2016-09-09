package clInterface.executor

import SonarRequest
import objects.Car
import objects.Environment
import java.net.ConnectException
import java.util.*
import java.util.concurrent.TimeUnit

class Sonar : CommandExecutor {

    private val SONAR_REGEX = Regex("sonar [0-9]{1,10}")

    override fun execute(command: String) {
        if (!SONAR_REGEX.matches(command)) {
            println("incorrect args of command sonar.")
            return
        }
        val id: Int
        try {
            id = command.split(" ")[1].toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            println("error in converting id to int type")
            return
        }
        val car: Car? = synchronized(Environment, {
            Environment.map[id]
        })
        if (car == null) {
            println("car with id=$id not found")
            return
        }
        val angles = getRequiredAngles() ?: return
        try {
            val sonarResult = car.scan(angles, 5, 3, SonarRequest.Smoothing.MEDIAN)
            val distances = sonarResult.get(2, TimeUnit.MINUTES)
            println("angles   : ${Arrays.toString(angles)}")
            println("distances: ${Arrays.toString(distances)}")
        } catch (e: ConnectException) {
            synchronized(Environment, {
                Environment.map.remove(id)
            })
        }
    }

    private fun getRequiredAngles(): IntArray? {
        println("print angles, after printing all angles print done")
        val angles = arrayListOf<Int>()
        while (true) {
            val command = readLine()!!.toLowerCase()
            when (command) {
                "reset" -> return null
                "done" -> {
                    return angles.toIntArray()
                }
                else -> {
                    try {
                        val angle = command.toInt()
                        if (angle < 0 || angle > 180) {
                            println("incorrect angle $angle. angle must be in [0,180] and div on 4")
                        } else {
                            angles.add(angle)
                        }
                    } catch (e: NumberFormatException) {
                        println("error in converting angle to int. try again")
                    }
                }
            }
        }
    }
}