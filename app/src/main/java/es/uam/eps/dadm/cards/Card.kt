package es.uam.eps.dadm.cards
import androidx.room.ColumnInfo
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToLong
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards_table")
open class Card(
    @ColumnInfo(name = "card_question")
    var question: String,
    var answer: String,
    var date: String = LocalDateTime.now().toString(),
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var deckId: String = ""

){
    var nextPracticeDate: String = date
    var easiness: Double = 2.5
    var answered : Boolean = false
    var quality: Int = -1
    var repetitions: Int = 0
    var interval: Long = 1L
    var deckName : String? = null

        constructor() : this(
            "question",
            "answer",
            LocalDateTime.now().toString(),
            UUID.randomUUID().toString(),
            ""
        )
        constructor(question:String, answer: String, nextPracticeDate: String) : this(question, answer, LocalDateTime.now().toString(), "0"){
            this.nextPracticeDate = nextPracticeDate
        }
        override fun toString() = "card | $question | $answer | $date | $nextPracticeDate"

        fun update(currentDate: LocalDateTime){
            easiness = maxOf(1.3, easiness + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02 ))
            repetitions = if(quality < 3) 0
            else repetitions + 1

            interval = when(repetitions) {
                0, 1 -> 1
                2 -> 6
                else -> (interval * easiness).roundToLong()
            }

            nextPracticeDate = currentDate.plusDays(interval).toLocalDate().toString()
        }

        fun details() = "easiness = ${"%.2f".format(easiness)} , rep = $repetitions \n int = $interval , next = ${nextPracticeDate.substring(0,10)}, deck = $deckName"

        fun isDue(date: LocalDateTime): Boolean {
            return date.toString().substring(0,10) >= this.nextPracticeDate.substring(0,10)
        }


    }
