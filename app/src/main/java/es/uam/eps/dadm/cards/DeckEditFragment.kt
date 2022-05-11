package es.uam.eps.dadm.cards

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.FragmentDeckEditBinding
import java.util.concurrent.Executors

class DeckEditFragment : Fragment() {
    private val executor = Executors.newSingleThreadExecutor()
    lateinit var binding: FragmentDeckEditBinding
    lateinit var deck: Deck
    private lateinit var deckName: String
    private val viewModel by lazy {
        ViewModelProvider(this).get(DeckEditViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_deck_edit,
            container,
            false
        )

        val args = DeckEditFragmentArgs.fromBundle(requireArguments())
        // El ID lo coge bien
        viewModel.loadDeckId(args.deckId)
        // TODO: da error al  borrar, creo que es por usar DeckWithCard que falla la base de datos?
        // TODO: habría que usar Deck?
        viewModel.deck.observe(viewLifecycleOwner) {
            if(it != null){
                deck = it
            }
            binding.deck = deck
            deckName = deck.name
        }
        // Devuelve la vista root de binding
        return binding.root
    }

    // unit 25/11
    override fun onStart() {
        super.onStart()
        // Obtenemos los datos originales del mazo
        //deckName = deckWithCards.deck.name
        val deckNameTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                deck.name = s.toString()
            }
        }
        // Ajusta el escuchador al elemento
        binding.deckEditName.addTextChangedListener(deckNameTextWatcher)

        // Añade escuchadores OnClickListener para los botones
        binding.acceptDeckEditButton.setOnClickListener { view ->
            executor.execute {
                context?.let { CardDatabase.getInstance(it).cardDao.updateDeck(deck) }
            }
            //reference.child(deck.deckId).setValue(deck)
            view.findNavController().navigate(R.id.action_deckEditFragment_to_deckListFragment)
        }
        binding.cancelDeckEditButton.setOnClickListener { view ->
            deck.name = deckName

            //reference.child(deckWithCards.deck.deckId.toString()).removeValue()
            //executor.execute {
            //    context?.let { CardDatabase.getInstance(it).cardDao.deleteDeck(deckWithCards.deck) }
            //}
            view.findNavController().navigate(R.id.action_deckEditFragment_to_deckListFragment)
        }

        binding.deleteDeckEditButton.setOnClickListener { view ->
            //reference.child(deck.deckId).removeValue()
            executor.execute {
                context?.let { CardDatabase.getInstance(it).cardDao.deleteDeck(deck) }
            }
            view.findNavController().navigate(R.id.action_deckEditFragment_to_deckListFragment)
        }
    }
}