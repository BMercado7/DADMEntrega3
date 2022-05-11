package es.uam.eps.dadm.cards.database

import androidx.lifecycle.LiveData
import androidx.room.*
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.Deck
import es.uam.eps.dadm.cards.DeckWithCards

@Dao
interface CardDao {
    @Query("SELECT * FROM cards_table")
    fun getCards(): LiveData<List<Card>>

    @Query("SELECT * FROM decks_table")
    fun getDecks(): LiveData<List<Deck>>

    @Query("SELECT * FROM cards_table WHERE id = :id")
    fun getCard(id: String): LiveData<Card?>

    @Query("SELECT * FROM decks_table WHERE deckId = :id")
    fun getDeck(id: String): LiveData<Deck?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCard(card: Card)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDecks(decks : List<Deck>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCards(cards : List<Card>)

    @Update
    fun update(card: Card)

    @Update
    fun updateDeck(deck: Deck)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDeck(deck: Deck)

    @Delete
    fun deleteCard(card: Card)

    @Delete
    fun deleteDeck(deck: Deck)

    @Query("DELETE FROM decks_table")
    fun deleteDecks()

    @Query("DELETE FROM cards_table")
    fun deleteCards()

    @Transaction
    @Query("SELECT * FROM decks_table")
    fun getDecksWithCards(): LiveData<List<DeckWithCards>>

    // Si pongo List no funciona
    /*@Transaction
    @Query("SELECT * FROM decks_table WHERE deckId = :deckId")
    fun getDeckWithCards(deckId: String): LiveData<List<DeckWithCards>>*/
    @Transaction
    @Query("SELECT * FROM decks_table WHERE deckId = :deckId")
    fun getDeckWithCards(deckId: String): LiveData<DeckWithCards>

    @Query("SELECT * FROM decks_table WHERE deckId = :deckId AND user = :user")
    fun getDeckWithCardsUser(deckId: String, user: String): LiveData<Deck>

    @Transaction
    @Query("SELECT * FROM decks_table WHERE deckId = :deckId")
    fun getDeckWithCardList(deckId: String): LiveData<List<DeckWithCards>>

    @Query("SELECT * FROM cards_table WHERE deckId = :id")
    fun getCardsFromDeckId(id: String): LiveData<List<Card>>

    @Transaction
    fun downloadDecks(decks :List<Deck>){
        //deleteDecks()
        addDecks(decks)
    }

    @Transaction
    fun downloadCards(cards :List<Card>){
        //deleteCards()
        addCards(cards)
    }
}


