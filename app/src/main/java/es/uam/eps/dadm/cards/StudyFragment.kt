package es.uam.eps.dadm.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import es.uam.eps.dadm.cards.databinding.FragmentStudyBinding

class StudyFragment : Fragment() {
    lateinit var binding: FragmentStudyBinding
    private val viewModel: StudyViewModel by lazy {
        ViewModelProvider(this).get(StudyViewModel::class.java)
    }

    private var listener: View.OnClickListener = View.OnClickListener { v ->
        val quality = when (v?.id) {
            // Asigna a quality el valor 0, 3 o 5,
            // dependiendo del botón que se haya pulsado
            R.id.easy_button -> 5
            R.id.doubt_button -> 3
            R.id.difficult_button -> 0
            else -> throw Exception("Unavailable quality")
        }
        // Llama al método update de viewModel
        viewModel.update(quality)
        // Si la propiedad card de viewModel es null
        // informa al usuario mediante un Toast de que
        // no quedan tarjetas que repasar
        if (viewModel.card == null) {
            Toast.makeText(activity, R.string.no_more_cards, Toast.LENGTH_LONG).show()
        }
        binding.invalidateAll()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_study,
            container,
            false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.dueCard.observe(viewLifecycleOwner) {
            viewModel.card = it
            binding.invalidateAll()
        }


        binding.apply {
            answerButton.setOnClickListener {
                viewModel?.card?.answered = true
                binding.invalidateAll()
            }

            boardView?.display
            // Ajusta el escuchador listener a los botones de dificultad
            easyButton.setOnClickListener(listener)
            doubtButton.setOnClickListener(listener)
            difficultButton.setOnClickListener(listener)
        }
        return binding.root
    }

}


