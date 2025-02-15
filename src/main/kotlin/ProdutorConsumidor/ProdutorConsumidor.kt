package ProdutorConsumidor

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

val channel = Channel<Int>(5)

class Producer(private val qtProduzir : Int) {

    suspend fun produceItems() {

        for (i in 1..qtProduzir) {

            channel.send(i)

            println("Produzindo... -> :  $i")
            delay(50)
        }

        channel.close()
    }
}

class Consumer {

    suspend fun consumeItems() {

        for (item in channel) {

            println("Consumindo... -> :  $item")
            delay(200)
        }
    }
}

fun main() = runBlocking {

    val producer = Producer(qtProduzir = 15)
    val consumer = Consumer()

    val producerJob = launch { producer.produceItems() }
    val consumerJob = launch { consumer.consumeItems() }

    producerJob.join()
    consumerJob.join()
}