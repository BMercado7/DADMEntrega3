package es.uam.eps.dadm.cards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import es.uam.eps.dadm.cards.databinding.ListItemDeckBinding


class DeckAdapter : RecyclerView.Adapter<DeckAdapter.DeckHolder>() {
    private lateinit var binding: ListItemDeckBinding
    var data =  listOf<DeckWithCards>()


    inner class DeckHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var local = binding
        private lateinit var deckWithCards: DeckWithCards
        //lateinit var deck: Deck
        fun bind(deckCards: DeckWithCards) {
            this.deckWithCards = deckCards
            //Fragmento que contiene la vista RecyclerView de la lista de tarjetas del mazo
            binding.deck = deckCards.deck
            local.listItem.setOnClickListener {
                it.findNavController()
                    .navigate(DeckListFragmentDirections.
                        actionDeckListFragmentToCardListFragment(deckWithCards.deck.deckId))
            }
        }
        init {
            local.listButtonEdit.setOnClickListener {
                it.findNavController()
                    .navigate(
                        DeckListFragmentDirections
                            .actionDeckListFragmentToDeckEditFragment(deckWithCards.deck.deckId)
                    )
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeckHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = ListItemDeckBinding.inflate(layoutInflater, parent, false)
        return DeckHolder(binding.root)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: DeckHolder, position: Int) {
        holder.bind(data[position])
    }
}