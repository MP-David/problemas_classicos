package JantarFilosofos

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

const val NUM_FILOSOFOS = 5

val garfos = Array(NUM_FILOSOFOS) { Mutex() } //Só um filoso pode acessar por vem

suspend fun filosofar(id: Int) {

    while (true) {

        println("Filósofo $id está pensando.")
        delay((1000..2000).random().toLong())

        val garfoEsquerdo = id
        //Identificação dos Filósofos: Cada filósofo é identificado por um número, começando de 0 até o número total de filósofos menos um. Por exemplo, se temos 5 filósofos, eles são numerados de 0 a 4.
        //Garfo à Esquerda: O garfo à esquerda de um filósofo é simplesmente o garfo correspondente ao seu próprio número. Por exemplo, o filósofo 2 pega o garfo 2.
        //Garfo à Direita: Para calcular o garfo à direita, usamos a fórmula (id + 1) % NUM_FILOSOFOS.
        //Aqui, id é o número do filósofo e NUM_FILOSOFOS é o total de filósofos.
        //O operador % (módulo) garante que, se o filósofo for o último (por exemplo, 4), ele "volta" para o primeiro filósofo (0). Assim, o filósofo 4 pega o garfo 0 como seu garfo à direita.
        val garfoDireito = (id + 1) % NUM_FILOSOFOS

        //Travo o garfo
        garfos[garfoEsquerdo].withLock {
            println("Filósofo $id pegou o garfo esquerdo.")

            garfos[garfoDireito].withLock {
                println("Filósofo $id pegou o garfo direito.")
                println("Filósofo $id está comendo.")
                delay((500..1000).random().toLong())
            }
            println("Filósofo $id largou o garfo direito.")
        }

        //Não dou o unlock no mutex, pois quando acaba a execução da função ele já da o unlick
        println("Filósofo $id largou o garfo esquerdo.")
    }
}

fun main() = runBlocking {

    val filosofos = List(NUM_FILOSOFOS) { id ->
        launch {
            filosofar(id)
        }
    }

    filosofos.forEach { it.join() }
}