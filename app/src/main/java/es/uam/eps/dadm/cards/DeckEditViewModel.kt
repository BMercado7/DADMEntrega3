package es.uam.eps.dadm.cards

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.auth.FirebaseAuth
import es.uam.eps.dadm.cards.database.CardDatabase

class DeckEditViewModel(application: Application): AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val deckId = MutableLiveData<String>()

    val deck: LiveData<Deck> = Transformations.switchMap(deckId) {
        FirebaseAuth.getInstance().currentUser?.email?.let { it1 ->
            CardDatabase.getInstance(context).cardDao
                .getDeckWithCardsUser(deckId = it, user = it1)
        }
    }

    fun loadDeckId(id: String) {
        deckId.value = id
    }
}

