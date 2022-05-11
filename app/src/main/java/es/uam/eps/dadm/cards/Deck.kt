package es.uam.eps.dadm.cards

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Entity(tableName = "decks_table")
data class Deck(
    @PrimaryKey
    val deckId: String,
    var name: String,
    var user: String? = FirebaseAuth.getInstance().currentUser?.email
){
    constructor() : this(
        UUID.randomUUID().toString(),
        ""
    )
}