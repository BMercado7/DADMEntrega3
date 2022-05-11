package es.uam.eps.dadm.cards

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import es.uam.eps.dadm.cards.database.CardDatabase

class StatisticsViewModel(application: Application): AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val deckId = MutableLiveData<String>()

    var cards: LiveData<List<Card>> =
        CardDatabase.getInstance(context).cardDao.getCards()
    val decks: LiveData<List<DeckWithCards>> =
        CardDatabase.getInstance(context).cardDao.getDecksWithCards()
    val deckWithCards: LiveData<List<DeckWithCards>> = Transformations.switchMap(deckId) {
        CardDatabase.getInstance(context).cardDao.getDeckWithCardList(it)
    }
    fun loadDeckId(id: String) {
        deckId.value = id
    }

    // Tarjetas Estudiadas:
    val nCardStudied: LiveData<Int> =
        Transformations.switchMap(cards, ::nCardStudied)
    private fun nCardStudied(cards: List<Card>): MutableLiveData<Int> {
        var nCardStudied = 0
        for (card in cards) {
            nCardStudied += card.repetitions
        }
        return MutableLiveData<Int>().apply {
            value = nCardStudied
        }
    }

}