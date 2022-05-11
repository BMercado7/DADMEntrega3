package es.uam.eps.dadm.cards

import android.app.Application
import es.uam.eps.dadm.cards.database.CardDatabase
import timber.log.Timber
import java.time.LocalDateTime
import java.util.concurrent.Executors

class CardsApplication: Application() {
    private val executor = Executors.newSingleThreadExecutor()
    /*
    init {
        val c1= Card("To wake up", "Despertarse")
        val c2 = Card("To rule out", "Descartar")
        val c3 = Card("To turn down", "Rechazar")
        cards.add(c1)
        cards.add(c2)
        cards.add(c3)
    }*/

    override fun onCreate() {
        super.onCreate()
        // Inicializa la base de datos
        val cardDatabase = CardDatabase.getInstance(applicationContext)

        // Añade unas tarjetas mediante los métodos del DAO
        executor.execute {
            //cardDatabase.cardDao.deleteCard( Card("J'ai faim", "Tengo hambre", deckId = 2))
            /*
            cardDatabase.cardDao.addDeck(Deck(1, "English"))
            cardDatabase.cardDao.addDeck(Deck(2, "French"))

            cardDatabase.cardDao.addCard(
                Card("To wake up", "Despertarse", deckId = 1)
            )
            cardDatabase.cardDao.addCard(
                Card("To rule out", "Descartar", deckId = 1)
            )
            cardDatabase.cardDao.addCard(
                Card("To turn down", "Rechazar", deckId = 1)
            )
            cardDatabase.cardDao.addCard(
                Card("La voiture", "El coche", deckId = 2)
            )
            cardDatabase.cardDao.addCard(
                Card("J'ai faim", "Tengo hambre", deckId = 2)
            )
            */

        }
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        fun numberOfDueCards() = cards.filter { it.isDue(LocalDateTime.now()) }.size
        fun getCard(id: String) = cards.find { it.id == id }
        fun addCard(card: Card) { cards.add(card) }

        //fun easyCards() = cards.filter { it.quality == 5 }.size

        //fun doubtCards() = cards.filter { it.quality == 3 }.size

        //fun hardCards() = cards.filter { it.quality == 0 }.size

        //fun addDeck(deck: Deck) {
        //    decks.add(deck)
        //}
        /*
        fun deleteCardsFromDeck(deckId: String){
            cards.forEach {
                if (deckId == it.id)
                    cards.remove(it)
            }
        }
        */
        var cards: MutableList<Card> = mutableListOf()
        var decks: MutableList<Deck> = mutableListOf()
    }
}