package es.uam.eps.dadm.cards

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.database.FirebaseDatabase
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.FragmentCardEditBinding
import java.util.concurrent.Executors

class CardEditFragment : Fragment() {
    private val executor = Executors.newSingleThreadExecutor()
    lateinit var binding: FragmentCardEditBinding
    private var reference = FirebaseDatabase
        .getInstance("https://blancamercadomolerocards-default-rtdb.europe-west1.firebasedatabase.app")
        .getReference("cards")

    lateinit var card: Card
    var question: String? = null
    var answer: String? = null

    private val viewModel by lazy {
        ViewModelProvider(this).get(CardEditViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_card_edit,
            container,
            false
        )
        val args = CardEditFragmentArgs.fromBundle(requireArguments())
        viewModel.loadCardId(args.cardId)
        viewModel.card.observe(viewLifecycleOwner) {
            if(it != null) {
                card = it
                binding.card = card
                question = card.question
                answer = card.answer
            }
        }
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        val questionTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                card.question = s.toString()
            }
        }

        val answerTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                card.answer = s.toString()
            }
        }

        // Ajusta el escuchador al elemento
        binding.questionEditText.addTextChangedListener(questionTextWatcher)
        binding.answerEditText.addTextChangedListener(answerTextWatcher)
        //val args = CardEditFragmentArgs.fromBundle(requireArguments())

        // Añade escuchadores OnClickListener para los botones
        binding.acceptCardEditButton.setOnClickListener {
            executor.execute {
                context?.let { card.let { it2 -> CardDatabase.getInstance(it).cardDao.update(it2) } }
            }
            //reference.child(card.id).setValue(card)
            it.findNavController()
                .navigate(CardEditFragmentDirections
                    .actionCardEditFragmentToCardListFragment(card.deckId))
        }
        binding.cancelCardEditButton.setOnClickListener { view ->
            if (card.question == "" || card.answer == ""){
                reference.child(card.id).removeValue()
                executor.execute {
                    context?.let { CardDatabase.getInstance(it).cardDao.deleteCard(card) }
                }
            }
            view.findNavController()
                .navigate(CardEditFragmentDirections
                    .actionCardEditFragmentToCardListFragment(card.deckId))
        }

        binding.deleteCardEditButton.setOnClickListener { view ->
            val deckId = card.deckId
            executor.execute {
                context?.let { CardDatabase.getInstance(it).cardDao.deleteCard(card) }
            }
            //reference.child(card!!.id).removeValue()
            view.findNavController()
                .navigate(CardEditFragmentDirections
                    .actionCardEditFragmentToCardListFragment(deckId))
        }

        if (question != "") binding.detailsButton.visibility = View.VISIBLE
        binding.detailsButton.setOnClickListener {
            if(binding.cardDetails.visibility == View.VISIBLE) binding.cardDetails.visibility = View.INVISIBLE
            else binding.cardDetails.visibility = View.VISIBLE

            if(binding.cardDetailsText.visibility == View.VISIBLE) binding.cardDetailsText.visibility = View.INVISIBLE
            else binding.cardDetailsText.visibility = View.VISIBLE
        }

    }
}