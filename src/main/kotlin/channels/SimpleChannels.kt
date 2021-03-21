package channels

import com.github.javafaker.Faker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


typealias Weather = String

@ExperimentalTime
@ExperimentalCoroutinesApi
fun main() = runBlocking {
    example1()
    example2()
    example3()
    example4()
}

@ExperimentalTime
suspend fun example1() {
    val (_, duration) = measureTimedValue {
        val list = foo()
        list.forEach {
            println(it)
        }
    }
    println("========SEQ-OUTPUT=========")
    println(duration)
}

@ExperimentalTime
@ExperimentalCoroutinesApi
suspend fun example2() = runBlocking {
    val (_, duration) = measureTimedValue {
        val producer = produce<Weather> {
            (1..5).map { send(getWeatherData()) }
            close()
        }
        producer.consumeEach {
            println(it)
        }
    }
    println("========CHANNEL-OUTPUT=========")
    println(duration)
}

@ExperimentalTime
@ExperimentalCoroutinesApi
suspend fun example3() = runBlocking {
    val (_, duration) = measureTimedValue {
        val producer = flow<Weather> {
            (1..5).map { emit(getWeatherData()) }
        }
        producer.collect {
            println(it)
        }
    }
    println("========FLOW-OUTPUT=========")
    println(duration)
}

@ExperimentalTime
@ExperimentalCoroutinesApi
suspend fun example4() = runBlocking {
    val (_, duration) = measureTimedValue {
        val producer = flow<Weather> {
            (1..5).map { emit(getWeatherData()) }
        }
        producer.buffer().collect {
            println(it)
        }
    }
    println("========FLOW-OUTPUT=========")
    println(duration)
}


suspend fun foo(): List<Weather> {
    return (1..5).map {
        getWeatherData()
    }
}

suspend fun getWeatherData(): Weather {
    delay(1000L)
    return Faker.instance().weather().run { "${temperatureCelsius()} : ${description()}" }
}

