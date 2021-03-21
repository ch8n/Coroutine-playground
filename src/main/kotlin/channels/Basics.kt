@file:Suppress("PackageDirectoryMismatch")

package channels.basic

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

@ExperimentalCoroutinesApi
fun main() = runBlocking {
    // official docs :https://kotlinlang.org/docs/channels.html

    //`creating a channel and cancelling it`()
    //`creating producer and consumer pattern`()
    //`creating pipelines pattern`()
    `fan-out channels`()

    Unit
}

suspend fun `creating a channel and cancelling it`() {
    // Channels are hot! ie. active once they are created

    // base example
    val channel = Channel<Int>()
    GlobalScope.launch {
        (1..5).forEach {
            delay(1000)
            channel.send(it)
        }
        channel.close()
    }

    // program will not terminate cause channel will stay active
    // if we dont call channel.close()
    for (item in channel) {
        println(item)
    }

}


@ExperimentalCoroutinesApi
fun `creating producer and consumer pattern`() = runBlocking {

    // producer
    val intProd = produce<Int> {
        (1..5).forEach {
            delay(1000)
            send(it)
        }
    }

    // consumer
    intProd.consumeEach {
        println(it)
    }

}


@ExperimentalCoroutinesApi
fun `creating pipelines pattern`() = runBlocking {

    // producer
    val numberProducer = produce<Int> {
        var count = 0
        while (true) {
            send(count)
            ++count
        }
    }

    val evenNumberProducer = produce<Int> {
        numberProducer.consumeEach {
            if (it % 2 == 0) {
                send(it)
            }
        }
    }

    // consumer
    val firstTenEvens = mutableListOf<Int>()
    for (it in evenNumberProducer) {
        firstTenEvens.add(it)
        if (firstTenEvens.size == 5) {
            println(firstTenEvens)
            coroutineContext.cancelChildren()
        }
    }


}


@ExperimentalCoroutinesApi
fun `fan-out channels`() = runBlocking {

    val numberProducer = produce<Int> {
        var count = 0
        while (true){
            delay(500)
            send(count)
            ++count
        }

    }

    fun doWork(id: Int, channel: ReceiveChannel<Int>) = launch {
        for (value in channel) {
            println("$id : $value")
        }
    }

    numberProducer.consumeEach {
        doWork(it, numberProducer)
    }

    numberProducer.cancel()
}
