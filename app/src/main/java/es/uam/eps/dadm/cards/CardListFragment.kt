package es.uam.eps.dadm.cards

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
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
import es.uam.eps.dadm.cards.databinding.FragmentCardListBinding
import java.util.concurrent.Executors

class CardListFragment: Fragment() {
    val auth = Firebase.auth
    private lateinit var adapter: CardAdapter
    private val executor = Executors.newSingleThreadExecutor()
    private var reference = FirebaseDatabase
        .getInstance("https://blancamercadomolerocards-default-rtdb.europe-west1.firebasedatabase.app")
        .getReference("cards")
    private var referenceCards = FirebaseDatabase
        .getInstance("https://blancamercadomolerocards-default-rtdb.europe-west1.firebasedatabase.app")
        .getReference("cards")
    // Inicializa cardListViewModel de Firebase
    private val cardListViewModel by lazy {
        ViewModelProvider(this).get(CardListFirebaseViewModel::class.java)
    }
    // Inicializa cardListViewModel de la base de datos de Room
    private val cardListViewModelDatabase by lazy {
        ViewModelProvider(this).get(CardListViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(
            requireActivity(),
            SettingsActivity.getMaximumNumberOfCards(view.context),
            Toast.LENGTH_LONG
        ).show()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentCardListBinding>(
            inflater,
            R.layout.fragment_card_list,
            container,
            false
        )
        SettingsActivity.setLoggedIn(requireContext(), true)
        val args = CardListFragmentArgs.fromBundle(requireArguments())
        cardListViewModelDatabase.loadDeckId(args.deckId)
        adapter = CardAdapter(deckId = args.deckId)
        //cardListViewModel.loadDeckId(args.deckId)
        adapter.data = emptyList()
        binding.cardListRecyclerView.adapter = adapter

        binding.newCardFab.setOnClickListener { view ->
            val card = Card("","", deckId = args.deckId)
            reference.child(card.id).setValue(card)
            executor.execute {
                context?.let { CardDatabase.getInstance(it).cardDao.addCard(card) }
            }
            view.findNavController().navigate(CardListFragmentDirections.actionCardListFragmentToCardEditFragment(card.id ,card.deckId))

        }
        // El método observe se utiliza para registrar un observador de la propiedad cards del ViewModel,
        // y ligar la vida de la observación al ciclo de vida del fragmento.
        //  actualiza la lista de tarjetas del adaptado
        cardListViewModelDatabase.cards.observe(viewLifecycleOwner) {
            adapter.data = it
            adapter.notifyDataSetChanged()
        }
        return binding.root
    }

    // Opciones menú
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
                        var decks: MutableList<Deck> = mutableListOf()
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
                        var cards: MutableList<Card> = mutableListOf()
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
                        snapshot.children.forEach {
                            context?.let { context ->
                                var decks: LiveData<List<Deck>> = CardDatabase.getInstance(context).cardDao.getDecks()
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
                        snapshot.children.forEach {
                            context?.let { context ->
                                var cards: LiveData<List<Card>> = CardDatabase.getInstance(context).cardDao.getCards()
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