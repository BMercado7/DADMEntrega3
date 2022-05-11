package es.uam.eps.dadm.cards

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.FragmentDeckListBinding
import java.util.concurrent.Executors

class DeckListFragment: Fragment() {
    private lateinit var adapter: DeckAdapter
    private val executor = Executors.newSingleThreadExecutor()
    private var reference = FirebaseDatabase
        .getInstance("https://blancamercadomolerocards-default-rtdb.europe-west1.firebasedatabase.app")
        .getReference("decks")

    private var referenceCards = FirebaseDatabase
        .getInstance("https://blancamercadomolerocards-default-rtdb.europe-west1.firebasedatabase.app")
        .getReference("cards")

    // Inicializa deckListViewModel
    private val deckListViewModel by lazy {
        ViewModelProvider(this).get(DeckListViewModel::class.java)
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentDeckListBinding>(
            inflater,
            R.layout.fragment_deck_list,
            container,
            false
        )
        adapter = DeckAdapter()
        adapter.data = emptyList()
        binding.deckListRecyclerView.adapter = adapter

        // Creación de Deck
        binding.newDeckFab.setOnClickListener { view ->
            val deck = Deck()
            executor.execute {
                context?.let { CardDatabase.getInstance(it).cardDao.addDeck(deck)}
            }
            reference.child(deck.deckId).setValue(deck)
            view.findNavController().navigate(DeckListFragmentDirections.actionDeckListFragmentToDeckEditFragment(
                deck.deckId
            ))
        }
        deckListViewModel.decks.observe(viewLifecycleOwner) { list ->
            //var listaMazos: MutableList<Deck> = mutableListOf()
            //list.forEach { listaMazos.add(it.deck) }
            adapter.data = list
            adapter.notifyDataSetChanged()
        }
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_card_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }
            // Volvemos a la pantalla de título
            R.id.log_out -> {
                Firebase.auth.signOut()
                val intent = Intent(context, TitleActivity::class.java)
                startActivity(intent)
            }
            R.id.download -> {
                reference.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val decks: MutableList<Deck> = mutableListOf()
                        snapshot.children.forEach { value ->
                             context?.let {
                                value.getValue(Deck::class.java)?.let { deck ->
                                    executor.execute {
                                        CardDatabase.getInstance(it).cardDao.getDeck(deck.deckId)
                                        decks.add(deck)
                                    }

                                }
                             }
                        }

                        context?.let {
                            executor.execute {
                                CardDatabase.getInstance(it).cardDao.downloadDecks(decks)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                // Para las cartas
                referenceCards.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val cards: MutableList<Card> = mutableListOf()
                        snapshot.children.forEach { value ->
                            context?.let {
                                value.getValue(Card::class.java)?.let { card ->
                                    executor.execute {
                                        CardDatabase.getInstance(it).cardDao.getCard(card.id)
                                        cards.add(card)
                                    }

                                }
                            }
                        }
                        context?.let {
                            executor.execute {
                                CardDatabase.getInstance(it).cardDao.downloadCards(cards)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            }

            R.id.upload -> {
                // Mazos
                reference.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { _ ->
                            context?.let { context ->
                                val decks: LiveData<List<Deck>> = CardDatabase.getInstance(context).cardDao.getDecks()
                                decks.observe(viewLifecycleOwner) {
                                    for (deck in it) {
                                        reference.child(deck.deckId).setValue(deck)
                                    }
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
                // Cartas
                referenceCards.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { _ ->
                            context?.let { context ->
                                val cards: LiveData<List<Card>> = CardDatabase.getInstance(context).cardDao.getCards()
                                cards.observe(viewLifecycleOwner) {
                                    for (card in it) {
                                        referenceCards.child(card.id).setValue(card)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        }
        return true
    }

}
