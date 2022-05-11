package es.uam.eps.dadm.cards.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.Deck

@Database(entities = [Card::class, Deck::class], version = 17, exportSchema = false)
abstract class CardDatabase : RoomDatabase() {
    abstract val cardDao: CardDao

    companion object {
        @Volatile
        private var INSTANCE: CardDatabase? = null

        //  devuelve la base de datos
        fun getInstance(context: Context): CardDatabase {
            var instance = INSTANCE

            if (instance == null) {
                instance = Room.databaseBuilder(
                    // l contexto de la aplicación
                    context.applicationContext,
                    //  clase de la base de datos
                    CardDatabase::class.java,
                    // nombre de la base de datos
                    "cards_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
            }
            return instance
        }
    }
}

