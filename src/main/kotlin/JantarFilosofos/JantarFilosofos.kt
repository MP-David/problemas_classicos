import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex

const val NUM_FILOSOFOS = 5
const val TIMEOUT_MS = 1000L

val garfos = Array(NUM_FILOSOFOS) { Mutex() }
//MUTEX
//it’s a lock that ensures that only one coroutine can access a particular resource or execute a particular task at a given time

suspend fun filosofar(idFilosofo: Int) {

    while (true) {

        println("PENSANDO >>>>    Filósofo $idFilosofo está pensando. \uD83E\uDD14")
        delay((1000..2000).random().toLong())

        val (garfoMenorIndex, garfoMaiorIndex) = setGarfos(idFilosofo)
        var comeu = false

        while (!comeu) {

            if (garfos[garfoMenorIndex].tryLock(TIMEOUT_MS)) {

                println("LOCK     >>>>    Filósofo $idFilosofo pegou o garfo ${garfoMenorIndex}. \uD83D\uDD12")

                if (garfos[garfoMaiorIndex].tryLock(TIMEOUT_MS)) {

                    println("LOCK     >>>>    Filósofo $idFilosofo pegou o garfo ${garfoMaiorIndex}. \uD83D\uDD12")
                    println("COMENDO  >>>>    Filósofo $idFilosofo está comendo. \uD83E\uDD69")
                    delay((500..1000).random().toLong())

                    unLockGarfos(garfoMaiorIndex, idFilosofo, garfoMenorIndex)
                    comeu = true
                    println("FINISH   >>>>    Filósofo $idFilosofo terminou de comer. \uD83D\uDEA9")

                } else {

                    garfos[garfoMenorIndex].unlock()
                    println("ERROR    >>>>    Filósofo $idFilosofo não conseguiu pegar o garfo da ${garfoMaiorIndex} e largou o garfo ${garfoMenorIndex}. ❌")
                }

            } else {

                println("ERROR    >>>>    Filósofo $idFilosofo não conseguiu pegar o garfo ${garfoMenorIndex}. ❌")
            }

            delay(500)
        }

    }
}

//Sempre tentar pegar o garfo de menor índice primeiro
// Com o intuito do último filosofo sempre tentar pegar o garfo do primerio,
// assim nunca tendo todos os filosos segurando um garfo
private fun setGarfos(id: Int): Pair<Int, Int> {

    val garfoEsquerdo = id
    // o resultado x + 1 mod n, sendo n um número impar sempre será X + 1,
    // com exceção do cenário onde x +1 = n, onde o mod será 0 ou no cenário onde x = 0
    val garfoDireito = (id + 1) % NUM_FILOSOFOS

    return if (garfoEsquerdo < garfoDireito) {

        Pair(garfoEsquerdo, garfoDireito)
    } else {

        Pair(garfoDireito, garfoEsquerdo)
    }
}


private fun unLockGarfos(garfoDireito: Int, id: Int, garfoEsquerdo: Int) {

    garfos[garfoDireito].unlock()
    println("UNLOCK   >>>>    Filósofo $id largou o garfo ${garfoDireito}. \uD83C\uDF74")
    garfos[garfoEsquerdo].unlock()
    println("UNLOCK   >>>>    Filósofo $id largou o garfo ${garfoEsquerdo}. \uD83C\uDF74")
}

fun main() = runBlocking {

    val filosofos = List(NUM_FILOSOFOS) { id -> launch { filosofar(id) } }

    filosofos.forEach { it.join() }
}